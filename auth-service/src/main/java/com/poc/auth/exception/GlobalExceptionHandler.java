package com.poc.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred. Please try again later.";
    private static final String VALIDATION_ERROR_MESSAGE = "Invalid request. Please check your input.";
    private static final String AUTH_ERROR_MESSAGE = "Authentication failed. Please check your credentials.";
    private static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this resource.";
    private static final String NOT_FOUND_MESSAGE = "The requested resource was not found.";
    private static final String DATABASE_ERROR_MESSAGE = "A database error occurred. Please try again later.";
    private static final String REDIS_ERROR_MESSAGE = "Session service temporarily unavailable. Please try again.";

    // ==================== Validation Errors ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        String message = fieldErrors.isEmpty() ? VALIDATION_ERROR_MESSAGE : fieldErrors;

        log.warn("Validation error on {}: {}", request.getRequestURI(), fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Error",
                        message,
                        request.getRequestURI(),
                        "VALIDATION_ERROR"
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        String violations = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining(", "));

        log.warn("Constraint violation on {}: {}", request.getRequestURI(), violations);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Error",
                        violations.isEmpty() ? VALIDATION_ERROR_MESSAGE : violations,
                        request.getRequestURI(),
                        "CONSTRAINT_VIOLATION"
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        String message = String.format("Required parameter '%s' is missing", ex.getParameterName());
        log.warn("Missing parameter on {}: {}", request.getRequestURI(), ex.getParameterName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        message,
                        request.getRequestURI(),
                        "MISSING_PARAMETER"
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String message = String.format("Invalid value for parameter '%s'", ex.getName());
        log.warn("Type mismatch on {}: parameter={}, value={}", request.getRequestURI(), ex.getName(), ex.getValue());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        message,
                        request.getRequestURI(),
                        "TYPE_MISMATCH"
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.warn("Malformed request body on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        "Invalid request body format",
                        request.getRequestURI(),
                        "MALFORMED_REQUEST"
                ));
    }

    // ==================== Authentication & Authorization ====================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        log.warn("Bad credentials attempt on {}", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        AUTH_ERROR_MESSAGE,
                        request.getRequestURI(),
                        "BAD_CREDENTIALS"
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        log.warn("Authentication failed on {}: {}", request.getRequestURI(), ex.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        AUTH_ERROR_MESSAGE,
                        request.getRequestURI(),
                        "AUTHENTICATION_FAILED"
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("Access denied on {}", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(
                        HttpStatus.FORBIDDEN.value(),
                        "Forbidden",
                        ACCESS_DENIED_MESSAGE,
                        request.getRequestURI(),
                        "ACCESS_DENIED"
                ));
    }

    // ==================== HTTP Errors ====================

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NoHandlerFoundException ex, HttpServletRequest request) {

        log.debug("Resource not found: {}", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        NOT_FOUND_MESSAGE,
                        request.getRequestURI(),
                        "NOT_FOUND"
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        log.debug("Method not supported: {} on {}", ex.getMethod(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        "Method Not Allowed",
                        String.format("Method '%s' is not supported for this endpoint", ex.getMethod()),
                        request.getRequestURI(),
                        "METHOD_NOT_ALLOWED"
                ));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

        log.debug("Media type not supported: {} on {}", ex.getContentType(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.of(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                        "Unsupported Media Type",
                        "The content type is not supported",
                        request.getRequestURI(),
                        "UNSUPPORTED_MEDIA_TYPE"
                ));
    }

    // ==================== Infrastructure Errors ====================

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ErrorResponse> handleRedisException(
            RedisConnectionFailureException ex, HttpServletRequest request) {

        log.error("Redis connection error on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.of(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Service Unavailable",
                        REDIS_ERROR_MESSAGE,
                        request.getRequestURI(),
                        "REDIS_UNAVAILABLE"
                ));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
            DataAccessException ex, HttpServletRequest request) {

        // Check if root cause is Redis
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();
        boolean isRedis = ex.getClass().getName().contains("Redis")
                || root.getClass().getName().contains("Redis")
                || root.getClass().getName().contains("lettuce");

        if (isRedis) {
            log.error("Redis error on {}: {}", request.getRequestURI(), root.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ErrorResponse.of(
                            HttpStatus.SERVICE_UNAVAILABLE.value(),
                            "Service Unavailable",
                            REDIS_ERROR_MESSAGE,
                            request.getRequestURI(),
                            "REDIS_UNAVAILABLE"
                    ));
        }

        log.error("Database error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        DATABASE_ERROR_MESSAGE,
                        request.getRequestURI(),
                        "DATABASE_ERROR"
                ));
    }

    // ==================== Authentication Exceptions ====================

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailed(
            AuthenticationFailedException ex, HttpServletRequest request) {

        log.warn("Authentication failed on {}: {} - {}", request.getRequestURI(), ex.getCode(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        ex.getMessage(),
                        request.getRequestURI(),
                        ex.getCode()
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.warn("Resource not found on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        ex.getMessage(),
                        request.getRequestURI(),
                        "RESOURCE_NOT_FOUND"
                ));
    }

    // ==================== Business Exceptions ====================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.warn("Illegal argument on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        ex.getMessage() != null ? ex.getMessage() : VALIDATION_ERROR_MESSAGE,
                        request.getRequestURI(),
                        "INVALID_ARGUMENT"
                ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex, HttpServletRequest request) {

        log.warn("Illegal state on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        HttpStatus.CONFLICT.value(),
                        "Conflict",
                        ex.getMessage() != null ? ex.getMessage() : "Operation cannot be performed in current state",
                        request.getRequestURI(),
                        "INVALID_STATE"
                ));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        log.warn("Business error on {}: {} - {}", request.getRequestURI(), ex.getCode(), ex.getMessage());

        HttpStatus status = ex.getStatus();
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        ex.getCode()
                ));
    }

    // ==================== Generic Exception Handler ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        // Log full stack trace for debugging but return generic message to user
        log.error("Unexpected error on {}: {} - {}", request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        GENERIC_ERROR_MESSAGE,
                        request.getRequestURI(),
                        "INTERNAL_ERROR"
                ));
    }
}
