package com.poc.auth.client;

import com.poc.auth.client.dto.InternalUserDto;
import com.poc.auth.client.dto.RegisterInternalRequest;
import com.poc.auth.client.dto.UpdateLoginStateRequest;
import com.poc.auth.client.dto.UpdatePasswordRequest;
import com.poc.auth.client.dto.UpdateProfileRequest;
import com.poc.auth.client.dto.UpdateAvatarRequest;
import com.poc.auth.client.dto.UpdateTotpRequest;
import com.poc.auth.client.dto.UpdateTwoFactorRequest;
import com.poc.auth.client.dto.PermissionDto;
import com.poc.auth.model.response.AccessContextResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for user-service.
 * auth-service calls user-service for all user/role/permission/membership operations.
 * Direction: auth-service -> user-service (NEVER the reverse).
 */
@FeignClient(name = "user-service", url = "${app.user.url:http://localhost:8101}")
public interface UserClient {

    // =========================================================================
    // Internal User endpoints (include password hash)
    // =========================================================================

    @GetMapping("/api/users/internal/by-email")
    InternalUserDto findByEmail(@RequestParam("email") String email);

    @GetMapping("/api/users/internal/{id}")
    InternalUserDto findById(@PathVariable("id") UUID id);

    @PutMapping("/api/users/internal/{id}/login-state")
    void updateLoginState(@PathVariable("id") UUID id, @RequestBody UpdateLoginStateRequest request);

    @GetMapping("/api/users/internal/context")
    AccessContextResponse getAccessContext(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Tenant-Id") String tenantId);

    @PostMapping("/api/users/internal/register")
    InternalUserDto registerInternal(@RequestBody RegisterInternalRequest request);

    @PutMapping("/api/users/internal/{id}/verify-email")
    void verifyEmail(@PathVariable("id") UUID id);

    @PutMapping("/api/users/internal/{id}/password")
    void updatePassword(@PathVariable("id") UUID id, @RequestBody UpdatePasswordRequest request);

    @PutMapping("/api/users/internal/{id}/two-factor")
    void updateTwoFactor(@PathVariable("id") UUID id, @RequestBody UpdateTwoFactorRequest request);

    // =========================================================================
    // Profile endpoints (for profile management)
    // =========================================================================

    @PutMapping("/api/users/internal/{id}/profile")
    InternalUserDto updateProfile(@PathVariable("id") UUID id, @RequestBody UpdateProfileRequest request);

    @PutMapping("/api/users/internal/{id}/avatar")
    InternalUserDto updateAvatar(@PathVariable("id") UUID id, @RequestBody UpdateAvatarRequest request);

    // =========================================================================
    // TOTP endpoints (for 2FA management)
    // =========================================================================

    @PutMapping("/api/users/internal/{id}/totp")
    InternalUserDto updateTotp(@PathVariable("id") UUID id, @RequestBody UpdateTotpRequest request);

    // =========================================================================
    // Access context endpoints
    // =========================================================================

    @GetMapping("/api/users/internal/context/for-tenant")
    AccessContextResponse getAccessContextForTenant(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam("tenantId") UUID tenantId);

    // =========================================================================
    // Permission endpoints (for gateway permission resolution)
    // =========================================================================

    @PostMapping("/api/permissions/by-roles")
    List<PermissionDto> findPermissionsByRoleIds(@RequestBody List<UUID> roleIds);

}
