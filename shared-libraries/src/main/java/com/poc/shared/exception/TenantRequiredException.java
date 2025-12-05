package com.poc.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a tenant context is required but not present.
 * Results in a 403 Forbidden response.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TenantRequiredException extends RuntimeException {

    public TenantRequiredException(String message) {
        super(message);
    }

    public TenantRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
