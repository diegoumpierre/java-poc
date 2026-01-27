package com.poc.auth.service;

import com.poc.auth.storage.client.StorageClient;
import com.poc.auth.storage.model.PresignedUrlResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for managing avatar URLs with presigned URL generation
 *
 * This service handles converting stored avatar paths to presigned URLs
 * for secure, temporary access to storage objects via storage-service.
 */
@Service
@Slf4j
public class AvatarUrlService {

    @Autowired(required = false)
    private StorageClient storageClient;

    @Value("${app.storage.enabled:true}")
    private boolean storageEnabled;

    @Value("${app.storage.bucket:auth-files}")
    private String bucketName;

    @Value("${app.storage.presignedUrlExpiryMinutes:30}")
    private int presignedUrlExpiryMinutes;

    /**
     * Converts a stored avatar path to a presigned URL if storage is enabled
     *
     * @param avatarPath The stored avatar path (object path in bucket)
     * @return Presigned URL if storage is enabled, otherwise returns original path
     */
    public String getAvatarUrl(String avatarPath) {
        // Return null if no avatar
        if (avatarPath == null || avatarPath.trim().isEmpty()) {
            return null;
        }

        // If storage is disabled or client not available, return as-is
        if (!storageEnabled || storageClient == null) {
            log.debug("Storage disabled, returning avatar path as-is: {}", avatarPath);
            return avatarPath;
        }

        // If it's a local filesystem path (starts with /api/ or /), return as-is
        if (avatarPath.startsWith("/")) {
            log.debug("Local filesystem path, returning as-is: {}", avatarPath);
            return avatarPath;
        }

        // If it's a storage object path (e.g., "avatars/user-id/file.png"), generate presigned URL
        if (!avatarPath.startsWith("http")) {
            try {
                PresignedUrlResponse response = storageClient.getPresignedUrl(
                    bucketName,
                    avatarPath,
                    presignedUrlExpiryMinutes
                );

                log.debug("Generated presigned URL for object: {} (expires in {} minutes)",
                    avatarPath, presignedUrlExpiryMinutes);

                return response.getUrl();
            } catch (Exception e) {
                log.error("Failed to generate presigned URL for object: {}", avatarPath, e);
                return avatarPath;
            }
        }

        // If it's already a full URL, return as-is
        log.debug("Full URL, returning as-is: {}", avatarPath);
        return avatarPath;
    }

    /**
     * Check if storage is enabled and configured
     *
     * @return true if storage is available for use
     */
    public boolean isStorageEnabled() {
        return storageEnabled && storageClient != null;
    }
}
