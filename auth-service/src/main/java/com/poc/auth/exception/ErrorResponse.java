package com.poc.auth.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.slf4j.MDC;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "User-friendly error message", example = "Invalid request. Please check your input.")
    private String message;

    @Schema(description = "Request path", example = "/api/users/123")
    private String path;

    @Schema(description = "Timestamp of the error")
    private LocalDateTime timestamp;

    @Schema(description = "Error code for client handling", example = "VALIDATION_ERROR")
    private String code;

    @Schema(description = "Correlation ID for distributed tracing", example = "550e8400-e29b-41d4-a716-446655440000")
    private String correlationId;

    @Schema(description = "Request ID for this specific request", example = "a1b2c3d4")
    private String requestId;

    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .correlationId(MDC.get("correlationId"))
                .requestId(MDC.get("requestId"))
                .build();
    }

    public static ErrorResponse of(int status, String error, String message, String path, String code) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .code(code)
                .timestamp(LocalDateTime.now())
                .correlationId(MDC.get("correlationId"))
                .requestId(MDC.get("requestId"))
                .build();
    }
}
