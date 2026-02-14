package com.poc.kanban.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int statusCode;
    private final String code;

    public BusinessException(String message) {
        this(message, 400, "BUSINESS_ERROR");
    }

    public BusinessException(String message, int statusCode) {
        this(message, statusCode, "BUSINESS_ERROR");
    }

    public BusinessException(String message, int statusCode, String code) {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(message, 404, "NOT_FOUND");
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(message, 400, "BAD_REQUEST");
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, 401, "UNAUTHORIZED");
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(message, 403, "FORBIDDEN");
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(message, 409, "CONFLICT");
    }
}
