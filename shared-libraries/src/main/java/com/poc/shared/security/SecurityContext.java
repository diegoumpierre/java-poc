package com.poc.shared.security;

import java.util.Collections;
import java.util.Set;

/**
 * Thread-local storage for user permissions.
 * Populated by SecurityFilter from X-User-Permissions header.
 */
public class SecurityContext {

    private static final ThreadLocal<Set<String>> CURRENT_PERMISSIONS = new ThreadLocal<>();

    private SecurityContext() {
    }

    public static Set<String> getPermissions() {
        Set<String> perms = CURRENT_PERMISSIONS.get();
        return perms != null ? perms : Collections.emptySet();
    }

    public static void setPermissions(Set<String> permissions) {
        CURRENT_PERMISSIONS.set(permissions);
    }

    public static void clear() {
        CURRENT_PERMISSIONS.remove();
    }

    public static boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }

    public static boolean hasAnyPermission(String... permissions) {
        Set<String> current = getPermissions();
        for (String p : permissions) {
            if (current.contains(p)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAllPermissions(String... permissions) {
        Set<String> current = getPermissions();
        for (String p : permissions) {
            if (!current.contains(p)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCustomer() {
        return hasPermission("PORTAL_ACCESS");
    }

    public static boolean hasContext() {
        Set<String> perms = CURRENT_PERMISSIONS.get();
        return perms != null && !perms.isEmpty();
    }
}
