package com.poc.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for tenant data from tenant-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {
    private UUID id;
    private String name;
    private String slug;
    private String tenantType;
    private String status;
    private String subscriptionStatus;
    private UUID parentTenantId;
    private Instant trialEndsAt;
    private Instant suspendedAt;
    private String suspensionReason;
    private UUID createdBy;
    private Instant deletedAt;
    private UUID deletedBy;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isActive() {
        return "ACTIVE".equals(status) && deletedAt == null;
    }

    public boolean isPlatform() {
        return "PLATFORM".equals(tenantType);
    }
}
