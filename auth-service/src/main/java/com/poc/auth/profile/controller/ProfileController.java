package com.poc.auth.controller;

import com.poc.auth.model.request.ProfileRequest;
import com.poc.auth.model.response.UserResponse;
import com.poc.auth.service.ProfileService;
import com.poc.auth.storage.client.StorageClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "User profile management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private StorageClient storageClient;

    @Value("${app.upload.avatar.path:./uploads/avatars}")
    private String avatarUploadPath;

    @Value("${app.storage.enabled:true}")
    private boolean storageEnabled;

    @Value("${app.storage.bucket:auth-files}")
    private String storageBucket;

    /**
     * GET /api/profile
     * Get current user profile
     */
    @GetMapping
    @Operation(summary = "Get user profile", description = "Get current user's profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getProfile(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(profileService.getProfile(java.util.UUID.fromString(userId)));
    }

    /**
     * PUT /api/profile
     * Update current user profile
     */
    @PutMapping
    @Operation(summary = "Update user profile", description = "Update current user's profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<UserResponse> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(java.util.UUID.fromString(userId), request));
    }

    /**
     * POST /api/profile/avatar
     * Upload user avatar
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload avatar", description = "Upload a new avatar image for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatar uploaded successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid file")
    })
    public ResponseEntity<UserResponse> uploadAvatar(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(profileService.uploadAvatar(java.util.UUID.fromString(userId), file));
    }

    /**
     * DELETE /api/profile/avatar
     * Delete user avatar
     */
    @DeleteMapping("/avatar")
    @Operation(summary = "Delete avatar", description = "Delete the current user's avatar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatar deleted successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<UserResponse> deleteAvatar(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(profileService.deleteAvatar(java.util.UUID.fromString(userId)));
    }

    /**
     * GET /api/profile/avatar/view
     * Serve current user's avatar image (proxy from storage or local)
     */
    @GetMapping("/avatar/view")
    @Operation(summary = "View avatar image", description = "View the current user's avatar image")
    public ResponseEntity<byte[]> viewAvatar(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestParam(value = "userId", required = false) String userIdParam) {
        try {
            // Accept userId from header (authenticated requests) or query param (public image loading)
            String userId = userIdHeader != null ? userIdHeader : userIdParam;

            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            UserResponse user = profileService.getProfile(java.util.UUID.fromString(userId));

            if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (storageEnabled && storageClient != null && !user.getAvatar().startsWith("/")) {
                // Download from storage-service
                byte[] imageBytes = storageClient.download(storageBucket, user.getAvatar());

                // Determine content type from filename
                String contentType = "image/png";
                if (user.getAvatar().endsWith(".jpg") || user.getAvatar().endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (user.getAvatar().endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (user.getAvatar().endsWith(".webp")) {
                    contentType = "image/webp";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=300")
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                        .body(imageBytes);
            } else {
                // Serve from local storage
                Path filePath = Paths.get(avatarUploadPath).resolve(user.getAvatar()).normalize();
                byte[] imageBytes = Files.readAllBytes(filePath);

                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=300")
                        .body(imageBytes);
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/profile/avatar/{filename}
     * Serve avatar image (public endpoint)
     */
    @GetMapping("/avatar/{filename}")
    @Operation(summary = "Get avatar image", description = "Retrieve an avatar image by filename")
    public ResponseEntity<Resource> getAvatarImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(avatarUploadPath).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
