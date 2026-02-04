package com.poc.tenant.membership.model;

import com.poc.tenant.membership.domain.Membership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {
    private UUID id;
    private UUID userId;
    private UUID tenantId;
    private String status;
    private Boolean isOwner;
    private List<UUID> roleIds;
    private Instant createdAt;

    private TenantInfo tenant;

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

    public static MembershipResponse from(Membership membership) {
        return MembershipResponse.builder()
                .id(membership.getId())
                .userId(membership.getUserId())
                .tenantId(membership.getTenantId())
                .status(membership.getStatus())
                .isOwner(membership.getIsOwner())
                .createdAt(membership.getCreatedAt())
                .build();
    }
}
