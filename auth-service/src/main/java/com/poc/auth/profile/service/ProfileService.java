package com.poc.auth.service;

import com.poc.auth.model.request.ProfileRequest;
import com.poc.auth.model.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProfileService {

    /**
     * Get the profile of the current user
     */
    UserResponse getProfile(UUID userId);

    /**
     * Update the profile of the current user
     */
    UserResponse updateProfile(UUID userId, ProfileRequest request);

    /**
     * Upload and update the user's avatar
     */
    UserResponse uploadAvatar(UUID userId, MultipartFile file);

    /**
     * Delete the user's avatar
     */
    UserResponse deleteAvatar(UUID userId);
}
