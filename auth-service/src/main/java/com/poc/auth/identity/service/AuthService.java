package com.poc.auth.service;

import com.poc.auth.client.dto.InternalUserDto;
import com.poc.auth.model.request.LoginRequest;
import com.poc.auth.model.request.RegisterRequest;
import com.poc.auth.model.request.ResendCodeRequest;
import com.poc.auth.model.request.ResetPasswordRequest;
import com.poc.auth.model.request.SelectTenantRequest;
import com.poc.auth.model.request.TwoFactorVerifyRequest;
import com.poc.auth.model.request.VerifyCodeRequest;
import com.poc.auth.model.response.AuthResponse;
import com.poc.auth.model.response.UserResponse;

import java.util.UUID;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    /**
     * Complete login by selecting a tenant for users with multiple tenants
     */
    AuthResponse selectTenant(SelectTenantRequest request);

    AuthResponse login(LoginRequest request, String ipAddress, String userAgent);

    AuthResponse register(RegisterRequest request);

    UserResponse getCurrentUser(UUID userId);

    InternalUserDto getUserById(UUID userId);

    AuthResponse logout(UUID userId, String accessToken, String refreshToken);

    AuthResponse forgotPassword(String email);

    AuthResponse verifyCode(VerifyCodeRequest request);

    AuthResponse resetPassword(ResetPasswordRequest request);

    AuthResponse refreshToken(String refreshToken);

    AuthResponse verify2FA(TwoFactorVerifyRequest request);

    AuthResponse resendCode(ResendCodeRequest request);
}
