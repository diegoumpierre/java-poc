package com.poc.auth.service.impl;

import com.poc.auth.client.UserClient;
import com.poc.auth.client.dto.InternalUserDto;
import com.poc.auth.client.dto.RegisterInternalRequest;
import com.poc.auth.client.dto.UpdateLoginStateRequest;
import com.poc.auth.client.dto.UpdatePasswordRequest;
import com.poc.auth.domain.VerificationCode;
import com.poc.shared.enums.RoleCode;
import com.poc.auth.domain.enums.VerificationType;
import com.poc.auth.client.dto.NotificationRequest;
import com.poc.auth.service.NotificationService;
import com.poc.auth.metrics.AuthMetrics;
import com.poc.auth.repository.JpaRepositoryVerificationCode;
import com.poc.auth.model.request.LoginRequest;
import com.poc.auth.model.request.RegisterRequest;
import com.poc.auth.model.request.ResendCodeRequest;
import com.poc.auth.model.request.ResetPasswordRequest;
import com.poc.auth.model.request.SelectTenantRequest;
import com.poc.auth.model.request.TwoFactorVerifyRequest;
import com.poc.auth.model.request.VerifyCodeRequest;
import com.poc.auth.model.response.AccessContextResponse;
import com.poc.auth.model.response.AuthResponse;
import com.poc.auth.model.response.MembershipResponse;
import com.poc.auth.model.response.TenantResponse;
import com.poc.auth.model.response.UserResponse;
import com.poc.auth.security.CustomUserDetails;
import com.poc.auth.security.JwtTokenProvider;
import com.poc.auth.security.TokenBlacklistService;
import com.poc.auth.service.AuditService;
import com.poc.auth.service.AuthService;
import com.poc.auth.service.MembershipQueryService;
import com.poc.auth.service.TenantQueryService;
import com.poc.auth.service.TwoFactorService;
import com.poc.auth.profile.service.CachedAvatarUrlService;
import com.poc.auth.validation.PasswordValidator;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final int CODE_EXPIRATION_MINUTES = 15;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${app.security.account-lockout.max-attempts:5}")
    private int maxFailedAttempts;

    @Value("${app.security.account-lockout.lockout-duration-minutes:15}")
    private int lockoutDurationMinutes;

    private final UserClient userClient;
    private final JpaRepositoryVerificationCode verificationCodeRepository;
    private final MembershipQueryService membershipQueryService;
    private final TenantQueryService tenantQueryService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final AuthMetrics authMetrics;
    private final CachedAvatarUrlService cachedAvatarUrlService;
    private final PasswordValidator passwordValidator;
    private final TwoFactorService twoFactorService;
    private final com.poc.auth.service.SessionService sessionService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        return login(request, null, null);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        authMetrics.recordLoginAttempt();
        Timer.Sample loginTimer = authMetrics.startLoginTimer();

        try {
            return processLogin(request, ipAddress, userAgent);
        } finally {
            authMetrics.recordLoginDuration(loginTimer);
        }
    }

    @Override
    @Transactional
    public AuthResponse selectTenant(SelectTenantRequest request) {
        // Validate temp token
        if (!jwtTokenProvider.validateToken(request.getTempToken())) {
            log.warn("Invalid or expired temp token for tenant selection");
            return AuthResponse.error("Invalid or expired token. Please login again.");
        }

        // Extract user ID from temp token
        UUID userId = jwtTokenProvider.getUserIdFromToken(request.getTempToken());
        InternalUserDto user = findUserByIdSafe(userId);
        if (user == null) {
            log.warn("User not found for tenant selection: {}", userId);
            return AuthResponse.error("User not found");
        }

        // Find membership for the selected tenant
        MembershipResponse selectedMembership = membershipQueryService.findActiveByUserId(userId).stream()
                .filter(m -> m.getTenantId().equals(request.getTenantId()))
                .findFirst()
                .orElse(null);

        if (selectedMembership == null) {
            log.warn("User {} does not have membership in tenant {}", userId, request.getTenantId());
            return AuthResponse.error("You are not a member of this organization");
        }

        // Check if 2FA is required
        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            // Generate new temp token with tenant context for 2FA
            String newTempToken = jwtTokenProvider.generateTempToken(user.getId(), selectedMembership.getId());

            // Send 2FA code
            try {
                twoFactorService.sendVerificationCode(user.getId(), user.getEmail(), user.getName(), null, null);
            } catch (IllegalStateException e) {
                log.warn("2FA rate limited for user: {}", user.getEmail());
                return AuthResponse.builder()
                        .success(false)
                        .message(e.getMessage())
                        .build();
            }

            log.info("2FA required for user: {} after tenant selection", user.getEmail());
            return AuthResponse.requires2FA(user.getEmail(), newTempToken);
        }

        // Generate tokens with the selected tenant context
        CustomUserDetails userDetails = new CustomUserDetails(user, selectedMembership.getTenantId(), selectedMembership.getId());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean rememberMe = Boolean.TRUE.equals(request.getRememberMe());
        String accessToken = jwtTokenProvider.generateTokenFromUserDetails(userDetails, rememberMe);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Create session in DB for revocation tracking
        trackSession(user.getId(), accessToken, jwtTokenProvider.getExpirationMs(rememberMe), null, null);

        authMetrics.recordLoginSuccess();
        authMetrics.recordTokenGeneration();
        authMetrics.incrementActiveSessions();

        log.info("User logged in after tenant selection: {} -> tenant {}", user.getEmail(), request.getTenantId());
        auditService.logLoginSuccess(user.getId(), user.getEmail());

        return AuthResponse.success(
                UserResponse.from(user, cachedAvatarUrlService, getUserRole(user), selectedMembership.getTenantId()),
                accessToken,
                refreshToken,
                jwtTokenProvider.getExpirationMs(rememberMe) / 1000,
                jwtTokenProvider.getRefreshExpirationMs() / 1000
        );
    }

    private AuthResponse processLogin(LoginRequest request, String ipAddress, String userAgent) {
        InternalUserDto user = findUserByEmailSafe(request.getEmail());

        if (user == null) {
            return handleLoginFailure(request.getEmail(), null, "User not found");
        }

        // Check if account is locked
        if (user.isAccountLocked()) {
            long remainingSeconds = java.time.Duration.between(Instant.now(), user.getLockedUntil()).getSeconds();
            log.warn("Login attempt for locked account: {}", user.getEmail());
            auditService.logLoginFailure(user.getEmail(), "Account locked");
            authMetrics.recordLoginFailure();
            return AuthResponse.builder()
                    .success(false)
                    .message(String.format("Account is locked. Try again in %d minutes.", (remainingSeconds / 60) + 1))
                    .build();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return handleLoginFailure(request.getEmail(), user, "Invalid password");
        }

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            return handleUnverifiedEmail(user);
        }

        // Reset failed attempts on successful login
        if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
            try {
                userClient.updateLoginState(user.getId(),
                        UpdateLoginStateRequest.builder().resetAttempts(true).build());
            } catch (Exception e) {
                log.warn("Failed to reset login attempts for user {} - login will proceed normally", user.getEmail(), e);
            }
        }

        // Check if user has multiple tenants and no tenant selected
        if (request.getMembershipId() == null) {
            List<MembershipResponse> activeMemberships = membershipQueryService.findActiveByUserId(user.getId());
            if (activeMemberships.size() > 1) {
                // User must select which tenant to login to
                String tempToken = jwtTokenProvider.generateTempToken(user.getId(), null);
                List<AccessContextResponse.TenantInfo> tenantInfos = activeMemberships.stream()
                        .map(m -> {
                            try {
                                var tenant = tenantQueryService.findById(m.getTenantId());
                                if (tenant == null) return null;
                                List<String> roleNames = m.getRoles() != null
                                        ? m.getRoles().stream().map(r -> r.getCode()).collect(java.util.stream.Collectors.toList())
                                        : java.util.Collections.emptyList();
                                return AccessContextResponse.TenantInfo.builder()
                                        .id(tenant.getId())
                                        .name(tenant.getName())
                                        .slug(tenant.getSlug())
                                        .type(tenant.getTenantType())
                                        .status(tenant.getStatus())
                                        .parentId(tenant.getParentTenantId())
                                        .roles(roleNames)
                                        .build();
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(t -> t != null)
                        .collect(java.util.stream.Collectors.toList());

                log.info("User {} has multiple tenants ({}), requiring selection", user.getEmail(), activeMemberships.size());
                return AuthResponse.requiresTenantSelection(user.getEmail(), tempToken, tenantInfos);
            }
        }

        // Check if 2FA is enabled
        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            return handle2FARequired(user, request, ipAddress, userAgent);
        }

        return authenticateAndGenerateTokens(user, request, ipAddress, userAgent);
    }

    private AuthResponse handle2FARequired(InternalUserDto user, LoginRequest request, String ipAddress, String userAgent) {
        // Resolve membership for temp token
        MembershipResponse selectedMembership = resolveActiveMembership(user, request.getMembershipId());
        UUID membershipId = selectedMembership != null ? selectedMembership.getId() : null;

        // Generate temporary token for 2FA flow
        String tempToken = jwtTokenProvider.generateTempToken(user.getId(), membershipId);

        // Send 2FA code
        try {
            twoFactorService.sendVerificationCode(user.getId(), user.getEmail(), user.getName(), ipAddress, userAgent);
        } catch (IllegalStateException e) {
            // Rate limited
            log.warn("2FA rate limited for user: {}", user.getEmail());
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }

        log.info("2FA required for user: {}, code sent to email", user.getEmail());
        return AuthResponse.requires2FA(user.getEmail(), tempToken);
    }

    private AuthResponse handleLoginFailure(String email, InternalUserDto user, String reason) {
        auditService.logLoginFailure(email, reason);
        authMetrics.recordLoginFailure();

        // If user exists, increment failed attempts and possibly lock account
        if (user != null) {
            int newAttempts = (user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0) + 1;

            if (newAttempts >= maxFailedAttempts) {
                Instant lockedUntil = Instant.now().plusSeconds((long) lockoutDurationMinutes * 60);
                userClient.updateLoginState(user.getId(),
                        UpdateLoginStateRequest.builder()
                                .failedLoginAttempts(newAttempts)
                                .lockedUntil(lockedUntil)
                                .build());
                log.warn("Account locked due to {} failed attempts: {}", maxFailedAttempts, email);
                return AuthResponse.builder()
                        .success(false)
                        .message(String.format("Account locked due to too many failed attempts. Try again in %d minutes.", lockoutDurationMinutes))
                        .build();
            }

            userClient.updateLoginState(user.getId(),
                    UpdateLoginStateRequest.builder()
                            .failedLoginAttempts(newAttempts)
                            .build());
            int remainingAttempts = maxFailedAttempts - newAttempts;
            log.info("Failed login attempt {} of {} for user: {}", newAttempts, maxFailedAttempts, email);

            if (remainingAttempts <= 2) {
                return AuthResponse.builder()
                        .success(false)
                        .message(String.format("Invalid email or password. %d attempts remaining before account lock.", remainingAttempts))
                        .build();
            }
        }

        return AuthResponse.error("Invalid email or password");
    }

    private AuthResponse handleUnverifiedEmail(InternalUserDto user) {
        createAndSendVerificationCode(user.getEmail(), user.getName(), VerificationType.EMAIL_VERIFICATION);
        authMetrics.recordEmailVerificationSent();
        log.info("Email not verified, verification email queued for: {}", user.getEmail());

        return AuthResponse.builder()
                .success(false)
                .message("Email not verified. A new verification code has been sent to your email.")
                .requiresVerification(true)
                .email(user.getEmail())
                .build();
    }

    private AuthResponse authenticateAndGenerateTokens(InternalUserDto user, LoginRequest request,
                                                        String ipAddress, String userAgent) {
        // Build CustomUserDetails directly from DTO (no AuthenticationManager - user-service owns the user)
        CustomUserDetails userDetails = new CustomUserDetails(user);

        MembershipResponse selectedMembership = resolveActiveMembership(user, request.getMembershipId());
        if (selectedMembership != null) {
            userDetails.setTenantContext(selectedMembership.getTenantId(), selectedMembership.getId());
            log.debug("Tenant context set for user {}: tenant={}, membership={}",
                    user.getEmail(), selectedMembership.getTenantId(), selectedMembership.getId());
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Check rememberMe flag - extends token validity to 30 days
        boolean rememberMe = Boolean.TRUE.equals(request.getRememberMe());
        String accessToken = jwtTokenProvider.generateTokenFromUserDetails(userDetails, rememberMe);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // Create session in DB for revocation tracking
        trackSession(user.getId(), accessToken, jwtTokenProvider.getExpirationMs(rememberMe), ipAddress, userAgent);

        authMetrics.recordLoginSuccess();
        authMetrics.recordTokenGeneration();
        authMetrics.incrementActiveSessions();

        log.info("User logged in: {} (rememberMe={})", user.getEmail(), rememberMe);
        auditService.logLoginSuccess(user.getId(), user.getEmail());

        UUID tenantId = selectedMembership != null ? selectedMembership.getTenantId() : null;
        return AuthResponse.success(
                UserResponse.from(user, cachedAvatarUrlService, getUserRole(user), tenantId),
                accessToken,
                refreshToken,
                jwtTokenProvider.getExpirationMs(rememberMe) / 1000,
                jwtTokenProvider.getRefreshExpirationMs() / 1000
        );
    }

    private MembershipResponse resolveActiveMembership(InternalUserDto user, UUID requestedMembershipId) {
        if (requestedMembershipId != null) {
            try {
                MembershipResponse requested = membershipQueryService.findById(requestedMembershipId);
                if (requested != null && requested.getUserId().equals(user.getId())
                        && "ACTIVE".equalsIgnoreCase(requested.getStatus())) {
                    return requested;
                }
            } catch (Exception e) {
                log.warn("Failed to find membership {}: {}", requestedMembershipId, e.getMessage());
            }
            log.warn("Invalid membership selection for user: {}", user.getEmail());
        }

        try {
            List<MembershipResponse> activeMemberships = membershipQueryService.findActiveByUserId(user.getId());
            return activeMemberships.isEmpty() ? null : activeMemberships.get(0);
        } catch (Exception e) {
            log.error("CRITICAL: Failed to resolve memberships for user {} ({}). User will see currentTenantId=null and be redirected to onboarding!",
                    user.getEmail(), user.getId(), e);
            return null;
        }
    }

    /**
     * Get the primary role for a user based on their active membership.
     * Now delegates to user-service for role data.
     */
    private String getUserRole(InternalUserDto user) {
        MembershipResponse activeMembership = resolveActiveMembership(user, null);
        if (activeMembership == null || activeMembership.getRoleIds() == null || activeMembership.getRoleIds().isEmpty()) {
            return RoleCode.USER.getCode();
        }

        if (activeMembership.getRoles() != null && !activeMembership.getRoles().isEmpty()) {
            for (var role : activeMembership.getRoles()) {
                String code = role.getCode().toUpperCase();
                if (RoleCode.ADMIN.getCode().equals(code) || "ADMINISTRATOR".equals(code)) {
                    return RoleCode.ADMIN.getCode();
                }
            }
            for (var role : activeMembership.getRoles()) {
                if (RoleCode.MANAGER.getCode().equalsIgnoreCase(role.getCode())) {
                    return RoleCode.MANAGER.getCode();
                }
            }
            return activeMembership.getRoles().get(0).getCode().toUpperCase();
        }

        return RoleCode.USER.getCode();
    }

    private void createAndSendVerificationCode(String email, String name, VerificationType type) {
        String code = generateVerificationCode();
        verificationCodeRepository.invalidatePreviousCodes(email, type);

        VerificationCode verificationCode = VerificationCode.builder()
                .email(email)
                .code(code)
                .type(type)
                .expiresAt(Instant.now().plus(CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES))
                .used(false)
                .build();
        verificationCodeRepository.save(verificationCode);

        NotificationRequest notification = (type == VerificationType.EMAIL_VERIFICATION)
                ? NotificationRequest.verification(email, name, code)
                : NotificationRequest.passwordReset(email, name, code);
        notificationService.send(notification);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return AuthResponse.error("Passwords do not match");
        }

        // Validate password strength
        PasswordValidator.ValidationResult passwordValidation = passwordValidator.validate(request.getPassword());
        if (!passwordValidation.isValid()) {
            return AuthResponse.error(passwordValidation.getErrorMessage());
        }

        // Register user via user-service (encode password here, send encoded)
        InternalUserDto user;
        try {
            user = userClient.registerInternal(RegisterInternalRequest.builder()
                    .email(request.getEmail().toLowerCase())
                    .encodedPassword(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .build());
        } catch (Exception e) {
            log.warn("Registration failed: {}", e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("already in use")) {
                return AuthResponse.error("Email already registered");
            }
            return AuthResponse.error("Registration failed. Please try again.");
        }

        authMetrics.recordRegistration();
        auditService.logRegistration(user.getId(), user.getEmail());

        createAndSendVerificationCode(user.getEmail(), user.getName(), VerificationType.EMAIL_VERIFICATION);
        authMetrics.recordEmailVerificationSent();
        log.info("User registered and verification email queued: {}", user.getEmail());

        return AuthResponse.success("Registration successful! Please check your email for verification code.");
    }

    @Override
    public UserResponse getCurrentUser(UUID userId) {
        InternalUserDto user = userClient.findById(userId);
        MembershipResponse membership = resolveActiveMembership(user, null);
        UUID tenantId = membership != null ? membership.getTenantId() : null;
        return UserResponse.from(user, cachedAvatarUrlService, getUserRole(user), tenantId);
    }

    @Override
    public InternalUserDto getUserById(UUID userId) {
        return userClient.findById(userId);
    }

    @Override
    public AuthResponse logout(UUID userId, String accessToken, String refreshToken) {
        SecurityContextHolder.clearContext();

        blacklistTokenIfPresent(accessToken, "Access");
        blacklistTokenIfPresent(refreshToken, "Refresh");

        try {
            InternalUserDto user = findUserByIdSafe(userId);
            if (user != null) {
                authMetrics.decrementActiveSessions();
                log.info("User logged out: {}", user.getEmail());
                auditService.logLogout(user.getId(), user.getEmail());
            }
        } catch (Exception e) {
            log.warn("Could not fetch user for logout audit: {}", e.getMessage());
            authMetrics.decrementActiveSessions();
        }

        return AuthResponse.success("Logout successful!");
    }

    private void blacklistTokenIfPresent(String token, String tokenType) {
        if (token == null || token.isEmpty()) {
            return;
        }
        String tokenId = jwtTokenProvider.getTokenIdFromToken(token);
        if (tokenId != null) {
            long expiration = jwtTokenProvider.getRemainingExpirationMs(token);
            tokenBlacklistService.blacklistToken(tokenId, expiration);
            log.debug("{} token blacklisted: {}", tokenType, tokenId);
        }
    }

    @Override
    @Transactional
    public AuthResponse forgotPassword(String email) {
        InternalUserDto user = findUserByEmailSafe(email);

        // Always return success to prevent email enumeration
        if (user == null) {
            log.info("Password reset requested for non-existent email: {}", email);
            return AuthResponse.success("If this email exists, a verification code has been sent.");
        }

        createAndSendVerificationCode(user.getEmail(), user.getName(), VerificationType.PASSWORD_RESET);
        authMetrics.recordPasswordResetRequest();
        log.info("Password reset code generated and email queued for: {}", email);

        return AuthResponse.success("Verification code sent to " + email);
    }

    @Override
    @Transactional
    public AuthResponse verifyCode(VerifyCodeRequest request) {
        VerificationType type = parseVerificationType(request.getType());
        if (type == null) {
            return AuthResponse.error("Invalid verification type");
        }

        VerificationCode verificationCode = findValidCode(request.getEmail(), request.getCode(), type);
        if (verificationCode == null) {
            log.warn("Invalid verification code attempt for email: {}", request.getEmail());
            return AuthResponse.error("Invalid or expired verification code");
        }

        if (verificationCode.isExpired()) {
            log.warn("Expired verification code attempt for email: {}", request.getEmail());
            return AuthResponse.error("Verification code has expired");
        }

        if (type == VerificationType.EMAIL_VERIFICATION) {
            return completeEmailVerification(verificationCode, request.getEmail());
        }

        // For PASSWORD_RESET, code is marked as used in resetPassword method
        log.info("Verification code validated for email: {}", request.getEmail());
        return AuthResponse.success("Code verified successfully");
    }

    private VerificationType parseVerificationType(String type) {
        try {
            return VerificationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private VerificationCode findValidCode(String email, String code, VerificationType type) {
        return verificationCodeRepository
                .findByEmailIgnoreCaseAndCodeAndTypeAndUsedFalse(email, code, type)
                .orElse(null);
    }

    private AuthResponse completeEmailVerification(VerificationCode verificationCode, String email) {
        verificationCode.setUsed(true);
        verificationCodeRepository.save(verificationCode);

        InternalUserDto user = findUserByEmailSafe(email);
        if (user != null) {
            userClient.verifyEmail(user.getId());
            notificationService.send(NotificationRequest.welcome(user.getEmail(), user.getName()));
            log.info("Email verified and welcome email queued for: {}", email);
            return AuthResponse.success("Email verified successfully! Welcome to the platform.");
        }

        return AuthResponse.success("Code verified successfully");
    }

    @Override
    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return AuthResponse.error("Passwords do not match");
        }

        // Validate password strength
        PasswordValidator.ValidationResult passwordValidation = passwordValidator.validate(request.getNewPassword());
        if (!passwordValidation.isValid()) {
            return AuthResponse.error(passwordValidation.getErrorMessage());
        }

        VerificationCode verificationCode = findValidCode(
                request.getEmail(), request.getCode(), VerificationType.PASSWORD_RESET);

        if (verificationCode == null || verificationCode.isExpired()) {
            log.warn("Invalid reset password attempt for email: {}", request.getEmail());
            return AuthResponse.error("Invalid or expired verification code");
        }

        InternalUserDto user = findUserByEmailSafe(request.getEmail());
        if (user == null) {
            return AuthResponse.error("User not found");
        }

        // Update password via user-service (encode here, send encoded)
        userClient.updatePassword(user.getId(),
                UpdatePasswordRequest.builder()
                        .encodedPassword(passwordEncoder.encode(request.getNewPassword()))
                        .build());

        verificationCode.setUsed(true);
        verificationCodeRepository.save(verificationCode);

        log.info("Password reset successful for email: {}", request.getEmail());
        auditService.logPasswordReset(request.getEmail());
        return AuthResponse.success("Password reset successful. You can now login with your new password.");
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        AuthResponse validationError = validateRefreshToken(refreshToken);
        if (validationError != null) {
            return validationError;
        }

        try {
            return processTokenRefresh(refreshToken);
        } catch (Exception ex) {
            log.error("Error refreshing token: {}", ex.getMessage());
            auditService.logRefreshTokenFailure("Error: " + ex.getMessage());
            return AuthResponse.error("Failed to refresh token");
        }
    }

    private AuthResponse validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            auditService.logRefreshTokenFailure("Refresh token is required");
            return AuthResponse.error("Refresh token is required");
        }

        String tokenId = jwtTokenProvider.getTokenIdFromToken(refreshToken);
        if (tokenId != null && tokenBlacklistService.isBlacklisted(tokenId)) {
            log.warn("Attempted reuse of refresh token: {}", tokenId);
            auditService.logRefreshTokenFailure("Refresh token already used (potential token theft)");
            return AuthResponse.error("Refresh token has already been used");
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            auditService.logRefreshTokenFailure("Invalid or expired refresh token");
            return AuthResponse.error("Invalid or expired refresh token");
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            auditService.logRefreshTokenFailure("Invalid refresh token");
            return AuthResponse.error("Invalid refresh token");
        }

        return null;
    }

    private AuthResponse processTokenRefresh(String refreshToken) {
        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        InternalUserDto user = findUserByIdSafe(userId);

        if (user == null || !Boolean.TRUE.equals(user.getEnabled())) {
            auditService.logRefreshTokenFailure("User not found or disabled");
            return AuthResponse.error("User not found or disabled");
        }

        // Rotating refresh token: blacklist old token before generating new one
        String oldTokenId = jwtTokenProvider.getTokenIdFromToken(refreshToken);
        if (oldTokenId != null) {
            long remainingExpiration = jwtTokenProvider.getRemainingExpirationMs(refreshToken);
            tokenBlacklistService.blacklistToken(oldTokenId, remainingExpiration);
            log.debug("Old refresh token blacklisted: {}", oldTokenId);
        }

        // Create CustomUserDetails with tenant context preserved from active membership
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Restore tenant context from user's active membership
        List<MembershipResponse> activeMemberships = membershipQueryService.findActiveByUserId(user.getId());
        if (!activeMemberships.isEmpty()) {
            MembershipResponse activeMembership = activeMemberships.get(0);
            userDetails.setTenantContext(activeMembership.getTenantId(), activeMembership.getId());
            log.debug("Token refresh: restored tenant context for user {}: tenant={}, membership={}",
                    user.getEmail(), activeMembership.getTenantId(), activeMembership.getId());
        }

        // Generate tokens with full user context (including tenant info)
        String newAccessToken = jwtTokenProvider.generateTokenFromUserDetails(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

        authMetrics.recordTokenRefresh();
        authMetrics.recordTokenGeneration();

        log.info("Token refreshed for user: {} (rotating refresh token)", user.getEmail());
        auditService.logRefreshTokenSuccess(user.getId(), user.getEmail());

        MembershipResponse membership = resolveActiveMembership(user, null);
        UUID tenantId = membership != null ? membership.getTenantId() : null;
        return AuthResponse.success(
                UserResponse.from(user, cachedAvatarUrlService, getUserRole(user), tenantId),
                newAccessToken,
                newRefreshToken,
                jwtTokenProvider.getExpirationMs() / 1000,
                jwtTokenProvider.getRefreshExpirationMs() / 1000
        );
    }

    @Override
    @Transactional
    public AuthResponse resendCode(ResendCodeRequest request) {
        VerificationType type = parseVerificationType(request.getType());
        if (type == null) {
            return AuthResponse.error("Invalid verification type. Use EMAIL_VERIFICATION or PASSWORD_RESET.");
        }

        InternalUserDto user = findUserByEmailSafe(request.getEmail());

        // For PASSWORD_RESET, always return success to prevent email enumeration
        if (user == null) {
            if (type == VerificationType.PASSWORD_RESET) {
                log.info("Resend code requested for non-existent email: {}", request.getEmail());
                return AuthResponse.success("If this email exists, a new verification code has been sent.");
            }
            return AuthResponse.error("Email not found");
        }

        // For EMAIL_VERIFICATION, check if already verified
        if (type == VerificationType.EMAIL_VERIFICATION && Boolean.TRUE.equals(user.getEmailVerified())) {
            return AuthResponse.error("Email is already verified");
        }

        createAndSendVerificationCode(user.getEmail(), user.getName(), type);
        log.info("Verification code resent for email: {} (type: {})", request.getEmail(), type);

        return AuthResponse.success("A new verification code has been sent to " + request.getEmail());
    }

    private String generateVerificationCode() {
        int code = SECURE_RANDOM.nextInt(1000000);
        return String.format("%06d", code);
    }

    @Override
    @Transactional
    public AuthResponse verify2FA(TwoFactorVerifyRequest request) {
        // Validate temp token
        String tempToken = request.getTempToken();

        if (!jwtTokenProvider.validateToken(tempToken)) {
            log.warn("Invalid or expired 2FA temp token");
            auditService.logLoginFailure("unknown", "Invalid 2FA temp token");
            return AuthResponse.builder()
                    .success(false)
                    .message("Session expired. Please login again.")
                    .build();
        }

        if (!jwtTokenProvider.is2FATempToken(tempToken)) {
            log.warn("Token is not a 2FA temp token");
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid token type. Please login again.")
                    .build();
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(tempToken);
        UUID membershipId = jwtTokenProvider.getMembershipIdFromToken(tempToken);

        InternalUserDto user = findUserByIdSafe(userId);
        if (user == null || !Boolean.TRUE.equals(user.getEnabled())) {
            log.warn("User not found or disabled for 2FA verification");
            return AuthResponse.builder()
                    .success(false)
                    .message("User not found or disabled.")
                    .build();
        }

        // Verify 2FA code
        if (!twoFactorService.verifyCode(userId, request.getCode())) {
            log.warn("Invalid 2FA code for user: {}", user.getEmail());
            auditService.logLoginFailure(user.getEmail(), "Invalid 2FA code");
            authMetrics.recordLoginFailure();
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid or expired verification code.")
                    .build();
        }

        // 2FA verified - generate full tokens
        return generateTokensAfter2FA(user, membershipId);
    }

    private AuthResponse generateTokensAfter2FA(InternalUserDto user, UUID membershipId) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UUID resolvedTenantId = null;

        // Set tenant context if membership was provided
        if (membershipId != null) {
            try {
                MembershipResponse membership = membershipQueryService.findById(membershipId);
                if (membership != null && membership.getUserId().equals(user.getId())
                        && "ACTIVE".equalsIgnoreCase(membership.getStatus())) {
                    userDetails.setTenantContext(membership.getTenantId(), membership.getId());
                    resolvedTenantId = membership.getTenantId();
                    log.debug("Tenant context set after 2FA for user {}: tenant={}, membership={}",
                            user.getEmail(), membership.getTenantId(), membership.getId());
                }
            } catch (Exception e) {
                log.warn("Failed to find membership {}: {}", membershipId, e.getMessage());
            }
        }

        // Fallback to first active membership if not resolved
        if (resolvedTenantId == null) {
            List<MembershipResponse> activeMemberships = membershipQueryService.findActiveByUserId(user.getId());
            if (!activeMemberships.isEmpty()) {
                MembershipResponse activeMembership = activeMemberships.get(0);
                userDetails.setTenantContext(activeMembership.getTenantId(), activeMembership.getId());
                resolvedTenantId = activeMembership.getTenantId();
            }
        }

        String accessToken = jwtTokenProvider.generateTokenFromUserDetails(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Create session in DB for revocation tracking
        trackSession(user.getId(), accessToken, jwtTokenProvider.getExpirationMs(), null, null);

        authMetrics.recordLoginSuccess();
        authMetrics.recordTokenGeneration();
        authMetrics.incrementActiveSessions();

        log.info("2FA verified successfully, user logged in: {}", user.getEmail());
        auditService.logLoginSuccess(user.getId(), user.getEmail());

        return AuthResponse.success(
                UserResponse.from(user, cachedAvatarUrlService, getUserRole(user), resolvedTenantId),
                accessToken,
                newRefreshToken,
                jwtTokenProvider.getExpirationMs() / 1000,
                jwtTokenProvider.getRefreshExpirationMs() / 1000
        );
    }

    /**
     * Track session in DB for revocation support.
     * Non-critical: failures are logged but don't block login.
     */
    private void trackSession(UUID userId, String accessToken, long expirationMs,
                              String ipAddress, String userAgent) {
        try {
            String tokenId = jwtTokenProvider.getTokenIdFromToken(accessToken);
            if (tokenId != null) {
                sessionService.createSession(userId, tokenId, expirationMs, ipAddress, userAgent);
            }
        } catch (Exception e) {
            log.warn("Failed to create session for user {}: {}", userId, e.getMessage());
        }
    }

    // =========================================================================
    // Helper methods for safe Feign calls (return null instead of throwing)
    // =========================================================================

    private InternalUserDto findUserByEmailSafe(String email) {
        try {
            return userClient.findByEmail(email);
        } catch (Exception e) {
            log.debug("User not found by email: {}", email);
            return null;
        }
    }

    private InternalUserDto findUserByIdSafe(UUID userId) {
        try {
            return userClient.findById(userId);
        } catch (Exception e) {
            log.debug("User not found by id: {}", userId);
            return null;
        }
    }
}
