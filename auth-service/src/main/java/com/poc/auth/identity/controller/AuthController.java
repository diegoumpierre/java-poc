package com.poc.auth.controller;

import com.poc.auth.model.request.ForgotPasswordRequest;
import com.poc.auth.model.request.LoginRequest;
import com.poc.auth.model.request.RegisterRequest;
import com.poc.auth.model.request.ResendCodeRequest;
import com.poc.auth.model.request.ResetPasswordRequest;
import com.poc.auth.model.request.SelectTenantRequest;
import com.poc.auth.model.request.SwitchTenantRequest;
import com.poc.auth.model.request.TwoFactorVerifyRequest;
import com.poc.auth.model.request.VerifyCodeRequest;
import com.poc.auth.model.response.AccessContextResponse;
import com.poc.auth.model.response.AuthResponse;
import com.poc.auth.model.response.MembershipResponse;
import com.poc.auth.model.response.UserResponse;
import com.poc.auth.client.UserClient;
import com.poc.auth.service.AuthService;
import com.poc.auth.service.MembershipQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;
    private final MembershipQueryService membershipQueryService;
    private final UserClient userClient;

    @Value("${app.security.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.security.cookie.max-age:604800}")
    private int cookieMaxAge; // 7 days default

    /**
     * POST /api/auth/login
     * Authenticate user with email and password
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {

        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse authResponse = authService.login(request, ipAddress, userAgent);

        // Set HTTP-only cookies for JWT tokens (access and refresh)
        if (authResponse.getAccessToken() != null) {
            Cookie authCookie = new Cookie("auth_token", authResponse.getAccessToken());
            authCookie.setHttpOnly(true);
            authCookie.setSecure(cookieSecure);
            authCookie.setPath("/");
            authCookie.setMaxAge(Math.toIntExact(authResponse.getExpiresIn()));
            authCookie.setAttribute("SameSite", "Strict");
            response.addCookie(authCookie);
        }

        if (authResponse.getRefreshToken() != null) {
            Cookie refreshCookie = new Cookie("refresh_token", authResponse.getRefreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(cookieSecure);
            refreshCookie.setPath("/api/auth");
            refreshCookie.setMaxAge(Math.toIntExact(authResponse.getRefreshExpiresIn()));
            refreshCookie.setAttribute("SameSite", "Strict");
            response.addCookie(refreshCookie);
        }

        if (!authResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        }

        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/select-tenant
     * Complete login by selecting a tenant (for users with multiple tenants)
     */
    @PostMapping("/select-tenant")
    @Operation(summary = "Select tenant", description = "Complete login by selecting a tenant for users with multiple tenants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid tenant selection"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public ResponseEntity<AuthResponse> selectTenant(
            @Valid @RequestBody SelectTenantRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.selectTenant(request);

        if (authResponse.isSuccess() && authResponse.getAccessToken() != null) {
            Cookie authCookie = new Cookie("auth_token", authResponse.getAccessToken());
            authCookie.setHttpOnly(true);
            authCookie.setSecure(cookieSecure);
            authCookie.setPath("/");
            authCookie.setMaxAge(Math.toIntExact(authResponse.getExpiresIn()));
            authCookie.setAttribute("SameSite", "Strict");
            response.addCookie(authCookie);

            if (authResponse.getRefreshToken() != null) {
                Cookie refreshCookie = new Cookie("refresh_token", authResponse.getRefreshToken());
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(cookieSecure);
                refreshCookie.setPath("/api/auth");
                refreshCookie.setMaxAge(Math.toIntExact(authResponse.getRefreshExpiresIn()));
                refreshCookie.setAttribute("SameSite", "Strict");
                response.addCookie(refreshCookie);
            }
        }

        if (!authResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        }

        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/register
     * Register a new user account
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration successful"),
            @ApiResponse(responseCode = "409", description = "Email already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);

        if (!authResponse.isSuccess()) {
            if (authResponse.getMessage() != null && authResponse.getMessage().contains("already registered")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(authResponse);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authResponse);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    /**
     * GET /api/auth/me
     * Get authenticated user profile
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get authenticated user profile")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(authService.getCurrentUser(java.util.UUID.fromString(userId)));
    }

    /**
     * GET /api/auth/context
     * Get complete access context for the authenticated user
     *
     * Returns:
     * - User info (id, email, name, avatar)
     * - Tenant info (current tenant, available tenants)
     * - Access control (roles, permissions, entitlements)
     * - Computed flags (isAdmin, isSuperAdmin, isReseller)
     *
     * Note: Menu should be fetched from organization-service via /api/organizations/menus
     */
    @GetMapping("/context")
    @Operation(summary = "Get access context", description = "Get complete access context including user, tenant, roles, permissions and entitlements")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access context retrieved",
                    content = @Content(schema = @Schema(implementation = AccessContextResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<AccessContextResponse> getAccessContext(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        return ResponseEntity.ok(userClient.getAccessContext(userId, tenantId));
    }

    /**
     * POST /api/auth/switch-tenant
     * Switch to a different tenant (for users with multiple tenants)
     */
    @PostMapping("/switch-tenant")
    @Operation(summary = "Switch tenant", description = "Switch to a different tenant for users with multiple tenants. Returns new access context.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant switched successfully",
                    content = @Content(schema = @Schema(implementation = AccessContextResponse.class))),
            @ApiResponse(responseCode = "400", description = "User is not a member of the requested tenant"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<AccessContextResponse> switchTenant(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody SwitchTenantRequest request) {
        try {
            AccessContextResponse context = userClient.getAccessContextForTenant(
                    userId, request.getTenantId());
            return ResponseEntity.ok(context);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/auth/me/memberships
     * Get current user's memberships (organizations)
     */
    @GetMapping("/me/memberships")
    @Operation(summary = "Get current user memberships", description = "Get all organizations/tenants the current user belongs to")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Memberships retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<java.util.List<MembershipResponse>> getCurrentUserMemberships(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(membershipQueryService.findByUserId(java.util.UUID.fromString(userId)));
    }

    /**
     * POST /api/auth/logout
     * Logout current user
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout current user and invalidate tokens")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<AuthResponse> logout(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @CookieValue(name = "auth_token", required = false) String cookieAccessToken,
            @CookieValue(name = "refresh_token", required = false) String cookieRefreshToken,
            HttpServletResponse response) {

        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        } else if (cookieAccessToken != null) {
            accessToken = cookieAccessToken;
        }

        Cookie authCookie = new Cookie("auth_token", null);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(cookieSecure);
        authCookie.setPath("/");
        authCookie.setMaxAge(0);

        Cookie refreshCookieToDelete = new Cookie("refresh_token", null);
        refreshCookieToDelete.setHttpOnly(true);
        refreshCookieToDelete.setSecure(cookieSecure);
        refreshCookieToDelete.setPath("/api/auth");
        refreshCookieToDelete.setMaxAge(0);

        response.addCookie(authCookie);
        response.addCookie(refreshCookieToDelete);

        return ResponseEntity.ok(authService.logout(
                java.util.UUID.fromString(userId),
                accessToken,
                cookieRefreshToken
        ));
    }

    /**
     * POST /api/auth/forgot-password
     * Request password reset
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Request password reset - sends a 6-digit verification code to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent to email"),
            @ApiResponse(responseCode = "400", description = "Invalid email")
    })
    public ResponseEntity<AuthResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request.getEmail()));
    }

    /**
     * POST /api/auth/verify-code
     * Verify the code sent via email
     */
    @PostMapping("/verify-code")
    @Operation(summary = "Verify code", description = "Verify the 6-digit code sent via email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired code")
    })
    public ResponseEntity<AuthResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        AuthResponse authResponse = authService.verifyCode(request);
        if (!authResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authResponse);
        }
        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/resend-code
     * Resend verification code
     */
    @PostMapping("/resend-code")
    @Operation(summary = "Resend code", description = "Resend a verification code to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or email already verified")
    })
    public ResponseEntity<AuthResponse> resendCode(@Valid @RequestBody ResendCodeRequest request) {
        AuthResponse authResponse = authService.resendCode(request);
        if (!authResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authResponse);
        }
        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/reset-password
     * Reset password using verified code
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using the verified 6-digit code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "400", description = "Invalid code or passwords don't match")
    })
    public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        AuthResponse authResponse = authService.resetPassword(request);
        if (!authResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authResponse);
        }
        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/refresh-token
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String cookieRefreshToken,
            @RequestBody(required = false) String bodyRefreshToken,
            HttpServletResponse response) {

        String refreshToken = cookieRefreshToken != null ? cookieRefreshToken : bodyRefreshToken;
        AuthResponse authResponse = authService.refreshToken(refreshToken);

        if (authResponse.isSuccess() && authResponse.getAccessToken() != null) {
            Cookie authCookie = new Cookie("auth_token", authResponse.getAccessToken());
            authCookie.setHttpOnly(true);
            authCookie.setSecure(cookieSecure);
            authCookie.setPath("/");
            authCookie.setMaxAge(Math.toIntExact(authResponse.getExpiresIn()));
            authCookie.setAttribute("SameSite", "Strict");
            response.addCookie(authCookie);

            if (authResponse.getRefreshToken() != null) {
                Cookie refreshCookie = new Cookie("refresh_token", authResponse.getRefreshToken());
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(cookieSecure);
                refreshCookie.setPath("/api/auth");
                refreshCookie.setMaxAge(Math.toIntExact(authResponse.getRefreshExpiresIn()));
                refreshCookie.setAttribute("SameSite", "Strict");
                response.addCookie(refreshCookie);
            }
        }

        if (!authResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        }

        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/verify-2fa
     * Complete login by verifying 2FA code
     */
    @PostMapping("/verify-2fa")
    @Operation(summary = "Verify 2FA", description = "Complete login by verifying the 2FA code sent to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA verified, login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired code"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<AuthResponse> verify2FA(
            @Valid @RequestBody TwoFactorVerifyRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.verify2FA(request);

        if (authResponse.isSuccess() && authResponse.getAccessToken() != null) {
            Cookie authCookie = new Cookie("auth_token", authResponse.getAccessToken());
            authCookie.setHttpOnly(true);
            authCookie.setSecure(cookieSecure);
            authCookie.setPath("/");
            authCookie.setMaxAge(Math.toIntExact(authResponse.getExpiresIn()));
            authCookie.setAttribute("SameSite", "Strict");
            response.addCookie(authCookie);

            if (authResponse.getRefreshToken() != null) {
                Cookie refreshCookie = new Cookie("refresh_token", authResponse.getRefreshToken());
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(cookieSecure);
                refreshCookie.setPath("/api/auth");
                refreshCookie.setMaxAge(Math.toIntExact(authResponse.getRefreshExpiresIn()));
                refreshCookie.setAttribute("SameSite", "Strict");
                response.addCookie(refreshCookie);
            }
        }

        if (!authResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
        }

        return ResponseEntity.ok(authResponse);
    }

    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
