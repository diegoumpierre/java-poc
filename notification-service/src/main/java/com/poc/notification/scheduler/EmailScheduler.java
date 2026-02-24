package com.poc.notification.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.EmailStatus;
import com.poc.notification.repository.EmailHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailScheduler {

    private static final int MAX_RETRIES = 3;

    private final EmailHistoryRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.email-queue}")
    private String emailQueueTopic;

    /**
     * Process scheduled emails - every minute
     */
    @Scheduled(fixedRateString = "${app.scheduler.scheduled-rate:60000}")
    @Transactional
    public void processScheduledEmails() {
        List<EmailHistory> scheduled = repository.findByStatusAndScheduledAtBefore(
                EmailStatus.PENDING.name(),
                Instant.now()
        );

        if (scheduled.isEmpty()) {
            return;
        }

        log.info("Processing {} scheduled emails", scheduled.size());

        for (EmailHistory email : scheduled) {
            try {
                email.setStatus(EmailStatus.QUEUED.name());
                email.setUpdatedAt(Instant.now());
                repository.save(email);
                kafkaTemplate.send(emailQueueTopic, email.getMessageId(), toJson(email));
                log.info("Queued scheduled email: {} to {}", email.getMessageId(), email.getRecipient());
            } catch (Exception e) {
                log.error("Failed to queue scheduled email {}: {}", email.getMessageId(), e.getMessage());
            }
        }
    }

    /**
     * Retry failed emails - every 5 minutes
     */
    @Scheduled(fixedRateString = "${app.scheduler.retry-rate:300000}")
    @Transactional
    public void retryFailedEmails() {
        List<EmailHistory> failed = repository.findTemplateByStatusAndRetryCountLessThan(
                EmailStatus.FAILED.name(),
                MAX_RETRIES
        );

        if (failed.isEmpty()) {
            return;
        }

        log.info("Processing {} failed emails for retry", failed.size());

        for (EmailHistory email : failed) {
            try {
                // Exponential backoff: 1min, 2min, 4min
                long delayMinutes = (long) Math.pow(2, email.getRetryCount() - 1);
                Instant nextRetry = email.getUpdatedAt().plus(delayMinutes, ChronoUnit.MINUTES);

                if (Instant.now().isAfter(nextRetry)) {
                    email.setStatus(EmailStatus.QUEUED.name());
                    email.setUpdatedAt(Instant.now());
                    repository.save(email);
                    kafkaTemplate.send(emailQueueTopic, email.getMessageId(), toJson(email));
                    log.info("Retrying email: {} (attempt {})", email.getMessageId(), email.getRetryCount() + 1);
                }
            } catch (Exception e) {
                log.error("Failed to retry email {}: {}", email.getMessageId(), e.getMessage());
            }
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Failed to serialize to JSON: {}", e.getMessage());
            return "{}";
        }
    }
}
