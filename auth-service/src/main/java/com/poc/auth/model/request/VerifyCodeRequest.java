package com.poc.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to verify a code sent via email")
public class VerifyCodeRequest {

    @Schema(description = "Email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "6-digit verification code", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Code must be exactly 6 digits")
    private String code;

    @Schema(description = "Type of verification: PASSWORD_RESET or EMAIL_VERIFICATION",
            example = "PASSWORD_RESET", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Type is required")
    private String type;
}
