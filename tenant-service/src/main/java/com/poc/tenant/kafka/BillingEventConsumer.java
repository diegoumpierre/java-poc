package com.poc.tenant.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.tenant.event.TenantProvisioningEventPublisher;
import com.poc.tenant.model.request.TenantRequest;
import com.poc.tenant.model.response.TenantResponse;
import com.poc.tenant.service.EntitlementService;
import com.poc.tenant.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Consumes billing-events from Kafka to handle provisioning flows.
 *
 * Event types handled:
 * - CHECKOUT_PROVISIONING_REQUESTED: billing completed checkout, needs tenant + user + entitlements
 * - SUBSCRIPTION_CREATED: marketplace purchase, provision entitlements for existing tenant
 * - SUBSCRIPTION_CANCELLED: subscription was cancelled, revoke entitlements
 * - SUBSCRIPTION_SUSPENDED: subscription is overdue, suspend tenant
 *
 * Idempotency: createTenant checks slug uniqueness; entitlements check for duplicates.
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class BillingEventConsumer {

    private final ObjectMapper objectMapper;
    private final TenantService tenantService;
    private final EntitlementService entitlementService;

    @Autowired(required = false)
    private TenantProvisioningEventPublisher provisioningEventPublisher;

    /**
     * Handles billing events from Kafka.
     * Exceptions are NOT caught here — they propagate to the DefaultErrorHandler
     * which retries 3 times with 5s backoff, then routes to DLT (billing-events.DLT).
     */
    @KafkaListener(
            topics = "${app.kafka.topics.billing-events:billing-events}",
            groupId = "tenant-service"
    )
    public void handleBillingEvent(String message) {
        JsonNode event;
        try {
            event = objectMapper.readTree(message);
        } catch (Exception e) {
            log.error("[BillingEventConsumer] Failed to parse message (will go to DLT): {}", message, e);
            throw new RuntimeException("Failed to parse billing event", e);
        }

        String eventType = event.has("eventType") ? event.get("eventType").asText() : null;

        switch (eventType != null ? eventType : "") {
            case "CHECKOUT_PROVISIONING_REQUESTED" -> handleCheckoutProvisioningRequested(event);
            case "SUBSCRIPTION_CREATED" -> handleSubscriptionCreated(event);
            case "SUBSCRIPTION_CANCELLED" -> handleSubscriptionCancelled(event);
            case "SUBSCRIPTION_SUSPENDED" -> handleSubscriptionSuspended(event);
            default -> log.debug("[BillingEventConsumer] Ignoring event type: {}", eventType);
        }
    }

    /**
     * Handles CHECKOUT_PROVISIONING_REQUESTED from billing-service.
     * Creates tenant, publishes user creation event, provisions entitlements,
     * and publishes TENANT_PROVISIONED for billing to update subscription.
     *
     * Critical steps (tenant creation, entitlement provisioning) throw on failure
     * so the message is retried by the error handler before going to DLT.
     * Non-critical steps (event publishing) use outbox pattern and won't fail.
     */
    private void handleCheckoutProvisioningRequested(JsonNode event) {
        UUID checkoutId = parseUUID(event, "checkoutId");
        UUID subscriptionId = parseUUID(event, "subscriptionId");
        UUID planId = parseUUID(event, "planId");
        UUID productId = parseUUID(event, "productId");
        String email = getTextOrNull(event, "email");
        String name = getTextOrNull(event, "name");
        String encodedPassword = getTextOrNull(event, "encodedPassword");

        if (email == null || name == null) {
            log.warn("[BillingEventConsumer] Missing email or name in CHECKOUT_PROVISIONING_REQUESTED: checkoutId={}",
                    checkoutId);
            return; // Bad data - no point retrying
        }

        log.info("[BillingEventConsumer] Processing CHECKOUT_PROVISIONING_REQUESTED: checkoutId={}, email={}",
                checkoutId, email);

        // Step 1: Create tenant (throws on failure → retry/DLT)
        String slug = generateSlug(email);
        TenantRequest tenantRequest = new TenantRequest();
        tenantRequest.setName(name);
        tenantRequest.setSlug(slug);
        TenantResponse tenant = tenantService.create(tenantRequest);
        log.info("[BillingEventConsumer] Created tenant: id={}, slug={}", tenant.getId(), tenant.getSlug());

        // Step 2: Publish PROVISION_USER_REQUESTED event for user-service (via outbox - safe)
        if (provisioningEventPublisher != null && encodedPassword != null) {
            provisioningEventPublisher.publishProvisionUserRequested(
                    tenant.getId(), tenant.getName(), tenant.getSlug(),
                    email, encodedPassword, name, planId);
            log.info("[BillingEventConsumer] Published PROVISION_USER_REQUESTED for tenant {} (email: {})",
                    tenant.getId(), email);
        } else {
            log.warn("[BillingEventConsumer] Cannot publish PROVISION_USER_REQUESTED: publisher={}, password={}",
                    provisioningEventPublisher != null ? "available" : "null",
                    encodedPassword != null ? "present" : "null");
        }

        // Step 3: Provision entitlements from features in event
        if (event.has("features") && event.get("features").isArray()) {
            int provisioned = 0;
            for (JsonNode feature : event.get("features")) {
                String code = getTextOrNull(feature, "code");
                Integer limitValue = feature.has("limitValue") && !feature.get("limitValue").isNull()
                        ? feature.get("limitValue").asInt() : null;

                if (code != null) {
                    entitlementService.grantEntitlement(
                            tenant.getId(), productId, code, "subscription", limitValue, null);
                    provisioned++;
                }
            }
            log.info("[BillingEventConsumer] Provisioned {} entitlements for tenant {}", provisioned, tenant.getId());
        }

        // Step 4: Publish TENANT_PROVISIONED for billing-service (via outbox - safe)
        if (provisioningEventPublisher != null) {
            provisioningEventPublisher.publishTenantProvisioned(
                    tenant.getId(), tenant.getName(), tenant.getSlug(),
                    planId, subscriptionId, email);
            log.info("[BillingEventConsumer] Published TENANT_PROVISIONED for tenant {} (subscription: {})",
                    tenant.getId(), subscriptionId);
        }
    }

    /**
     * Handles SUBSCRIPTION_CREATED from billing-service (marketplace purchases).
     * Provisions entitlements for an existing tenant that purchased a new product.
     * Exceptions propagate for retry/DLT handling.
     */
    private void handleSubscriptionCreated(JsonNode event) {
        UUID tenantId = parseUUID(event, "tenantId");
        UUID subscriptionId = parseUUID(event, "subscriptionId");
        UUID productId = parseUUID(event, "productId");

        if (tenantId == null) {
            log.warn("[BillingEventConsumer] Missing tenantId in SUBSCRIPTION_CREATED");
            return; // Bad data - no point retrying
        }

        log.info("[BillingEventConsumer] Processing SUBSCRIPTION_CREATED: tenantId={}, subscriptionId={}, productId={}",
                tenantId, subscriptionId, productId);

        if (event.has("features") && event.get("features").isArray()) {
            int provisioned = 0;
            for (JsonNode feature : event.get("features")) {
                String code = getTextOrNull(feature, "code");
                Integer limitValue = feature.has("limitValue") && !feature.get("limitValue").isNull()
                        ? feature.get("limitValue").asInt() : null;

                if (code != null) {
                    entitlementService.grantEntitlement(
                            tenantId, productId, code, "subscription", limitValue, null);
                    provisioned++;
                }
            }
            log.info("[BillingEventConsumer] Provisioned {} entitlements for tenant {} (subscription {})",
                    provisioned, tenantId, subscriptionId);
        } else {
            log.warn("[BillingEventConsumer] SUBSCRIPTION_CREATED has no features: tenantId={}, subscriptionId={}",
                    tenantId, subscriptionId);
        }
    }

    /**
     * Handles SUBSCRIPTION_CANCELLED from billing-service.
     * Revokes entitlements. Exceptions propagate for retry/DLT.
     */
    private void handleSubscriptionCancelled(JsonNode event) {
        UUID tenantId = parseUUID(event, "tenantId");
        UUID subscriptionId = parseUUID(event, "subscriptionId");

        if (tenantId == null) {
            log.warn("[BillingEventConsumer] Missing tenantId in SUBSCRIPTION_CANCELLED");
            return;
        }

        log.info("[BillingEventConsumer] Processing SUBSCRIPTION_CANCELLED: tenantId={}, subscriptionId={}",
                tenantId, subscriptionId);

        entitlementService.revokeByTenantIdAndSource(tenantId, "subscription");
        log.info("[BillingEventConsumer] Revoked subscription entitlements for tenant {}", tenantId);
    }

    /**
     * Handles SUBSCRIPTION_SUSPENDED from billing-service.
     * Suspends the tenant. Exceptions propagate for retry/DLT.
     */
    private void handleSubscriptionSuspended(JsonNode event) {
        UUID tenantId = parseUUID(event, "tenantId");
        String reason = getTextOrNull(event, "suspendReason");

        if (tenantId == null) {
            log.warn("[BillingEventConsumer] Missing tenantId in SUBSCRIPTION_SUSPENDED");
            return;
        }

        if (reason == null) {
            reason = "Subscription suspended due to overdue payment";
        }

        log.info("[BillingEventConsumer] Processing SUBSCRIPTION_SUSPENDED: tenantId={}", tenantId);

        com.poc.tenant.model.request.SuspendTenantRequest suspendRequest =
                com.poc.tenant.model.request.SuspendTenantRequest.builder()
                        .reason(reason)
                        .suspendChildren(false)
                        .build();
        tenantService.suspendTenant(tenantId, suspendRequest);
        log.info("[BillingEventConsumer] Suspended tenant {} due to: {}", tenantId, reason);
    }

    private String generateSlug(String email) {
        String prefix = email.split("@")[0]
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private UUID parseUUID(JsonNode event, String field) {
        if (!event.has(field) || event.get(field).isNull()) return null;
        try {
            return UUID.fromString(event.get(field).asText());
        } catch (IllegalArgumentException e) {
            log.warn("[BillingEventConsumer] Invalid UUID for field {}: {}", field, event.get(field).asText());
            return null;
        }
    }

    private String getTextOrNull(JsonNode node, String field) {
        if (!node.has(field) || node.get(field).isNull()) return null;
        return node.get(field).asText();
    }
}
