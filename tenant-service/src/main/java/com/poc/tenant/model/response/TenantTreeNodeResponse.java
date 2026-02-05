package com.poc.tenant.model.response;

import com.poc.tenant.tenant.domain.Tenant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantTreeNodeResponse {
    private UUID id;
    private String name;
    private String slug;
    private String tenantType;
    private String status;
    private String subscriptionStatus;
    private int childCount;
    @Builder.Default
    private List<TenantTreeNodeResponse> children = new ArrayList<>();
    private Instant suspendedAt;
    private String suspensionReason;
    private Instant trialEndsAt;
    private Instant createdAt;

    public static TenantTreeNodeResponse from(Tenant tenant, int childCount) {
        return TenantTreeNodeResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .slug(tenant.getSlug())
                .tenantType(tenant.getTenantType())
                .status(tenant.getStatus())
                .subscriptionStatus(tenant.getSubscriptionStatus())
                .childCount(childCount)
                .children(new ArrayList<>())
                .suspendedAt(tenant.getSuspendedAt())
                .suspensionReason(tenant.getSuspensionReason())
                .trialEndsAt(tenant.getTrialEndsAt())
                .createdAt(tenant.getCreatedAt())
                .build();
    }
}
