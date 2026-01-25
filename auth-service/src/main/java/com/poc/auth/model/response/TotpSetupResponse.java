package com.poc.auth.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotpSetupResponse {

    /**
     * The TOTP secret (base32 encoded)
     * User should store this securely as backup
     */
    private String secret;

    /**
     * QR code as data URI (data:image/png;base64,...)
     * Can be directly used in an img src attribute
     */
    private String qrCodeDataUri;

    /**
     * The secret formatted for manual entry
     */
    private String manualEntryKey;

    /**
     * The issuer name shown in authenticator apps
     */
    private String issuer;

    /**
     * The account name (usually email) shown in authenticator apps
     */
    private String accountName;

    /**
     * Backup codes (only returned on initial setup)
     */
    private List<String> backupCodes;
}
