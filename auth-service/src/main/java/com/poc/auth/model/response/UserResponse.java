package com.poc.auth.model.response;

import com.poc.auth.client.dto.InternalUserDto;
import com.poc.auth.profile.service.CachedAvatarUrlService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String email;
    private String name;
    private String nickname;
    private String avatar;
    private String role;
    private boolean emailVerified;
    private boolean enabled;
    private int failedLoginAttempts;
    private boolean accountLocked;
    private Instant lockedUntil;
    private boolean twoFactorEnabled;
    private String twoFactorMethod;
    private UUID currentTenantId;
    private List<MembershipResponse> memberships;
    private Instant createdAt;
    private Instant updatedAt;

    private static final String DEFAULT_ROLE = "USER";

    /**
     * Convert InternalUserDto to UserResponse
     */
    public static UserResponse from(InternalUserDto user) {
        return from(user, DEFAULT_ROLE, null);
    }

    /**
     * Convert InternalUserDto to UserResponse with role
     */
    public static UserResponse from(InternalUserDto user, String role) {
        return from(user, role, null);
    }

    /**
     * Convert InternalUserDto to UserResponse with role and tenant ID
     */
    public static UserResponse from(InternalUserDto user, String role, UUID currentTenantId) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(role != null ? role : DEFAULT_ROLE)
                .emailVerified(user.getEmailVerified() != null ? user.getEmailVerified() : false)
                .enabled(user.getEnabled() != null ? user.getEnabled() : true)
                .failedLoginAttempts(user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0)
                .accountLocked(user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now()))
                .lockedUntil(user.getLockedUntil())
                .twoFactorEnabled(user.getTwoFactorEnabled() != null ? user.getTwoFactorEnabled() : false)
                .twoFactorMethod(user.getTwoFactorMethod())
                .currentTenantId(currentTenantId)
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Convert InternalUserDto to UserResponse with cached avatar URL, role, and tenant ID
     */
    public static UserResponse from(InternalUserDto user, CachedAvatarUrlService cachedAvatarUrlService, String role, UUID currentTenantId) {
        String avatarUrl = user.getAvatar();

        if (cachedAvatarUrlService != null && avatarUrl != null && !avatarUrl.isEmpty()) {
            avatarUrl = cachedAvatarUrlService.getAvatarUrl(user.getId().toString(), avatarUrl);
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .avatar(avatarUrl)
                .role(role != null ? role : DEFAULT_ROLE)
                .emailVerified(user.getEmailVerified() != null ? user.getEmailVerified() : false)
                .enabled(user.getEnabled() != null ? user.getEnabled() : true)
                .failedLoginAttempts(user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0)
                .accountLocked(user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now()))
                .lockedUntil(user.getLockedUntil())
                .twoFactorEnabled(user.getTwoFactorEnabled() != null ? user.getTwoFactorEnabled() : false)
                .twoFactorMethod(user.getTwoFactorMethod())
                .currentTenantId(currentTenantId)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
