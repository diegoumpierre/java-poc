package com.poc.auth.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRequestRequest {

    @NotNull(message = "Tenant ID is required")
    private UUID tenantId;

    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
}
