package com.poc.auth.service.impl;

import com.poc.auth.client.UserClient;
import com.poc.auth.client.dto.InternalUserDto;
import com.poc.auth.client.dto.UpdateAvatarRequest;
import com.poc.auth.client.dto.UpdateProfileRequest;
import com.poc.auth.exception.BusinessException;
import com.poc.auth.model.request.ProfileRequest;
import com.poc.auth.model.response.UserResponse;
import com.poc.auth.profile.service.CachedAvatarUrlService;
import com.poc.auth.service.ProfileService;
import com.poc.auth.storage.client.StorageClient;
import com.poc.auth.storage.model.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final UserClient userClient;
    private final CachedAvatarUrlService cachedAvatarUrlService;

    @Autowired(required = false)
    private StorageClient storageClient;

    @Value("${app.upload.avatar.path:./uploads/avatars}")
    private String avatarUploadPath;

    @Value("${app.upload.avatar.max-size:5242880}")
    private long maxAvatarSize;

    @Value("${app.upload.avatar.allowed-types:image/jpeg,image/png,image/gif,image/webp}")
    private String allowedTypes;

    @Value("${app.storage.enabled:true}")
    private boolean storageEnabled;

    @Value("${app.storage.bucket:auth-files}")
    private String storageBucket;

    public ProfileServiceImpl(UserClient userClient,
                              CachedAvatarUrlService cachedAvatarUrlService) {
        this.userClient = userClient;
        this.cachedAvatarUrlService = cachedAvatarUrlService;
    }

    @Override
    public UserResponse getProfile(UUID userId) {
        InternalUserDto user = findUserById(userId);
        return UserResponse.from(user, cachedAvatarUrlService, "USER", null);
    }

    @Override
    public UserResponse updateProfile(UUID userId, ProfileRequest request) {
        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .build();

        InternalUserDto updatedUser = userClient.updateProfile(userId, updateRequest);
        log.info("Profile updated for user: {}", userId);
        return UserResponse.from(updatedUser, cachedAvatarUrlService, "USER", null);
    }

    @Override
    public UserResponse uploadAvatar(UUID userId, MultipartFile file) {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new BusinessException("No file provided");
        }

        if (file.getSize() > maxAvatarSize) {
            throw new BusinessException("File size exceeds maximum allowed size of " + (maxAvatarSize / 1024 / 1024) + "MB");
        }

        String contentType = file.getContentType();
        List<String> allowedTypesList = Arrays.asList(allowedTypes.split(","));
        if (contentType == null || !allowedTypesList.contains(contentType)) {
            throw new BusinessException("Invalid file type. Allowed types: " + allowedTypes);
        }

        // Delete old avatar if exists (don't fail upload if delete fails)
        InternalUserDto currentUser = findUserById(userId);
        if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
            deleteAvatarFile(currentUser.getAvatar(), false);
        }

        String avatarUrl;

        if (storageEnabled && storageClient != null) {
            try {
                log.info("Uploading avatar to storage-service for user: {}", userId);

                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                String objectName = "avatars/" + userId.toString() + "/" + UUID.randomUUID().toString() + extension;

                FileUploadResponse response = storageClient.upload(
                    file, storageBucket, objectName, userId, "auth"
                );

                if (!response.isSuccess()) {
                    throw new BusinessException("Failed to upload avatar: " + response.getMessage());
                }

                avatarUrl = objectName;
                log.info("Avatar uploaded successfully for user: {} - path: {}", userId, objectName);

            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("Failed to upload avatar for user: {}", userId, e);
                throw new BusinessException("Failed to upload avatar to storage");
            }
        } else {
            try {
                log.info("Uploading avatar to filesystem for user: {}", userId);

                Path uploadDir = Paths.get(avatarUploadPath);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                String newFilename = UUID.randomUUID().toString() + extension;

                Path filePath = uploadDir.resolve(newFilename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                avatarUrl = "/api/profile/avatar/" + newFilename;
                log.info("Avatar uploaded to filesystem successfully for user: {}", userId);

            } catch (IOException e) {
                log.error("Failed to upload avatar to filesystem for user: {}", userId, e);
                throw new BusinessException("Failed to upload avatar");
            }
        }

        // Update avatar path in user-service
        InternalUserDto updatedUser = userClient.updateAvatar(userId,
                UpdateAvatarRequest.builder().avatar(avatarUrl).build());

        // Invalidate cache for new avatar
        if (cachedAvatarUrlService != null) {
            cachedAvatarUrlService.invalidateCache(userId.toString());
            log.debug("Invalidated avatar cache after upload for user: {}", userId);
        }

        return UserResponse.from(updatedUser, cachedAvatarUrlService, "USER", null);
    }

    @Override
    public UserResponse deleteAvatar(UUID userId) {
        InternalUserDto user = findUserById(userId);

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            deleteAvatarFile(user.getAvatar(), true);

            // Clear avatar path in user-service
            InternalUserDto updatedUser = userClient.updateAvatar(userId,
                    UpdateAvatarRequest.builder().avatar(null).build());
            log.info("Avatar deleted for user: {}", userId);

            // Invalidate cache after delete
            if (cachedAvatarUrlService != null) {
                cachedAvatarUrlService.invalidateCache(userId.toString());
                log.debug("Invalidated avatar cache after delete for user: {}", userId);
            }

            return UserResponse.from(updatedUser, cachedAvatarUrlService, "USER", null);
        }

        return UserResponse.from(user, cachedAvatarUrlService, "USER", null);
    }

    private InternalUserDto findUserById(UUID userId) {
        try {
            return userClient.findById(userId);
        } catch (Exception e) {
            log.error("Failed to find user {}: {}", userId, e.getMessage());
            throw new BusinessException("User not found");
        }
    }

    private void deleteAvatarFile(String avatarUrl, boolean throwOnFailure) {
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return;
        }

        if (storageEnabled && storageClient != null && !avatarUrl.startsWith("/")) {
            try {
                storageClient.delete(storageBucket, avatarUrl);
                log.info("Deleted avatar from storage: {}", avatarUrl);
            } catch (Exception e) {
                log.warn("Failed to delete avatar from storage: {}", avatarUrl, e);
                if (throwOnFailure) {
                    throw new BusinessException(
                            "Não foi possível excluir o avatar. Tente novamente mais tarde.",
                            org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                            "STORAGE_ERROR"
                    );
                }
            }
        } else if (avatarUrl.startsWith("/api/profile/avatar/")) {
            try {
                String filename = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(avatarUploadPath).resolve(filename);
                Files.deleteIfExists(filePath);
                log.info("Deleted avatar from filesystem: {}", filename);
            } catch (IOException e) {
                log.warn("Failed to delete avatar file from filesystem: {}", avatarUrl, e);
                if (throwOnFailure) {
                    throw new BusinessException(
                            "Não foi possível excluir o avatar. Tente novamente mais tarde.",
                            org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                            "STORAGE_ERROR"
                    );
                }
            }
        }
    }
}
