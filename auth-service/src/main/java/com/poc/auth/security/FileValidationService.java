package com.poc.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Service for validating file uploads to prevent malicious file uploads.
 * Validates file extension, MIME type, and file size.
 */
@Service
@Slf4j
public class FileValidationService {

    // Allowed image extensions for avatars
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "webp"
    );

    // Allowed MIME types for images
    private static final Set<String> ALLOWED_IMAGE_MIME_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    // Magic bytes for common image formats
    private static final byte[] JPEG_MAGIC = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] GIF_MAGIC = new byte[]{0x47, 0x49, 0x46};
    private static final byte[] WEBP_MAGIC = new byte[]{0x52, 0x49, 0x46, 0x46};

    @Value("${app.upload.max-file-size:5242880}")  // 5MB default
    private long maxFileSize;

    /**
     * Validate an image file upload (for avatars).
     *
     * @param file The uploaded file
     * @throws SecurityException if validation fails
     */
    public void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new SecurityException("File is empty");
        }

        // 1. Validate file size
        if (file.getSize() > maxFileSize) {
            log.warn("File size exceeds limit: {} > {}", file.getSize(), maxFileSize);
            throw new SecurityException("File size exceeds maximum allowed size");
        }

        // 2. Validate file extension
        String filename = sanitizeFilename(file.getOriginalFilename());
        String extension = getFileExtension(filename).toLowerCase();

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            log.warn("Invalid file extension: {}", extension);
            throw new SecurityException("File type not allowed. Allowed types: " + ALLOWED_IMAGE_EXTENSIONS);
        }

        // 3. Validate declared MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_MIME_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Invalid content type: {}", contentType);
            throw new SecurityException("Invalid file content type");
        }

        // 4. Validate actual file content (magic bytes)
        try {
            if (!isValidImageContent(file.getInputStream(), extension)) {
                log.warn("File content does not match declared type: {}", filename);
                throw new SecurityException("File content does not match declared type");
            }
        } catch (IOException e) {
            log.error("Error reading file content", e);
            throw new SecurityException("Error validating file content");
        }

        log.debug("File validation passed: {}", filename);
    }

    /**
     * Check if file content matches expected image type using magic bytes.
     */
    private boolean isValidImageContent(InputStream inputStream, String extension) throws IOException {
        byte[] header = new byte[12];
        int bytesRead = inputStream.read(header);

        if (bytesRead < 4) {
            return false;
        }

        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> startsWith(header, JPEG_MAGIC);
            case "png" -> startsWith(header, PNG_MAGIC);
            case "gif" -> startsWith(header, GIF_MAGIC);
            case "webp" -> startsWith(header, WEBP_MAGIC);
            default -> false;
        };
    }

    /**
     * Check if byte array starts with given prefix.
     */
    private boolean startsWith(byte[] array, byte[] prefix) {
        if (array.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (array[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Extract file extension from filename.
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * Sanitize filename to prevent path traversal attacks.
     */
    public String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unnamed";
        }

        // Remove path separators
        String sanitized = filename.replaceAll("[/\\\\]", "");

        // Remove null bytes
        sanitized = sanitized.replace("\0", "");

        // Remove special characters except dots, dashes, underscores
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Limit length
        if (sanitized.length() > 255) {
            String ext = getFileExtension(sanitized);
            sanitized = sanitized.substring(0, 250 - ext.length()) + "." + ext;
        }

        // Prevent hidden files
        if (sanitized.startsWith(".")) {
            sanitized = "_" + sanitized.substring(1);
        }

        return sanitized;
    }

    /**
     * Generate a safe filename with timestamp.
     */
    public String generateSafeFilename(String originalFilename, String prefix) {
        String sanitized = sanitizeFilename(originalFilename);
        String extension = getFileExtension(sanitized);
        long timestamp = System.currentTimeMillis();

        return String.format("%s_%d.%s", prefix, timestamp, extension);
    }
}
