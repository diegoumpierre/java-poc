package com.poc.auth.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorStatusResponse {

    /**
     * Whether 2FA is enabled for this user
     */
    private boolean twoFactorEnabled;

    /**
     * The current 2FA method: EMAIL or TOTP
     */
    private String method;

    /**
     * Whether TOTP (authenticator app) is configured
     */
    private boolean totpConfigured;

    /**
     * When TOTP was enabled (if configured)
     */
    private Instant totpEnabledAt;

    /**
     * Number of unused backup codes remaining
     */
    private int backupCodesRemaining;
}
