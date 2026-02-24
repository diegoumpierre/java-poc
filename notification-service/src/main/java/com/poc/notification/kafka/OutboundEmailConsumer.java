package com.poc.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.notification.domain.ConfigType;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.repository.EmailHistoryRepository;
import com.poc.notification.service.DirectEmailService;
import com.poc.notification.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboundEmailConsumer {

    private final DirectEmailService directEmailService;
    private final RateLimitService rateLimitService;
    private final EmailHistoryRepository emailHistoryRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.outbound-queue}",
                   groupId = "${app.kafka.consumer.group-id}")
    public void consume(String message, Acknowledgment ack) {
        try {
            EmailHistory msg = objectMapper.readValue(message, EmailHistory.class);

            // Re-fetch from DB to get latest state
            EmailHistory dbMsg = emailHistoryRepository.findByMessageId(msg.getMessageId())
                    .orElse(null);
            if (dbMsg == null) {
                log.warn("Message not found in DB: {}", msg.getMessageId());
                ack.acknowledge();
                return;
            }

            // Skip if already sent or dead
            if ("SENT".equals(dbMsg.getStatus()) || "DEAD".equals(dbMsg.getStatus())) {
                ack.acknowledge();
                return;
            }

            String configType = dbMsg.getConfigType() != null ? dbMsg.getConfigType() : ConfigType.ATENDIMENTO.name();

            // Check rate limit
            if (!rateLimitService.canSend(dbMsg.getTenantId(), configType)) {
                dbMsg.setStatus("QUEUED");
                dbMsg.setUpdatedAt(Instant.now());
                emailHistoryRepository.save(dbMsg);
                log.debug("Rate limited, email queued for later: {}", dbMsg.getMessageId());
                ack.acknowledge();
                return;
            }

            directEmailService.sendEmail(dbMsg);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process outbound email: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
