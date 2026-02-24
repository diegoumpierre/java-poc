package com.poc.notification.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.shared.event.NotificationEventDTO;
import com.poc.shared.tenant.TenantContext;
import com.poc.notification.domain.EmailTemplate;
import com.poc.notification.dto.NotificationRequest;
import com.poc.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Consumes notification events published by other services via Kafka.
 * Replaces the Feign-based POST /api/notification/send calls.
 *
 * <p>Flow: Service publishes NotificationEventDTO to Kafka
 * -> this consumer converts it to NotificationRequest
 * -> delegates to NotificationService.queueEmail() (existing flow)</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${app.kafka.topics.notification-events:notification-events}",
            groupId = "${app.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNotificationEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String message = record.value();
        log.debug("Received notification event: {}", message);

        try {
            NotificationEventDTO event = objectMapper.readValue(message, NotificationEventDTO.class);
            processEvent(event);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("[NotificationEvent] Failed to parse event: {}", message, e);
            ack.acknowledge(); // Skip malformed messages
        } catch (Exception e) {
            log.error("[NotificationEvent] Error processing event: {}", e.getMessage(), e);
            ack.acknowledge(); // Acknowledge to avoid infinite reprocessing; errors are logged
        }
    }

    private void processEvent(NotificationEventDTO event) {
        if (event.getTo() == null || event.getTemplate() == null) {
            log.warn("[NotificationEvent] Event missing required fields: to={}, template={}",
                    event.getTo(), event.getTemplate());
            return;
        }

        log.info("[NotificationEvent] Processing: template={}, to={}, source={}",
                event.getTemplate(), event.getTo(), event.getSourceService());

        // Resolve template enum from string
        EmailTemplate template;
        try {
            template = EmailTemplate.valueOf(event.getTemplate());
        } catch (IllegalArgumentException e) {
            log.error("[NotificationEvent] Unknown template: '{}'. Available: {}",
                    event.getTemplate(), EmailTemplate.values());
            return;
        }

        // Build NotificationRequest (matches existing POST /api/notification/send contract)
        NotificationRequest request = NotificationRequest.builder()
                .to(event.getTo())
                .template(template)
                .subject(event.getSubject())
                .variables(event.getVariables())
                .build();

        // Set tenant context if provided (for multi-tenant email history)
        UUID tenantId = event.getTenantId();
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
        }

        try {
            // Delegate to existing notification flow (rate limit, history, Kafka email-queue, retry)
            String userId = event.getSourceService() != null
                    ? "kafka:" + event.getSourceService()
                    : "kafka:unknown";
            notificationService.queueEmail(request, userId);
        } finally {
            // Always clear tenant context
            if (tenantId != null) {
                TenantContext.clear();
            }
        }
    }
}
