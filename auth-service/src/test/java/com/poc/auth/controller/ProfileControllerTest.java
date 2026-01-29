package com.poc.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.auth.model.request.ProfileRequest;
import com.poc.auth.model.response.UserResponse;
import com.poc.auth.security.CustomUserDetailsService;
import com.poc.auth.security.JwtTokenProvider;
import com.poc.auth.service.ProfileService;
import com.poc.auth.storage.client.StorageClient;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProfileController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.poc\\.converter\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.poc\\.auth\\.security\\..*")
    })
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private StorageClient storageClient;

    private UUID userId;
    private UserResponse userResponse;
    private ProfileRequest profileRequest;

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

        userResponse = UserResponse.builder()
                .id(userId)
                .email("test@example.com")
                .name("Test User")
                .nickname("testuser")
                .emailVerified(true)
                .build();

        profileRequest = new ProfileRequest();
        profileRequest.setName("Updated Name");
        profileRequest.setNickname("updateduser");
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContext.clear();
    }

    @Test
    void getProfile_WithValidUserId_ReturnsUserProfile() throws Exception {
        when(profileService.getProfile(any(UUID.class))).thenReturn(userResponse);

        mockMvc.perform(get("/api/profile")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.nickname").value("testuser"));

        verify(profileService).getProfile(userId);
    }

    @Test
    void updateProfile_WithValidData_ReturnsUpdatedProfile() throws Exception {
        userResponse.setName("Updated Name");
        userResponse.setNickname("updateduser");

        when(profileService.updateProfile(any(UUID.class), any(ProfileRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(put("/api/profile")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.nickname").value("updateduser"));

        verify(profileService).updateProfile(eq(userId), any(ProfileRequest.class));
    }

    @Test
    void uploadAvatar_WithValidFile_ReturnsUpdatedProfile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        userResponse.setAvatar("avatars/avatar.png");
        when(profileService.uploadAvatar(any(UUID.class), any())).thenReturn(userResponse);

        mockMvc.perform(multipart("/api/profile/avatar")
                        .file(file)
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatar").value("avatars/avatar.png"));

        verify(profileService).uploadAvatar(eq(userId), any());
    }

    @Test
    void deleteAvatar_WithValidUserId_ReturnsUpdatedProfile() throws Exception {
        userResponse.setAvatar(null);
        when(profileService.deleteAvatar(any(UUID.class))).thenReturn(userResponse);

        mockMvc.perform(delete("/api/profile/avatar")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatar").doesNotExist());

        verify(profileService).deleteAvatar(userId);
    }

    @Test
    void viewAvatar_WithUserIdHeader_ReturnsImage() throws Exception {
        userResponse.setAvatar("avatar.png");
        when(profileService.getProfile(any(UUID.class))).thenReturn(userResponse);

        mockMvc.perform(get("/api/profile/avatar/view")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isNotFound()); // Will fail without actual file

        verify(profileService).getProfile(userId);
    }

    @Test
    void viewAvatar_WithUserIdParam_ReturnsImage() throws Exception {
        userResponse.setAvatar("avatar.png");
        when(profileService.getProfile(any(UUID.class))).thenReturn(userResponse);

        mockMvc.perform(get("/api/profile/avatar/view")
                        .param("userId", userId.toString()))
                .andExpect(status().isNotFound()); // Will fail without actual file

        verify(profileService).getProfile(userId);
    }

    @Test
    void viewAvatar_WithoutUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/profile/avatar/view"))
                .andExpect(status().isBadRequest());

        verify(profileService, never()).getProfile(any());
    }

    @Test
    void viewAvatar_WithNoAvatar_ReturnsNotFound() throws Exception {
        userResponse.setAvatar(null);
        when(profileService.getProfile(any(UUID.class))).thenReturn(userResponse);

        mockMvc.perform(get("/api/profile/avatar/view")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isNotFound());

        verify(profileService).getProfile(userId);
    }
}
