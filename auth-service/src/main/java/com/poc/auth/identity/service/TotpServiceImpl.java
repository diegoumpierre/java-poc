package com.poc.auth.service.impl;

import com.poc.auth.model.response.TotpSetupResponse;
import com.poc.auth.service.TotpService;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
@Slf4j
public class TotpServiceImpl implements TotpService {

    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final SecureRandom secureRandom;
    private CodeVerifier codeVerifier;

    @Value("${app.totp.issuer:101 Softwares}")
    private String issuer;

    @Value("${app.totp.digits:6}")
    private int digits;

    @Value("${app.totp.period:30}")
    private int period;

    public TotpServiceImpl() {
        this.secretGenerator = new DefaultSecretGenerator();
        this.qrGenerator = new ZxingPngQrGenerator();
        this.secureRandom = new SecureRandom();
    }

    @PostConstruct
    public void init() {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, digits);
        this.codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        // Allow 1 period before and after current time to handle clock drift
        ((DefaultCodeVerifier) this.codeVerifier).setTimePeriod(period);
        ((DefaultCodeVerifier) this.codeVerifier).setAllowedTimePeriodDiscrepancy(1);
    }

    @Override
    public String generateSecret() {
        return secretGenerator.generate();
    }

    @Override
    public TotpSetupResponse generateSetupData(String email, String secret) {
        QrData qrData = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(digits)
                .period(period)
                .build();

        String qrCodeDataUri;
        try {
            byte[] imageData = qrGenerator.generate(qrData);
            qrCodeDataUri = getDataUriForImage(imageData, qrGenerator.getImageMimeType());
        } catch (QrGenerationException e) {
            log.error("Failed to generate QR code", e);
            throw new RuntimeException("Failed to generate QR code for TOTP setup", e);
        }

        return TotpSetupResponse.builder()
                .secret(secret)
                .qrCodeDataUri(qrCodeDataUri)
                .manualEntryKey(secret)
                .issuer(issuer)
                .accountName(email)
                .build();
    }

    @Override
    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null) {
            return false;
        }

        // Remove any spaces from the code
        String cleanCode = code.replaceAll("\\s", "");

        // Verify the code length
        if (cleanCode.length() != digits) {
            return false;
        }

        return codeVerifier.isValidCode(secret, cleanCode);
    }

    @Override
    public String[] generateBackupCodes(int count) {
        String[] codes = new String[count];
        for (int i = 0; i < count; i++) {
            // Generate 8-character alphanumeric codes
            codes[i] = generateBackupCode();
        }
        return codes;
    }

    private String generateBackupCode() {
        // Generate 8 random bytes and encode as alphanumeric
        byte[] bytes = new byte[6];
        secureRandom.nextBytes(bytes);

        // Convert to a readable format: XXXX-XXXX
        String encoded = Base64.getEncoder().encodeToString(bytes)
                .replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase()
                .substring(0, 8);

        return encoded.substring(0, 4) + "-" + encoded.substring(4, 8);
    }
}
