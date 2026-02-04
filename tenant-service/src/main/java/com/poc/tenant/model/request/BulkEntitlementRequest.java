package com.poc.tenant.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkEntitlementRequest {

    @NotNull
    private UUID tenantId;

    @NotNull
    private UUID productId;

    private UUID planId;

    @NotNull
    private List<FeatureEntitlement> features;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureEntitlement {
        private String code;
        private String type;
        private Integer limitValue;
    }
}
