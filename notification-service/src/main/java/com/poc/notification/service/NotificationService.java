package com.poc.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.shared.tenant.TenantContext;
import com.poc.notification.domain.ConfigType;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.EmailStatus;
import com.poc.notification.dto.NotificationRequest;
import com.poc.notification.dto.NotificationResponse;
import com.poc.notification.repository.EmailHistoryRepository;
import com.poc.notification.provider.SmtpEmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailHistoryRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RateLimitService rateLimitService;
    private final TenantConfigService tenantConfigService;
    private final SmtpEmailProvider smtpProvider;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.email-queue}")
    private String emailQueueTopic;

    @Transactional
    public NotificationResponse queueEmail(NotificationRequest request, String userId) {
        try {
            // 1. Check rate limit (legacy, allows all for template emails)
            rateLimitService.checkRateLimit(userId);

            // 2. Create history record with tenant context
            UUID tenantId = TenantContext.getCurrentTenant();
            EmailHistory history = EmailHistory.builder()
                    .messageId(UUID.randomUUID().toString())
                    .userId(userId)
                    .tenantId(tenantId != null ? tenantId.toString() : null)
                    .configType(ConfigType.NOTIFICATION.name())
                    .recipient(request.getTo())
                    .template(request.getTemplate().name())
                    .subject(request.getSubject() != null ?
                            request.getSubject() : request.getTemplate().getDefaultSubject())
                    .variables(toJson(request.getVariables()))
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .build();

            // 3. Check if scheduled
            if (request.getScheduledAt() != null && request.getScheduledAt().isAfter(Instant.now())) {
                history.setScheduledAt(request.getScheduledAt());
                history.setStatus(EmailStatus.PENDING.name());
            } else {
                history.setStatus(EmailStatus.QUEUED.name());
            }

            // 4. Save to database
            history = repository.save(history);

            // 5. Send to Kafka if not scheduled
            if (history.getStatus().equals(EmailStatus.QUEUED.name())) {
                kafkaTemplate.send(emailQueueTopic, history.getMessageId(), toJson(history));
                log.info("Queued email {} to {}", history.getMessageId(), history.getRecipient());
            } else {
                log.info("Scheduled email {} to {} for {}", history.getMessageId(),
                        history.getRecipient(), request.getScheduledAt());
            }

            return NotificationResponse.success(history.getMessageId());

        } catch (RateLimitService.RateLimitExceededException e) {
            log.warn("Rate limit exceeded for user {}: {}", userId, e.getMessage());
            return NotificationResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to queue email: {}", e.getMessage(), e);
            return NotificationResponse.error("Failed to queue email: " + e.getMessage());
        }
    }

    public List<EmailHistory> getHistoryByUser(String userId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            return repository.findByUserIdAndTenantIdOrderByCreatedAtDesc(userId, tenantId.toString());
        }
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Map<String, Object> testSmtpConnection() {
        try {
            var config = tenantConfigService.getNotificationConfig();
            boolean connected = smtpProvider.testConnection(config);
            return Map.of(
                    "connected", connected,
                    "message", connected ? "SMTP connection successful" : "SMTP connection failed"
            );
        } catch (Exception e) {
            return Map.of(
                    "connected", false,
                    "message", "SMTP connection failed: " + e.getMessage()
            );
        }
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Failed to serialize object to JSON: {}", e.getMessage());
            return "{}";
        }
    }
}
