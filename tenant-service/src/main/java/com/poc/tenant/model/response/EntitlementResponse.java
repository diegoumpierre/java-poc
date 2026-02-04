package com.poc.tenant.model.response;

import com.poc.tenant.domain.Entitlement;
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
public class EntitlementResponse {

    private UUID id;
    private UUID tenantId;
    private UUID productId;
    private String featureCode;
    private String source;
    private boolean enabled;
    private Integer limitValue;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static EntitlementResponse from(Entitlement entitlement) {
        return EntitlementResponse.builder()
                .id(entitlement.getId())
                .tenantId(entitlement.getTenantId())
                .productId(entitlement.getProductId())
                .featureCode(entitlement.getFeatureCode())
                .source(entitlement.getSource())
                .enabled(Boolean.TRUE.equals(entitlement.getEnabled()))
                .limitValue(entitlement.getLimitValue())
                .expiresAt(entitlement.getExpiresAt())
                .createdAt(entitlement.getCreatedAt())
                .updatedAt(entitlement.getUpdatedAt())
                .build();
    }
}
