package com.poc.auth.controller;

import com.poc.auth.client.UserClient;
import com.poc.auth.client.dto.InternalUserDto;
import com.poc.auth.client.dto.UpdateTotpRequest;
import com.poc.auth.model.request.TotpVerifySetupRequest;
import com.poc.auth.model.response.TotpSetupResponse;
import com.poc.auth.model.response.TwoFactorStatusResponse;
import com.poc.auth.repository.BackupCodeRepository;
import com.poc.auth.service.TotpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Two-Factor Authentication", description = "2FA management APIs")
@SecurityRequirement(name = "bearerAuth")
public class TwoFactorController {

    private final TotpService totpService;
    private final UserClient userClient;
    private final BackupCodeRepository backupCodeRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/status")
    @Operation(summary = "Get 2FA status", description = "Get current 2FA configuration status")
    public ResponseEntity<TwoFactorStatusResponse> getStatus(
            @RequestHeader("X-User-Id") String userId) {

        InternalUserDto user = findUser(userId);

        int backupCodesRemaining = backupCodeRepository.countUnusedByUserId(user.getId());

        TwoFactorStatusResponse response = TwoFactorStatusResponse.builder()
                .twoFactorEnabled(user.getTwoFactorEnabled() != null && user.getTwoFactorEnabled())
                .method(user.getTwoFactorMethod())
                .totpConfigured(user.getTotpSecret() != null && user.getTotpVerifiedAt() != null)
                .totpEnabledAt(user.getTotpVerifiedAt())
                .backupCodesRemaining(backupCodesRemaining)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/totp/setup")
    @Operation(summary = "Setup TOTP", description = "Generate TOTP secret and QR code")
    public ResponseEntity<?> setupTotp(@RequestHeader("X-User-Id") String userId) {

        InternalUserDto user = findUser(userId);

        if (user.getTotpSecret() != null && user.getTotpVerifiedAt() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "TOTP already configured. Disable first."));
        }

        String secret = totpService.generateSecret();
        TotpSetupResponse setupData = totpService.generateSetupData(user.getEmail(), secret);

        log.info("TOTP setup initiated for user: {}", user.getEmail());
        return ResponseEntity.ok(setupData);
    }

    @PostMapping("/totp/verify-setup")
    @Operation(summary = "Verify and enable TOTP", description = "Verify code and enable TOTP")
    public ResponseEntity<?> verifyTotpSetup(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody TotpVerifySetupRequest request) {

        InternalUserDto user = findUser(userId);

        if (!totpService.verifyCode(request.getSecret(), request.getCode())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid TOTP code"));
        }

        String[] backupCodes = totpService.generateBackupCodes(10);

        for (String code : backupCodes) {
            backupCodeRepository.save(user.getId(), passwordEncoder.encode(code.replace("-", "")));
        }

        userClient.updateTotp(user.getId(), UpdateTotpRequest.builder()
                .totpSecret(request.getSecret())
                .totpVerifiedAt(Instant.now())
                .twoFactorEnabled(true)
                .twoFactorMethod("TOTP")
                .build());

        log.info("TOTP enabled for user: {}", user.getEmail());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "TOTP enabled successfully",
                "backupCodes", Arrays.asList(backupCodes)
        ));
    }

    @PostMapping("/totp/disable")
    @Operation(summary = "Disable TOTP", description = "Disable TOTP authentication")
    public ResponseEntity<?> disableTotp(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Map<String, String> request) {

        String password = request.get("password");
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password required"));
        }

        InternalUserDto user = findUser(userId);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid password"));
        }

        userClient.updateTotp(user.getId(), UpdateTotpRequest.builder()
                .totpSecret(null)
                .totpVerifiedAt(null)
                .twoFactorMethod("EMAIL")
                .build());

        backupCodeRepository.deleteByUserId(user.getId());

        log.info("TOTP disabled for user: {}", user.getEmail());

        return ResponseEntity.ok(Map.of("success", true, "message", "TOTP disabled"));
    }

    @PostMapping("/disable")
    @Operation(summary = "Disable 2FA completely", description = "Disable all 2FA")
    public ResponseEntity<?> disable2FA(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Map<String, String> request) {

        String password = request.get("password");
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password required"));
        }

        InternalUserDto user = findUser(userId);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid password"));
        }

        userClient.updateTotp(user.getId(), UpdateTotpRequest.builder()
                .twoFactorEnabled(false)
                .twoFactorMethod("EMAIL")
                .totpSecret(null)
                .totpVerifiedAt(null)
                .build());

        backupCodeRepository.deleteByUserId(user.getId());

        log.info("2FA disabled for user: {}", user.getEmail());

        return ResponseEntity.ok(Map.of("success", true, "message", "2FA disabled"));
    }

    @PostMapping("/regenerate-backup-codes")
    @Operation(summary = "Regenerate backup codes", description = "Generate new backup codes")
    public ResponseEntity<?> regenerateBackupCodes(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Map<String, String> request) {

        String password = request.get("password");
        if (password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password required"));
        }

        InternalUserDto user = findUser(userId);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid password"));
        }

        if (user.getTotpSecret() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "TOTP must be enabled first"));
        }

        backupCodeRepository.deleteByUserId(user.getId());

        String[] backupCodes = totpService.generateBackupCodes(10);

        for (String code : backupCodes) {
            backupCodeRepository.save(user.getId(), passwordEncoder.encode(code.replace("-", "")));
        }

        log.info("Backup codes regenerated for user: {}", user.getEmail());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "backupCodes", Arrays.asList(backupCodes)
        ));
    }

    private InternalUserDto findUser(String userId) {
        return userClient.findById(UUID.fromString(userId));
    }
}
