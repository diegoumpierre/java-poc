package com.poc.shared.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes notification events to Kafka for async processing
 * by notification-service. Replaces Feign-based NotificationClient calls.
 *
 * <p>Only active when {@code app.kafka.enabled=true}.</p>
 *
 * <p>Usage in services:</p>
 * <pre>
 * notificationEventPublisher.send(NotificationEventDTO.of(
 *     "user@email.com",
 *     "WELCOME",
 *     Map.of("userName", "John"),
 *     tenantId,
 *     "auth-service"
 * ));
 * </pre>
 */
@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class NotificationEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.notification-events:notification-events}")
    private String notificationEventsTopic;

    /**
     * Publish a notification event to Kafka.
     * Fire-and-forget with error logging (matches existing Feign try-catch pattern).
     *
     * @param event the notification event to publish
     */
    public void send(NotificationEventDTO event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            String key = event.getTenantId() != null ? event.getTenantId().toString() : null;
            kafkaTemplate.send(notificationEventsTopic, key, json)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("[NotificationEvent] Failed to publish event: template={}, to={} - {}",
                                    event.getTemplate(), event.getTo(), ex.getMessage());
                        } else {
                            log.debug("[NotificationEvent] Published event: template={}, to={}, source={}",
                                    event.getTemplate(), event.getTo(), event.getSourceService());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("[NotificationEvent] Failed to serialize event: {}", e.getMessage());
        }
    }
}
