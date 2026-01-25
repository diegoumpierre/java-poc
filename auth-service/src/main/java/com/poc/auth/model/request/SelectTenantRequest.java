package com.poc.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request to select tenant during multi-tenant login")
public class SelectTenantRequest {

    @NotBlank(message = "Temp token is required")
    @Schema(description = "Temporary token received from login response when tenant selection is required")
    private String tempToken;

    @NotNull(message = "Tenant ID is required")
    @Schema(description = "The ID of the tenant to login to", example = "00000000-0000-0000-0000-000000000000")
    private UUID tenantId;

    @Schema(description = "Remember me - extends token validity to 30 days", example = "false")
    private Boolean rememberMe;
}
