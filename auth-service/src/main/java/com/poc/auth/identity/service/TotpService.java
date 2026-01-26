package com.poc.auth.service;

import com.poc.auth.model.response.TotpSetupResponse;

public interface TotpService {

    /**
     * Generate a new TOTP secret for a user
     * @return The generated secret (base32 encoded)
     */
    String generateSecret();

    /**
     * Generate setup data including QR code
     * @param email User's email (used as label in authenticator app)
     * @param secret The TOTP secret
     * @return Setup response with QR code data URL and secret
     */
    TotpSetupResponse generateSetupData(String email, String secret);

    /**
     * Verify a TOTP code against a secret
     * @param secret The user's TOTP secret
     * @param code The 6-digit code from authenticator app
     * @return true if the code is valid
     */
    boolean verifyCode(String secret, String code);

    /**
     * Generate backup codes for recovery
     * @param count Number of backup codes to generate
     * @return Array of backup codes (plain text - hash before storing)
     */
    String[] generateBackupCodes(int count);
}
