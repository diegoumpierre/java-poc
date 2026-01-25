package com.poc.auth.model.response;

import com.poc.auth.client.dto.TenantDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantResponse {

    private UUID id;
    private String name;
    private String slug;
    private UUID parentTenantId;
    private String tenantType;
    private String status;
    private Instant suspendedAt;
    private String suspensionReason;
    private Instant trialEndsAt;
    private String subscriptionStatus;
    private Instant createdAt;
    private Instant updatedAt;

    public static TenantResponse from(TenantDto dto) {
        return TenantResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .parentTenantId(dto.getParentTenantId())
                .tenantType(dto.getTenantType())
                .status(dto.getStatus())
                .suspendedAt(dto.getSuspendedAt())
                .suspensionReason(dto.getSuspensionReason())
                .trialEndsAt(dto.getTrialEndsAt())
                .subscriptionStatus(dto.getSubscriptionStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public boolean isPlatform() {
        return "PLATFORM".equals(tenantType);
    }
}
