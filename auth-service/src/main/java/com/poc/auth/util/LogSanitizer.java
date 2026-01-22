package com.poc.auth.util;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing sensitive data before logging.
 * Prevents exposure of passwords, tokens, and personal information in logs.
 */
public final class LogSanitizer {

    private LogSanitizer() {
        // Utility class
    }

    // Patterns for sensitive data
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})"
    );

    private static final Pattern JWT_PATTERN = Pattern.compile(
        "(eyJ[a-zA-Z0-9_-]*\\.eyJ[a-zA-Z0-9_-]*\\.[a-zA-Z0-9_-]*)"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "(password|senha|pwd|secret)[\"':\\s=]+([^\"'\\s,}]+)",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile(
        "\\b(\\d{4})[- ]?(\\d{4})[- ]?(\\d{4})[- ]?(\\d{4})\\b"
    );

    private static final Pattern CPF_PATTERN = Pattern.compile(
        "\\b(\\d{3})\\.?(\\d{3})\\.?(\\d{3})-?(\\d{2})\\b"
    );

    /**
     * Mask an email address for logging.
     * Example: john.doe@example.com -> jo***@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || email.length() < 5) {
            return "***";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (localPart.length() <= 2) {
            return "**" + domain;
        }

        return localPart.substring(0, 2) + "***" + domain;
    }

    /**
     * Mask a phone number for logging.
     * Example: +5511999998888 -> +55***8888
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 8) {
            return "***";
        }

        // Keep first 3 and last 4 digits
        String digits = phone.replaceAll("[^0-9+]", "");
        if (digits.length() < 8) {
            return "***";
        }

        return digits.substring(0, 3) + "***" + digits.substring(digits.length() - 4);
    }

    /**
     * Mask a UUID for logging (show only first and last segments).
     * Example: 550e8400-e29b-41d4-a716-446655440000 -> 550e8400-****-****-****-446655440000
     */
    public static String maskUuid(String uuid) {
        if (uuid == null || uuid.length() != 36) {
            return "***";
        }

        return uuid.substring(0, 8) + "-****-****-****-" + uuid.substring(24);
    }

    /**
     * Completely mask a password or token.
     */
    public static String maskSecret(String secret) {
        if (secret == null || secret.isEmpty()) {
            return "[empty]";
        }
        return "[REDACTED:" + secret.length() + " chars]";
    }

    /**
     * Sanitize a string that may contain sensitive data.
     * Detects and masks emails, JWTs, passwords, credit cards, etc.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }

        String result = input;

        // Mask JWTs
        result = JWT_PATTERN.matcher(result).replaceAll("[JWT_REDACTED]");

        // Mask passwords
        result = PASSWORD_PATTERN.matcher(result).replaceAll("$1=[REDACTED]");

        // Mask emails
        result = EMAIL_PATTERN.matcher(result).replaceAll("$1***@$2");

        // Mask credit cards
        result = CREDIT_CARD_PATTERN.matcher(result).replaceAll("$1-****-****-$4");

        // Mask CPF
        result = CPF_PATTERN.matcher(result).replaceAll("$1.***.***-$4");

        return result;
    }

    /**
     * Sanitize and truncate a string for logging.
     */
    public static String sanitizeAndTruncate(String input, int maxLength) {
        String sanitized = sanitize(input);
        if (sanitized == null) {
            return null;
        }

        // Remove newlines to prevent log injection
        sanitized = sanitized.replaceAll("[\\r\\n]", " ");

        if (sanitized.length() > maxLength) {
            return sanitized.substring(0, maxLength) + "...[truncated]";
        }

        return sanitized;
    }

    /**
     * Create a safe log message for user actions.
     */
    public static String userAction(String userId, String action) {
        return String.format("user=%s action=%s", maskUuid(userId), action);
    }

    /**
     * Create a safe log message for user actions with details.
     */
    public static String userAction(String userId, String action, String details) {
        return String.format("user=%s action=%s details=%s",
            maskUuid(userId),
            action,
            sanitizeAndTruncate(details, 100));
    }
}
