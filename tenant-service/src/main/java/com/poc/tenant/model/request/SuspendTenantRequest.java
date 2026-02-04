package com.poc.tenant.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuspendTenantRequest {
    @NotBlank(message = "Reason is required")
    private String reason;

    @Builder.Default
    private Boolean suspendChildren = false;
}
