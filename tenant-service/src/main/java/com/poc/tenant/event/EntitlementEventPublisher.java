package com.poc.tenant.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.tenant.domain.OutboxEvent;
import com.poc.tenant.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Publishes entitlement events via the Transactional Outbox Pattern.
 * Events are saved to TNT_OUTBOX_EVENTS table within the current transaction,
 * then asynchronously published to Kafka by OutboxPublisher.
 */
@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class EntitlementEventPublisher {

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.entitlement-events:entitlement-events}")
    private String entitlementEventsTopic;

    public void publishEntitlementGranted(UUID entitlementId, UUID tenantId, String featureCode) {
        EntitlementEventDTO event = EntitlementEventDTO.granted(entitlementId, tenantId, featureCode);
        saveToOutbox(event, tenantId.toString());
    }

    public void publishEntitlementRevoked(UUID entitlementId, UUID tenantId, String featureCode) {
        EntitlementEventDTO event = EntitlementEventDTO.revoked(entitlementId, tenantId, featureCode);
        saveToOutbox(event, tenantId.toString());
    }

    public void publishSubscriptionActivated(UUID tenantId) {
        EntitlementEventDTO event = EntitlementEventDTO.subscriptionActivated(tenantId);
        saveToOutbox(event, tenantId.toString());
    }

    public void publishSubscriptionCancelled(UUID tenantId) {
        EntitlementEventDTO event = EntitlementEventDTO.subscriptionCancelled(tenantId);
        saveToOutbox(event, tenantId.toString());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveToOutbox(EntitlementEventDTO event, String key) {
        try {
            String json = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = OutboxEvent.create(entitlementEventsTopic, key, json);
            outboxRepository.save(outboxEvent);
            log.debug("[EntitlementEvent] Saved event to outbox: type={}, tenant={}, id={}",
                    event.getEventType(), event.getTenantId(), outboxEvent.getId());
        } catch (JsonProcessingException e) {
            log.error("[EntitlementEvent] Failed to serialize event: {}", e.getMessage());
        }
    }
}
