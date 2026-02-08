package com.poc.kanban.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Service for validating file uploads to prevent malicious file uploads.
 * Validates file extension, MIME type, and file size.
 */
@Service
@Slf4j
public class FileValidationService {

    // Allowed extensions for attachments
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        // Images
        "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg",
        // Documents
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf", "odt", "ods",
        // Archives
        "zip", "tar", "gz", "rar", "7z",
        // Code
        "json", "xml", "csv", "md", "yaml", "yml"
    );

    // Blocked extensions (security risk)
    private static final Set<String> BLOCKED_EXTENSIONS = Set.of(
        "exe", "bat", "cmd", "sh", "ps1", "vbs", "js", "jar", "msi", "dll", "scr",
        "php", "asp", "aspx", "jsp", "cgi", "pl", "py", "rb", "htaccess"
    );

    // Allowed MIME types
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
        // Images
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp", "image/svg+xml",
        // Documents
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain",
        "application/rtf",
        "application/vnd.oasis.opendocument.text",
        "application/vnd.oasis.opendocument.spreadsheet",
        // Archives
        "application/zip",
        "application/x-tar",
        "application/gzip",
        "application/x-rar-compressed",
        "application/x-7z-compressed",
        // Code/Data
        "application/json",
        "application/xml",
        "text/xml",
        "text/csv",
        "text/markdown",
        "text/yaml",
        "application/x-yaml"
    );

    @Value("${app.upload.max-file-size:10485760}")  // 10MB default
    private long maxFileSize;

    /**
     * Validate a file upload for attachments.
     *
     * @param file The uploaded file
     * @throws SecurityException if validation fails
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new SecurityException("File is empty");
        }

        // 1. Validate file size
        if (file.getSize() > maxFileSize) {
            log.warn("File size exceeds limit: {} > {}", file.getSize(), maxFileSize);
            throw new SecurityException("File size exceeds maximum allowed size of " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // 2. Validate file extension
        String filename = sanitizeFilename(file.getOriginalFilename());
        String extension = getFileExtension(filename).toLowerCase();

        if (BLOCKED_EXTENSIONS.contains(extension)) {
            log.warn("Blocked file extension attempted: {}", extension);
            throw new SecurityException("File type is not allowed for security reasons");
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            log.warn("Invalid file extension: {}", extension);
            throw new SecurityException("File type not allowed. Contact administrator for supported types.");
        }

        // 3. Validate declared MIME type
        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            // Allow application/octet-stream but log it
            if (!"application/octet-stream".equals(contentType)) {
                log.warn("Unexpected content type: {} for file: {}", contentType, filename);
            }
        }

        // 4. Check for double extensions (e.g., file.php.jpg)
        if (hasDoubleExtension(filename)) {
            log.warn("Double extension detected: {}", filename);
            throw new SecurityException("Invalid filename format");
        }

        log.debug("File validation passed: {}", filename);
    }

    /**
     * Check for suspicious double extensions.
     */
    private boolean hasDoubleExtension(String filename) {
        String[] parts = filename.split("\\.");
        if (parts.length > 2) {
            // Check if any middle part is a blocked extension
            for (int i = 1; i < parts.length - 1; i++) {
                if (BLOCKED_EXTENSIONS.contains(parts[i].toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
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
    public String generateSafeFilename(String originalFilename) {
        String sanitized = sanitizeFilename(originalFilename);
        String extension = getFileExtension(sanitized);
        String nameWithoutExt = sanitized.substring(0, sanitized.lastIndexOf('.'));
        long timestamp = System.currentTimeMillis();

        // Limit name length
        if (nameWithoutExt.length() > 50) {
            nameWithoutExt = nameWithoutExt.substring(0, 50);
        }

        return String.format("%s_%d.%s", nameWithoutExt, timestamp, extension);
    }
}
