package com.poc.auth.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.auth.service.impl.CachedEntitlementQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Kafka listener for entitlement events from tenant-service.
 *
 * Listens to entitlement-events topic and invalidates the local cache
 * when entitlements change. This ensures auth-service always has
 * up-to-date entitlement data.
 *
 * Event types handled:
 * - ENTITLEMENT_GRANTED: New feature enabled for tenant
 * - ENTITLEMENT_REVOKED: Feature disabled for tenant
 * - ENTITLEMENT_UPDATED: Feature limits changed
 * - SUBSCRIPTION_ACTIVATED: All entitlements for tenant may have changed
 * - SUBSCRIPTION_CANCELLED: All entitlements for tenant revoked
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class EntitlementEventListener {

    private final CachedEntitlementQueryService cachedEntitlementQueryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.entitlement-events:entitlement-events}", groupId = "${spring.kafka.consumer.group-id:auth-service}")
    public void handleEntitlementEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            String tenantIdStr = event.has("tenantId") && !event.get("tenantId").isNull()
                    ? event.get("tenantId").asText()
                    : null;

            if (tenantIdStr == null) {
                log.warn("[EntitlementEventListener] Received event without tenantId: {}", eventType);
                return;
            }

            UUID tenantId = UUID.fromString(tenantIdStr);
            String featureCode = event.has("featureCode") && !event.get("featureCode").isNull()
                    ? event.get("featureCode").asText()
                    : null;

            log.info("[EntitlementEventListener] Received {} for tenant {} (feature: {})",
                    eventType, tenantId, featureCode);

            // Invalidate cache for all entitlement-related events
            switch (eventType) {
                case "ENTITLEMENT_GRANTED":
                case "ENTITLEMENT_REVOKED":
                case "ENTITLEMENT_UPDATED":
                case "SUBSCRIPTION_ACTIVATED":
                case "SUBSCRIPTION_CANCELLED":
                    cachedEntitlementQueryService.invalidateTenantCache(tenantId);
                    break;
                default:
                    log.debug("[EntitlementEventListener] Ignoring unknown event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("[EntitlementEventListener] Failed to process event: {}", e.getMessage(), e);
        }
    }
}
