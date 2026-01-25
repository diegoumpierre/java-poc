package com.poc.auth.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private boolean success;
    private String message;
    private UserResponse user;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Long refreshExpiresIn;
    private Boolean requiresVerification;
    private String email;

    // 2FA specific fields
    private Boolean requires2FA;
    private String tempToken;  // Temporary token for 2FA verification

    // Multi-tenant support
    private Boolean requiresTenantSelection;  // True if user has multiple tenants
    private List<AccessContextResponse.TenantInfo> availableTenants;  // List of tenants user can choose from

    public static AuthResponse success(UserResponse user, String accessToken, Long expiresIn) {
        return AuthResponse.builder()
                .success(true)
                .message("Success")
                .user(user)
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .build();
    }

    public static AuthResponse success(UserResponse user, String accessToken, String refreshToken, Long expiresIn, Long refreshExpiresIn) {
        return AuthResponse.builder()
                .success(true)
                .message("Success")
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .refreshExpiresIn(refreshExpiresIn)
                .build();
    }

    public static AuthResponse success(String message) {
        return AuthResponse.builder()
                .success(true)
                .message(message)
                .build();
    }

    public static AuthResponse error(String message) {
        return AuthResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public static AuthResponse requires2FA(String email, String tempToken) {
        return AuthResponse.builder()
                .success(false)
                .message("Two-factor authentication required. Please check your email for the verification code.")
                .requires2FA(true)
                .email(email)
                .tempToken(tempToken)
                .build();
    }

    /**
     * Response when user belongs to multiple tenants and must select one
     */
    public static AuthResponse requiresTenantSelection(String email, String tempToken, List<AccessContextResponse.TenantInfo> tenants) {
        return AuthResponse.builder()
                .success(false)
                .message("User belongs to multiple organizations. Please select one to continue.")
                .requiresTenantSelection(true)
                .email(email)
                .tempToken(tempToken)
                .availableTenants(tenants)
                .build();
    }
}
