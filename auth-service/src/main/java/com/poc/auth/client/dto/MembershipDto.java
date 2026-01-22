package com.poc.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for membership data from user-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDto {
    private UUID id;
    private UUID userId;
    private UUID tenantId;
    private String status;
    private List<UUID> roleIds;
    private Instant deletedAt;
    private UUID deletedBy;
    private Instant createdAt;
    private Instant updatedAt;

    // Embedded tenant info (from user-service optimization)
    private TenantInfo tenant;

    public boolean isActive() {
        return "ACTIVE".equals(status) && deletedAt == null;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantInfo {
        private UUID id;
        private String name;
        private String slug;
        private String tenantType;
        private String status;
        private UUID parentTenantId;
    }
}
