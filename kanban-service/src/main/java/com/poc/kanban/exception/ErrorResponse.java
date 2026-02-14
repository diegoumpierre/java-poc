package com.poc.kanban.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String code;
    private Map<String, String> validationErrors;

    public static ErrorResponse of(int status, String error, String message, String path, String code) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .code(code)
                .build();
    }

    public static ErrorResponse of(int status, String error, String message, String path) {
        return of(status, error, message, path, null);
    }

    public static ErrorResponse notFound(String message, String path) {
        return of(404, "Not Found", message, path, "NOT_FOUND");
    }

    public static ErrorResponse badRequest(String message, String path) {
        return of(400, "Bad Request", message, path, "BAD_REQUEST");
    }

    public static ErrorResponse unauthorized(String message, String path) {
        return of(401, "Unauthorized", message, path, "UNAUTHORIZED");
    }

    public static ErrorResponse forbidden(String message, String path) {
        return of(403, "Forbidden", message, path, "FORBIDDEN");
    }

    public static ErrorResponse conflict(String message, String path) {
        return of(409, "Conflict", message, path, "CONFLICT");
    }

    public static ErrorResponse internalError(String path) {
        return of(500, "Internal Server Error", "An unexpected error occurred", path, "INTERNAL_ERROR");
    }
}
