package com.poc.auth.exception;

import lombok.Getter;

/**
 * Exception thrown when authentication fails (invalid credentials, locked account, etc.)
 * Results in HTTP 401 Unauthorized
 */
@Getter
public class AuthenticationFailedException extends RuntimeException {

    private final String code;
    private final boolean accountLocked;
    private final Integer lockoutMinutesRemaining;

    public AuthenticationFailedException(String message) {
        super(message);
        this.code = "AUTHENTICATION_FAILED";
        this.accountLocked = false;
        this.lockoutMinutesRemaining = null;
    }

    public AuthenticationFailedException(String message, String code) {
        super(message);
        this.code = code;
        this.accountLocked = false;
        this.lockoutMinutesRemaining = null;
    }

    public static AuthenticationFailedException invalidCredentials() {
        return new AuthenticationFailedException("Invalid email or password", "INVALID_CREDENTIALS");
    }

    public static AuthenticationFailedException accountLocked(int minutesRemaining) {
        AuthenticationFailedException ex = new AuthenticationFailedException(
                String.format("Account is locked. Try again in %d minutes.", minutesRemaining),
                "ACCOUNT_LOCKED"
        );
        return new AuthenticationFailedException(ex.getMessage(), ex.getCode(), true, minutesRemaining);
    }

    public static AuthenticationFailedException tooManyAttempts(int lockoutMinutes) {
        return new AuthenticationFailedException(
                String.format("Account locked due to too many failed attempts. Try again in %d minutes.", lockoutMinutes),
                "TOO_MANY_ATTEMPTS"
        );
    }

    private AuthenticationFailedException(String message, String code, boolean accountLocked, Integer lockoutMinutesRemaining) {
        super(message);
        this.code = code;
        this.accountLocked = accountLocked;
        this.lockoutMinutesRemaining = lockoutMinutesRemaining;
    }
}
