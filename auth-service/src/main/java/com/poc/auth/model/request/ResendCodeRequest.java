package com.poc.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to resend a verification code")
public class ResendCodeRequest {

    @Schema(description = "Email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Type of verification: PASSWORD_RESET or EMAIL_VERIFICATION",
            example = "EMAIL_VERIFICATION", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Type is required")
    private String type;
}
