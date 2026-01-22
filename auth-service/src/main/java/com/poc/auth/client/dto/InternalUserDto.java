package com.poc.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for user data from user-service (internal endpoint).
 * Includes password hash for login validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalUserDto {
    private UUID id;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String avatar;
    private Boolean emailVerified;
    private Boolean enabled;
    private Integer failedLoginAttempts;
    private Instant lockedUntil;
    private Boolean twoFactorEnabled;
    private String twoFactorMethod;
    private String totpSecret;
    private Instant totpVerifiedAt;
    private Instant createdAt;

    public boolean isAccountLocked() {
        return lockedUntil != null && Instant.now().isBefore(lockedUntil);
    }

    public boolean isDeleted() {
        return Boolean.FALSE.equals(enabled);
    }
}
