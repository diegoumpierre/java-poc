package com.poc.tenant.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntitlementEventDTO {

    private String eventType;
    private UUID entitlementId;
    private UUID tenantId;
    private String featureCode;
    private Instant timestamp;

    public static final String ENTITLEMENT_GRANTED = "ENTITLEMENT_GRANTED";
    public static final String ENTITLEMENT_REVOKED = "ENTITLEMENT_REVOKED";
    public static final String ENTITLEMENT_UPDATED = "ENTITLEMENT_UPDATED";
    public static final String SUBSCRIPTION_ACTIVATED = "SUBSCRIPTION_ACTIVATED";
    public static final String SUBSCRIPTION_CANCELLED = "SUBSCRIPTION_CANCELLED";

    public static EntitlementEventDTO granted(UUID entitlementId, UUID tenantId, String featureCode) {
        return EntitlementEventDTO.builder()
                .eventType(ENTITLEMENT_GRANTED)
                .entitlementId(entitlementId)
                .tenantId(tenantId)
                .featureCode(featureCode)
                .timestamp(Instant.now())
                .build();
    }

    public static EntitlementEventDTO revoked(UUID entitlementId, UUID tenantId, String featureCode) {
        return EntitlementEventDTO.builder()
                .eventType(ENTITLEMENT_REVOKED)
                .entitlementId(entitlementId)
                .tenantId(tenantId)
                .featureCode(featureCode)
                .timestamp(Instant.now())
                .build();
    }

    public static EntitlementEventDTO subscriptionActivated(UUID tenantId) {
        return EntitlementEventDTO.builder()
                .eventType(SUBSCRIPTION_ACTIVATED)
                .tenantId(tenantId)
                .timestamp(Instant.now())
                .build();
    }

    public static EntitlementEventDTO subscriptionCancelled(UUID tenantId) {
        return EntitlementEventDTO.builder()
                .eventType(SUBSCRIPTION_CANCELLED)
                .tenantId(tenantId)
                .timestamp(Instant.now())
                .build();
    }
}
