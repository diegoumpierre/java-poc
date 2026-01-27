package com.poc.auth.service;

import com.poc.auth.client.UserClient;
import com.poc.auth.client.dto.InternalUserDto;
import com.poc.auth.client.dto.NotificationRequest;
import com.poc.auth.client.dto.UpdateTwoFactorRequest;
import com.poc.auth.domain.TwoFactorCode;
import com.poc.auth.repository.TwoFactorCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwoFactorService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${app.security.two-factor.code-expiration-minutes:5}")
    private int codeExpirationMinutes;

    @Value("${app.security.two-factor.max-attempts-per-hour:5}")
    private int maxAttemptsPerHour;

    private final TwoFactorCodeRepository twoFactorCodeRepository;
    private final UserClient userClient;
    private final NotificationService notificationService;

    /**
     * Enable 2FA for a user
     */
    public void enable2FA(UUID userId) {
        userClient.updateTwoFactor(userId,
                UpdateTwoFactorRequest.builder().enabled(true).method("EMAIL").build());
        log.info("2FA enabled for user: {}", userId);
    }

    /**
     * Disable 2FA for a user
     */
    public void disable2FA(UUID userId) {
        userClient.updateTwoFactor(userId,
                UpdateTwoFactorRequest.builder().enabled(false).build());
        log.info("2FA disabled for user: {}", userId);
    }

    /**
     * Check if user has 2FA enabled
     */
    public boolean is2FAEnabled(UUID userId) {
        try {
            InternalUserDto user = userClient.findById(userId);
            return Boolean.TRUE.equals(user.getTwoFactorEnabled());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate and send 2FA code via email.
     * Accepts individual fields instead of User entity.
     */
    @Transactional
    public void sendVerificationCode(UUID userId, String email, String name, String ipAddress, String userAgent) {
        // Check rate limit
        int recentAttempts = twoFactorCodeRepository.countRecentAttempts(
                userId,
                Instant.now().minus(1, ChronoUnit.HOURS)
        );

        if (recentAttempts >= maxAttemptsPerHour) {
            log.warn("2FA rate limit exceeded for user: {}", email);
            throw new IllegalStateException("Too many verification attempts. Please try again later.");
        }

        // Invalidate previous codes
        twoFactorCodeRepository.invalidatePreviousCodes(userId);

        // Generate new code
        String code = generateCode();
        Instant expiresAt = Instant.now().plus(codeExpirationMinutes, ChronoUnit.MINUTES);

        TwoFactorCode twoFactorCode = TwoFactorCode.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .code(code)
                .expiresAt(expiresAt)
                .used(false)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(Instant.now())
                .build();

        twoFactorCodeRepository.save(twoFactorCode);

        // Send code via notification service
        NotificationRequest notification = NotificationRequest.twoFactorCode(
                email,
                name,
                code,
                codeExpirationMinutes
        );
        notificationService.send(notification);

        log.info("2FA code sent to user: {} (expires in {} minutes)", email, codeExpirationMinutes);
    }

    /**
     * Verify 2FA code
     */
    @Transactional
    public boolean verifyCode(UUID userId, String code) {
        Optional<TwoFactorCode> twoFactorCodeOpt = twoFactorCodeRepository.findValidCode(userId, code);

        if (twoFactorCodeOpt.isEmpty()) {
            log.warn("Invalid 2FA code attempt for user: {}", userId);
            return false;
        }

        TwoFactorCode twoFactorCode = twoFactorCodeOpt.get();

        if (twoFactorCode.isExpired()) {
            log.warn("Expired 2FA code attempt for user: {}", userId);
            return false;
        }

        // Mark code as used
        twoFactorCode.setUsed(true);
        twoFactorCode.markNotNew();
        twoFactorCodeRepository.save(twoFactorCode);

        log.info("2FA code verified successfully for user: {}", userId);
        return true;
    }

    /**
     * Generate 6-digit verification code
     */
    private String generateCode() {
        int code = SECURE_RANDOM.nextInt(1000000);
        return String.format("%06d", code);
    }

    /**
     * Cleanup expired codes (runs every hour)
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void cleanupExpiredCodes() {
        twoFactorCodeRepository.deleteExpiredCodes(Instant.now());
        log.debug("Cleaned up expired 2FA codes");
    }
}
