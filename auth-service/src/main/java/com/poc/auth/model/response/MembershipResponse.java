package com.poc.auth.model.response;

import com.poc.auth.client.dto.MembershipDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipResponse {

    private UUID id;
    private UUID userId;
    private UUID tenantId;
    private String status;
    private List<RoleResponse> roles;
    private List<UUID> roleIds;  // For fetching roles from auth-service
    private Instant createdAt;
    private Instant updatedAt;

    // User details (when fetched)
    private String userEmail;
    private String userName;

    // Tenant details (embedded from organization-service)
    private String tenantName;
    private String tenantSlug;
    private String tenantType;
    private String tenantStatus;
    private UUID tenantParentId;

    public static MembershipResponse from(MembershipDto dto) {
        MembershipResponse.MembershipResponseBuilder builder = MembershipResponse.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .tenantId(dto.getTenantId())
                .status(dto.getStatus())
                .roleIds(dto.getRoleIds())  // Copy roleIds for fetching roles
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt());

        // Copy embedded tenant info if available
        if (dto.getTenant() != null) {
            builder.tenantName(dto.getTenant().getName())
                    .tenantSlug(dto.getTenant().getSlug())
                    .tenantType(dto.getTenant().getTenantType())
                    .tenantStatus(dto.getTenant().getStatus())
                    .tenantParentId(dto.getTenant().getParentTenantId());
        }

        return builder.build();
    }
}
