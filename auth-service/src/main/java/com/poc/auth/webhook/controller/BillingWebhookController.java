package com.poc.auth.webhook.controller;

import com.poc.auth.service.impl.CachedEntitlementQueryService;
import com.poc.auth.service.impl.CachedTenantQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/webhooks/billing")
@RequiredArgsConstructor
@Slf4j
public class BillingWebhookController {

    private final CachedEntitlementQueryService cachedEntitlementQueryService;
    private final CachedTenantQueryService cachedTenantQueryService;

    @PostMapping("/subscription-created")
    public ResponseEntity<Map<String, String>> subscriptionCreated(@RequestBody SubscriptionEvent event) {
        log.info("Received subscription.created webhook for tenant: {}, subscription: {}",
                event.tenantId(), event.subscriptionId());

        cachedEntitlementQueryService.invalidateTenantCache(event.tenantId());
        cachedTenantQueryService.invalidateTenantCache(event.tenantId());

        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/subscription-updated")
    public ResponseEntity<Map<String, String>> subscriptionUpdated(@RequestBody SubscriptionEvent event) {
        log.info("Received subscription.updated webhook for tenant: {}, subscription: {}, {} -> {}",
                event.tenantId(), event.subscriptionId(), event.previousStatus(), event.status());

        cachedEntitlementQueryService.invalidateTenantCache(event.tenantId());

        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/subscription-cancelled")
    public ResponseEntity<Map<String, String>> subscriptionCancelled(@RequestBody SubscriptionEvent event) {
        log.info("Received subscription.cancelled webhook for tenant: {}, subscription: {}",
                event.tenantId(), event.subscriptionId());

        cachedEntitlementQueryService.invalidateTenantCache(event.tenantId());
        cachedTenantQueryService.invalidateTenantCache(event.tenantId());

        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    public record SubscriptionEvent(
            UUID tenantId,
            UUID subscriptionId,
            String status,
            String previousStatus
    ) {}
}
