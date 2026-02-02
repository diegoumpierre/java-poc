package com.poc.tenant.exception;

import com.poc.shared.exception.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<Map<String, Object>> handlePermissionDenied(PermissionDeniedException ex) {
        log.warn("Permission denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
            "error", "Forbidden",
            "message", ex.getMessage(),
            "requiredPermission", ex.getMessage().replace("Permission denied. Required: ", ""),
            "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", "Not Found",
            "message", ex.getMessage(),
            "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "error", "Bad Request",
            "message", ex.getMessage(),
            "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "error", "Bad Request",
            "message", ex.getMessage(),
            "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", "Internal Server Error",
            "message", "An unexpected error occurred",
            "timestamp", Instant.now().toString()
        ));
    }
}
