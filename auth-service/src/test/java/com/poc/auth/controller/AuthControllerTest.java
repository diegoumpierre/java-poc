package com.poc.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.auth.model.request.LoginRequest;
import com.poc.auth.model.request.RegisterRequest;
import com.poc.auth.model.request.ResetPasswordRequest;
import com.poc.auth.model.request.VerifyCodeRequest;
import com.poc.auth.model.response.AccessContextResponse;
import com.poc.auth.model.response.AuthResponse;
import com.poc.auth.model.response.UserResponse;
import com.poc.auth.client.UserClient;
import com.poc.auth.security.CustomUserDetailsService;
import com.poc.auth.security.JwtTokenProvider;
import com.poc.auth.service.AuthService;
import com.poc.auth.service.MembershipQueryService;
import com.poc.shared.security.SecurityContext;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.poc\\.converter\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.poc\\.auth\\.security\\..*")
    })
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

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
    private RegisterRequest registerRequest;
    private AuthResponse authResponse;
    private UserResponse userResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        SecurityContext.setPermissions(Set.of(
            "KANBAN_MANAGE", "KANBAN_VIEW", "FINANCE_MANAGE", "FINANCE_APPROVE",
            "HELPDESK_MANAGE", "HELPDESK_RESPOND", "CUSTOMER_MANAGE",
            "BPF_MANAGE", "RH_MANAGE", "PERICIA_MANAGE", "BILLING_MANAGE",
            "TENANT_MANAGE", "MENU_MANAGE", "RESELLER_MANAGE",
            "USER_MANAGE", "ROLE_MANAGE", "PLATFORM_ADMIN"
        ));

        userId = UUID.randomUUID();

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        registerRequest.setName("Test User");

        authResponse = AuthResponse.builder()
                .success(true)
                .message("Success")
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .expiresIn(86400L)
                .refreshExpiresIn(604800L)
                .build();

        userResponse = UserResponse.builder()
                .id(userId)
                .email("test@example.com")
                .name("Test User")
                .emailVerified(true)
                .build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContext.clear();
    }

    @Test
    void login_WithValidCredentials_ReturnsAuthResponse() throws Exception {
        when(authService.login(any(LoginRequest.class), nullable(String.class), nullable(String.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").value("test-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("test-refresh-token"))
                .andExpect(cookie().exists("auth_token"))
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    void login_WithInvalidEmail_ReturnsBadRequest() throws Exception {
        loginRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WithValidData_ReturnsAuthResponse() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void register_WithInvalidEmail_ReturnsBadRequest() throws Exception {
        registerRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCurrentUser_WithValidUserId_ReturnsUserResponse() throws Exception {
        when(authService.getCurrentUser(any(UUID.class))).thenReturn(userResponse);

        mockMvc.perform(get("/api/auth/me")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void logout_WithValidUserId_ReturnsSuccess() throws Exception {
        when(authService.logout(any(UUID.class), any(), any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/logout")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("auth_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));
    }

    @Test
    void forgotPassword_WithValidEmail_ReturnsSuccess() throws Exception {
        when(authService.forgotPassword(anyString())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void verifyCode_WithValidCode_ReturnsSuccess() throws Exception {
        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setEmail("test@example.com");
        request.setCode("123456");
        request.setType("EMAIL_VERIFICATION");

        when(authService.verifyCode(any(VerifyCodeRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void resetPassword_WithValidData_ReturnsSuccess() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setCode("123434");
        request.setNewPassword("newpassword123");
        request.setConfirmPassword("newpassword123");

        when(authService.resetPassword(any(ResetPasswordRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void refreshToken_WithValidToken_ReturnsNewTokens() throws Exception {
        when(authService.refreshToken(anyString())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/refresh-token")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "valid-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists("auth_token"));
    }

    @Test
    void getAccessContext_WithValidHeaders_ReturnsContext() throws Exception {
        UUID tenantId = UUID.randomUUID();

        AccessContextResponse contextResponse = AccessContextResponse.builder()
                .userId(userId)
                .email("test@example.com")
                .name("Test User")
                .hasTenant(true)
                .roles(Set.of("ADMIN"))
                .permissions(Set.of("BILLING_MANAGE"))
                .entitlements(Set.of("HELPDESK_MODULE", "FINANCE_MODULE"))
                .isAdmin(true)
                .isSuperAdmin(false)
                .isReseller(false)
                .isPlatformAdmin(false)
                .build();

        when(userClient.getAccessContext(userId.toString(), tenantId.toString()))
                .thenReturn(contextResponse);

        mockMvc.perform(get("/api/auth/context")
                        .header("X-User-Id", userId.toString())
                        .header("X-Tenant-Id", tenantId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.entitlements").isArray())
                .andExpect(jsonPath("$.isAdmin").value(true))
                .andExpect(jsonPath("$.isPlatformAdmin").value(false));
    }

    @Test
    void getAccessContext_WithEntitlements_SerializesAsStringArray() throws Exception {
        UUID tenantId = UUID.randomUUID();

        AccessContextResponse contextResponse = AccessContextResponse.builder()
                .userId(userId)
                .email("test@example.com")
                .entitlements(Set.of("HELPDESK_MODULE"))
                .isAdmin(true)
                .isSuperAdmin(false)
                .isReseller(false)
                .isPlatformAdmin(true)
                .build();

        when(userClient.getAccessContext(userId.toString(), tenantId.toString()))
                .thenReturn(contextResponse);

        mockMvc.perform(get("/api/auth/context")
                        .header("X-User-Id", userId.toString())
                        .header("X-Tenant-Id", tenantId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entitlements[0]").value("HELPDESK_MODULE"))
                // Boolean fields must use "is" prefix for frontend compatibility
                .andExpect(jsonPath("$.isAdmin").value(true))
                .andExpect(jsonPath("$.isPlatformAdmin").value(true))
                // Must NOT have bare boolean names
                .andExpect(jsonPath("$.admin").doesNotExist())
                .andExpect(jsonPath("$.platformAdmin").doesNotExist());
    }
}
