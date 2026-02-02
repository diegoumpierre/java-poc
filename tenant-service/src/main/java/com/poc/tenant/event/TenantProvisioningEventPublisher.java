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
 * Publishes tenant provisioning events via the Transactional Outbox Pattern.
 * Events are saved to TNT_OUTBOX_EVENTS table within the current transaction,
 * then asynchronously published to Kafka by OutboxPublisher.
 */
@Service
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class TenantProvisioningEventPublisher {

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.tenant-provisioning-events:tenant-provisioning-events}")
    private String tenantProvisioningEventsTopic;

    public void publishProvisionUserRequested(UUID tenantId, String tenantName, String tenantSlug,
                                               String adminEmail, String adminPasswordEncoded,
                                               String adminName, UUID planId) {
        TenantProvisioningEventDTO event = TenantProvisioningEventDTO.provisionUserRequested(
                tenantId, tenantName, tenantSlug, adminEmail, adminPasswordEncoded, adminName, planId);
        saveToOutbox(event, tenantId.toString());
    }

    public void publishTenantProvisioned(UUID tenantId, String tenantName, String tenantSlug,
                                          UUID planId, UUID subscriptionId, String adminEmail) {
        TenantProvisioningEventDTO event = TenantProvisioningEventDTO.tenantProvisioned(
                tenantId, tenantName, tenantSlug, planId, subscriptionId, adminEmail);
        saveToOutbox(event, tenantId.toString());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveToOutbox(TenantProvisioningEventDTO event, String key) {
        try {
            String json = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = OutboxEvent.create(tenantProvisioningEventsTopic, key, json);
            outboxRepository.save(outboxEvent);
            log.debug("[TenantProvisioningEvent] Saved event to outbox: type={}, tenant={}, id={}",
                    event.getEventType(), event.getTenantId(), outboxEvent.getId());
        } catch (JsonProcessingException e) {
            log.error("[TenantProvisioningEvent] Failed to serialize event: {}", e.getMessage());
        }
    }
}
