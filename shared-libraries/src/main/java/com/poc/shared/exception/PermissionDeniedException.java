package com.poc.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PermissionDeniedException extends RuntimeException {

    private final String requiredPermission;

    public PermissionDeniedException(String requiredPermission) {
        super("Permission denied. Required: " + requiredPermission);
        this.requiredPermission = requiredPermission;
    }

    public PermissionDeniedException(String requiredPermission, String message) {
        super(message);
        this.requiredPermission = requiredPermission;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }
}
