package com.poc.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to switch to a different tenant")
public class SwitchTenantRequest {

    @NotNull(message = "Tenant ID is required")
    @Schema(description = "The ID of the tenant to switch to", example = "00000000-0000-0000-0000-000000000000")
    private UUID tenantId;
}
