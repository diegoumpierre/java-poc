package com.poc.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "admin@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(example = "changeme123")
    private String password;

    @Schema(description = "Optional: Select specific tenant/membership for multi-tenant login")
    private UUID membershipId;

    @Schema(description = "Remember me - extends token validity to 30 days", example = "false")
    private Boolean rememberMe;
}
