package com.poc.tenant.kafka;

import com.poc.tenant.domain.OutboxEvent;
import com.poc.tenant.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Polls the outbox table for pending events and publishes them to Kafka.
 * Part of the Transactional Outbox Pattern to guarantee at-least-once delivery.
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Polls every 5 seconds for pending outbox events and publishes to Kafka.
     */
    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:5000}")
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxRepository.findPendingEvents(50);
        if (pending.isEmpty()) return;

        log.debug("[Outbox] Found {} pending events to publish", pending.size());

        for (OutboxEvent event : pending) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getEventKey(), event.getPayload())
                        .get(); // synchronous send for reliability

                outboxRepository.markAsSent(event.getId());
                log.debug("[Outbox] Published event {} to topic {}", event.getId(), event.getTopic());

            } catch (Exception e) {
                handlePublishFailure(event, e);
            }
        }
    }

    private void handlePublishFailure(OutboxEvent event, Exception e) {
        String errorMsg = e.getMessage() != null ? e.getMessage().substring(0, Math.min(e.getMessage().length(), 2000)) : "Unknown error";

        if (event.getRetryCount() + 1 >= event.getMaxRetries()) {
            outboxRepository.markAsFailed(event.getId(), errorMsg);
            log.error("[Outbox] Event {} FAILED permanently after {} retries. Topic: {}, Error: {}",
                    event.getId(), event.getMaxRetries(), event.getTopic(), errorMsg);
        } else {
            outboxRepository.incrementRetry(event.getId(), errorMsg);
            log.warn("[Outbox] Event {} failed (retry {}/{}). Topic: {}, Error: {}",
                    event.getId(), event.getRetryCount() + 1, event.getMaxRetries(),
                    event.getTopic(), errorMsg);
        }
    }

    /**
     * Cleanup old sent events (older than 7 days). Runs daily at 3 AM.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOldEvents() {
        outboxRepository.cleanupOldEvents();
        log.info("[Outbox] Cleaned up old sent events");
    }
}
