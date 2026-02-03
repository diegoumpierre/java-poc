package com.poc.tenant.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.tenant.tenant.service.TenantProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Consumes user-events from Kafka to complete async tenant provisioning.
 *
 * When user-service creates a user for a tenant provisioning request,
 * it publishes a USER_CREATED_FOR_TENANT event. This consumer:
 *   1. Creates the admin membership locally (TNT_ACC_MEMBERSHIPS + TNT_ACC_MEMBERSHIP_ROLES)
 *   2. Updates tenant provisioningStatus to "COMPLETE"
 *
 * Idempotency: If membership already exists, MembershipService.addMember will throw
 * BusinessException which is caught and logged.
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final ObjectMapper objectMapper;
    private final TenantProvisioningService provisioningService;

    /**
     * Handles user events from Kafka.
     * Exceptions propagate to the DefaultErrorHandler which retries 3 times
     * with 5s backoff, then routes to DLT (user-events.DLT).
     */
    @KafkaListener(
            topics = "${app.kafka.topics.user-events:user-events}",
            groupId = "tenant-service"
    )
    public void handleUserEvent(String message) {
        JsonNode event;
        try {
            event = objectMapper.readTree(message);
        } catch (Exception e) {
            log.error("[UserEventConsumer] Failed to parse message (will go to DLT): {}", message, e);
            throw new RuntimeException("Failed to parse user event", e);
        }

        String eventType = event.has("eventType") ? event.get("eventType").asText() : null;

        if ("USER_CREATED_FOR_TENANT".equals(eventType)) {
            handleUserCreatedForTenant(event);
        } else {
            log.debug("[UserEventConsumer] Ignoring event type: {}", eventType);
        }
    }

    private void handleUserCreatedForTenant(JsonNode event) {
        UUID tenantId = parseUUID(event, "tenantId");
        UUID userId = parseUUID(event, "userId");

        if (tenantId == null || userId == null) {
            log.warn("[UserEventConsumer] Missing tenantId or userId in USER_CREATED_FOR_TENANT event");
            return;
        }

        log.info("[UserEventConsumer] Received USER_CREATED_FOR_TENANT: userId={}, tenantId={}",
                userId, tenantId);

        provisioningService.completeProvisioningWithUser(tenantId, userId);
    }

    private UUID parseUUID(JsonNode event, String field) {
        if (!event.has(field) || event.get(field).isNull()) return null;
        try {
            return UUID.fromString(event.get(field).asText());
        } catch (IllegalArgumentException e) {
            log.warn("[UserEventConsumer] Invalid UUID for field {}: {}", field, event.get(field).asText());
            return null;
        }
    }
}
