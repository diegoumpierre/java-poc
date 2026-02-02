package com.poc.tenant.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Event published for tenant provisioning lifecycle.
 *
 * Event types:
 * - PROVISION_USER_REQUESTED: tenant needs a user to be created (consumed by user-service)
 * - TENANT_PROVISIONED: tenant provisioning is complete, includes tenantId for billing to update subscription
 *
 * The password is ALREADY bcrypt-encoded - consumer must NOT re-encode.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantProvisioningEventDTO {

    private String eventType;
    private UUID tenantId;
    private String tenantName;
    private String tenantSlug;
    private String adminEmail;
    private String adminPasswordEncoded;
    private String adminName;
    private UUID planId;
    private UUID subscriptionId;
    private Integer subscriptionSeats;
    private Long timestamp;

    public static final String PROVISION_USER_REQUESTED = "PROVISION_USER_REQUESTED";
    public static final String TENANT_PROVISIONED = "TENANT_PROVISIONED";

    public static TenantProvisioningEventDTO provisionUserRequested(
            UUID tenantId, String tenantName, String tenantSlug,
            String adminEmail, String adminPasswordEncoded, String adminName,
            UUID planId) {
        return TenantProvisioningEventDTO.builder()
                .eventType(PROVISION_USER_REQUESTED)
                .tenantId(tenantId)
                .tenantName(tenantName)
                .tenantSlug(tenantSlug)
                .adminEmail(adminEmail)
                .adminPasswordEncoded(adminPasswordEncoded)
                .adminName(adminName)
                .planId(planId)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static TenantProvisioningEventDTO tenantProvisioned(
            UUID tenantId, String tenantName, String tenantSlug,
            UUID planId, UUID subscriptionId, String adminEmail) {
        return TenantProvisioningEventDTO.builder()
                .eventType(TENANT_PROVISIONED)
                .tenantId(tenantId)
                .tenantName(tenantName)
                .tenantSlug(tenantSlug)
                .planId(planId)
                .subscriptionId(subscriptionId)
                .adminEmail(adminEmail)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
