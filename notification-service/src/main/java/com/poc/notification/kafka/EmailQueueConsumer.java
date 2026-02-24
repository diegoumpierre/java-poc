package com.poc.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.EmailStatus;
import com.poc.notification.repository.EmailHistoryRepository;
import com.poc.notification.service.TemplateEmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailQueueConsumer {

    private static final int MAX_RETRIES = 3;

    private final TemplateEmailSenderService templateEmailSenderService;
    private final EmailHistoryRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.email-queue}", groupId = "${app.kafka.consumer.group-id}")
    public void consume(String message, Acknowledgment ack) {
        EmailHistory history = null;
        try {
            // Parse the message to get the messageId
            EmailHistory parsed = objectMapper.readValue(message, EmailHistory.class);
            final String messageId = parsed.getMessageId();
            log.info("Processing email: {} to {}", messageId, parsed.getRecipient());

            // Fetch fresh from DB (may have been updated)
            history = repository.findByMessageId(messageId)
                    .orElseThrow(() -> new RuntimeException("Email not found: " + messageId));

            // Update status to SENDING
            history.setStatus(EmailStatus.SENDING.name());
            history.setUpdatedAt(Instant.now());
            repository.save(history);

            // Send the email
            templateEmailSenderService.send(history);

            // Update status to SENT
            history.setStatus(EmailStatus.SENT.name());
            history.setSentAt(Instant.now());
            history.setUpdatedAt(Instant.now());
            repository.save(history);

            log.info("Email sent successfully: {} to {}", history.getMessageId(), history.getRecipient());
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process email: {}", e.getMessage());
            handleFailure(history, e);
            ack.acknowledge(); // Ack to avoid reprocessing, retry via scheduler
        }
    }

    private void handleFailure(EmailHistory history, Exception e) {
        if (history == null) {
            log.error("Cannot handle failure: history is null");
            return;
        }

        try {
            // Refresh from DB
            history = repository.findByMessageId(history.getMessageId()).orElse(history);

            history.setRetryCount(history.getRetryCount() + 1);
            history.setErrorMessage(e.getMessage());
            history.setUpdatedAt(Instant.now());

            if (history.getRetryCount() >= MAX_RETRIES) {
                history.setStatus(EmailStatus.DEAD.name());
                log.error("Email {} marked as DEAD after {} retries", history.getMessageId(), MAX_RETRIES);
            } else {
                history.setStatus(EmailStatus.FAILED.name());
                log.warn("Email {} marked as FAILED (retry {} of {})",
                        history.getMessageId(), history.getRetryCount(), MAX_RETRIES);
            }

            repository.save(history);
        } catch (Exception ex) {
            log.error("Failed to handle failure for email: {}", ex.getMessage());
        }
    }
}
