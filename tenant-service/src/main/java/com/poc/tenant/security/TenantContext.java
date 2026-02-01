package com.poc.tenant.security;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<UUID> tenantId = new ThreadLocal<>();
    private static final ThreadLocal<UUID> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> userEmail = new ThreadLocal<>();
    private static final ThreadLocal<Set<String>> userRoles = new ThreadLocal<>();

    public static UUID getTenantId() {
        return tenantId.get();
    }

    public static void setTenantId(UUID id) {
        tenantId.set(id);
    }

    public static UUID getUserId() {
        return userId.get();
    }

    public static void setUserId(UUID id) {
        userId.set(id);
    }

    public static String getUserEmail() {
        return userEmail.get();
    }

    public static void setUserEmail(String email) {
        userEmail.set(email);
    }

    public static Set<String> getUserRoles() {
        Set<String> roles = userRoles.get();
        return roles != null ? roles : Collections.emptySet();
    }

    public static void setUserRoles(Set<String> roles) {
        userRoles.set(roles);
    }

    public static void clear() {
        tenantId.remove();
        userId.remove();
        userEmail.remove();
        userRoles.remove();
    }

    // Aliases for compatibility
    public static UUID getCurrentTenant() {
        return getTenantId();
    }

    public static UUID getCurrentUser() {
        return getUserId();
    }
}
