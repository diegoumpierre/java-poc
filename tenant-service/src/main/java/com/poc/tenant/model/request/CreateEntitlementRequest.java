package com.poc.tenant.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateEntitlementRequest {

    private UUID tenantId;

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotBlank(message = "Feature code is required")
    private String featureCode;

    @Builder.Default
    private String source = "subscription";

    private Integer limitValue;

    private Instant expiresAt;
}
