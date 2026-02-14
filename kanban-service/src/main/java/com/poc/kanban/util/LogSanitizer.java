package com.poc.kanban.util;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing sensitive data before logging.
 * Prevents exposure of passwords, tokens, and personal information in logs.
 */
public final class LogSanitizer {

    private LogSanitizer() {
        // Utility class
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})"
    );

    private static final Pattern JWT_PATTERN = Pattern.compile(
        "(eyJ[a-zA-Z0-9_-]*\\.eyJ[a-zA-Z0-9_-]*\\.[a-zA-Z0-9_-]*)"
    );

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

    public static String maskUuid(String uuid) {
        if (uuid == null || uuid.length() != 36) {
            return "***";
        }
        return uuid.substring(0, 8) + "-****-****-****-" + uuid.substring(24);
    }

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        String result = input;
        result = JWT_PATTERN.matcher(result).replaceAll("[JWT_REDACTED]");
        result = EMAIL_PATTERN.matcher(result).replaceAll("$1***@$2");
        return result;
    }

    public static String sanitizeAndTruncate(String input, int maxLength) {
        String sanitized = sanitize(input);
        if (sanitized == null) {
            return null;
        }
        sanitized = sanitized.replaceAll("[\\r\\n]", " ");
        if (sanitized.length() > maxLength) {
            return sanitized.substring(0, maxLength) + "...[truncated]";
        }
        return sanitized;
    }

    public static String userAction(String userId, String action) {
        return String.format("user=%s action=%s", maskUuid(userId), action);
    }
}
