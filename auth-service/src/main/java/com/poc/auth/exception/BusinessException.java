package com.poc.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception for business logic errors.
 * The message will be shown to the user, so it should be user-friendly.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public BusinessException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.code = "BUSINESS_ERROR";
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = "BUSINESS_ERROR";
    }

    public BusinessException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public BusinessException(String message, String code) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.code = code;
    }

    // Factory methods for common scenarios
    public static BusinessException notFound(String resource) {
        return new BusinessException(
                String.format("%s not found", resource),
                HttpStatus.NOT_FOUND,
                "NOT_FOUND"
        );
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT, "CONFLICT");
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }
}
