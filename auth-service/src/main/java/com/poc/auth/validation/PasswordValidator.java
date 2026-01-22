package com.poc.auth.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Password strength validator with configurable rules.
 *
 * Default requirements:
 * - Minimum 8 characters
 * - At least 1 uppercase letter
 * - At least 1 lowercase letter
 * - At least 1 digit
 * - At least 1 special character
 */
@Component
@Slf4j
public class PasswordValidator {

    @Value("${app.security.password.min-length:8}")
    private int minLength;

    @Value("${app.security.password.require-uppercase:true}")
    private boolean requireUppercase;

    @Value("${app.security.password.require-lowercase:true}")
    private boolean requireLowercase;

    @Value("${app.security.password.require-digit:true}")
    private boolean requireDigit;

    @Value("${app.security.password.require-special:true}")
    private boolean requireSpecial;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

    /**
     * Validate password strength.
     * @param password The password to validate
     * @return ValidationResult with success status and error messages
     */
    public ValidationResult validate(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("Password is required");
            return new ValidationResult(false, errors);
        }

        if (password.length() < minLength) {
            errors.add(String.format("Password must be at least %d characters long", minLength));
        }

        if (requireUppercase && !UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (requireLowercase && !LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter");
        }

        if (requireDigit && !DIGIT_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one digit");
        }

        if (requireSpecial && !SPECIAL_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one special character (!@#$%^&*()_+-=[]{}|;':\",./<>?)");
        }

        boolean isValid = errors.isEmpty();
        if (!isValid) {
            log.debug("Password validation failed: {}", errors);
        }

        return new ValidationResult(isValid, errors);
    }

    /**
     * Get a human-readable description of password requirements.
     */
    public String getRequirementsDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Password must: ");
        sb.append(String.format("be at least %d characters", minLength));

        if (requireUppercase) {
            sb.append(", contain an uppercase letter");
        }
        if (requireLowercase) {
            sb.append(", contain a lowercase letter");
        }
        if (requireDigit) {
            sb.append(", contain a digit");
        }
        if (requireSpecial) {
            sb.append(", contain a special character");
        }

        return sb.toString();
    }

    /**
     * Result of password validation.
     */
    public record ValidationResult(boolean isValid, List<String> errors) {

        public String getErrorMessage() {
            if (errors == null || errors.isEmpty()) {
                return null;
            }
            return String.join(". ", errors);
        }
    }
}
