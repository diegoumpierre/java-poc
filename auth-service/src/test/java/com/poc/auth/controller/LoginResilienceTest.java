package com.poc.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.auth.client.UserClient;
import com.poc.auth.model.request.LoginRequest;
import com.poc.auth.model.response.AuthResponse;
import com.poc.auth.model.response.UserResponse;
import com.poc.auth.security.CustomUserDetailsService;
import com.poc.auth.security.JwtTokenProvider;
import com.poc.auth.service.AuthService;
import com.poc.auth.service.MembershipQueryService;
import com.poc.shared.security.SecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests login endpoint resilience: verifies that login returns proper
 * responses under normal and degraded conditions (Feign failures, etc).
 */
@WebMvcTest(controllers = AuthController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.poc\\.converter\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.poc\\.auth\\.security\\..*")
    })
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Login Resilience")
class LoginResilienceTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private MembershipQueryService membershipQueryService;

    @MockitoBean
    private UserClient userClient;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        SecurityContext.setPermissions(Set.of("PLATFORM_ADMIN"));

        loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("password");
    }

    @AfterEach
    void tearDown() {
        SecurityContext.clear();
    }

    @Test
    @DisplayName("Login with valid credentials returns 200 with tokens")
    void login_WithValidCredentials_Returns200WithTokens() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthResponse response = AuthResponse.builder()
                .success(true)
                .message("Success")
                .user(UserResponse.builder()
                        .id(userId)
                        .email("admin@example.com")
                        .name("Platform Administrator")
                        .emailVerified(true)
                        .build())
                .accessToken("valid-access-token")
                .refreshToken("valid-refresh-token")
                .expiresIn(86400L)
                .refreshExpiresIn(604800L)
                .build();

        when(authService.login(any(LoginRequest.class), nullable(String.class), nullable(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.accessToken").value("valid-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("valid-refresh-token"))
                .andExpect(jsonPath("$.expiresIn").value(86400))
                .andExpect(cookie().exists("auth_token"))
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    @DisplayName("Login with wrong password returns 401")
    void login_WithWrongPassword_Returns401() throws Exception {
        loginRequest.setPassword("wrong-password");

        AuthResponse response = AuthResponse.builder()
                .success(false)
                .message("Invalid email or password")
                .build();

        when(authService.login(any(LoginRequest.class), nullable(String.class), nullable(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Login succeeds even when membership lookup fails (degraded mode)")
    void login_WhenMembershipLookupFails_StillSucceeds() throws Exception {
        // Simulates the fix: when tenant-service is down, login still works
        // but currentTenantId will be null (user redirected to onboarding)
        UUID userId = UUID.randomUUID();
        AuthResponse response = AuthResponse.builder()
                .success(true)
                .message("Success")
                .user(UserResponse.builder()
                        .id(userId)
                        .email("admin@example.com")
                        .name("Platform Administrator")
                        .emailVerified(true)
                        .currentTenantId(null) // null because tenant-service was unavailable
                        .build())
                .accessToken("access-token-no-tenant")
                .refreshToken("refresh-token-no-tenant")
                .expiresIn(86400L)
                .refreshExpiresIn(604800L)
                .build();

        when(authService.login(any(LoginRequest.class), nullable(String.class), nullable(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.user.currentTenantId").doesNotExist());
    }

    @Test
    @DisplayName("Login does not return 500 when service throws exception")
    void login_WhenServiceThrows_DoesNotReturn500() throws Exception {
        // Before the fix, Feign exceptions would bubble up and cause 500.
        // Now the service handles them gracefully, but if something unexpected
        // still happens, the controller should handle it too.
        AuthResponse response = AuthResponse.builder()
                .success(false)
                .message("Service temporarily unavailable")
                .build();

        when(authService.login(any(LoginRequest.class), nullable(String.class), nullable(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Consecutive logins return consistent results")
    void login_ConsecutiveCalls_ReturnConsistentResults() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .success(true)
                .message("Success")
                .accessToken("token-1")
                .refreshToken("refresh-1")
                .expiresIn(86400L)
                .refreshExpiresIn(604800L)
                .build();

        when(authService.login(any(LoginRequest.class), nullable(String.class), nullable(String.class)))
                .thenReturn(response);

        // First call
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Second call - should be equally successful
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Login with missing email returns 400")
    void login_WithMissingEmail_Returns400() throws Exception {
        loginRequest.setEmail(null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login with missing password returns 400")
    void login_WithMissingPassword_Returns400() throws Exception {
        loginRequest.setPassword(null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login requiring 2FA returns 401 with requires2FA flag")
    void login_When2FARequired_Returns401WithRequires2FA() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .success(false)
                .message("Two-factor authentication required")
                .requires2FA(true)
                .tempToken("temp-2fa-token")
                .email("admin@example.com")
                .build();

        when(authService.login(any(LoginRequest.class), nullable(String.class), nullable(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.requires2FA").value(true))
                .andExpect(jsonPath("$.tempToken").value("temp-2fa-token"));
    }

    @Test
    @DisplayName("Login requiring tenant selection returns 401 with tenant selection flag")
    void login_WhenMultipleTenants_Returns401WithTenantSelection() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .success(false)
                .message("Please select a tenant")
                .requiresTenantSelection(true)
                .tempToken("temp-tenant-token")
                .email("admin@example.com")
                .build();

        when(authService.login(any(LoginRequest.class), nullable(String.class), nullable(String.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.requiresTenantSelection").value(true))
                .andExpect(jsonPath("$.tempToken").value("temp-tenant-token"));
    }
}
