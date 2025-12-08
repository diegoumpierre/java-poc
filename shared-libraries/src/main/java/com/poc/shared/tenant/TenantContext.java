package com.poc.shared.tenant;

import com.poc.shared.exception.TenantRequiredException;

import java.util.UUID;

/**
 * Thread-local storage for tenant context.
 * Ensures data isolation in multi-tenant SaaS architecture.
 *
 * <p>Usage:</p>
 * <pre>
 * // Set context (usually done by TenantFilter)
 * TenantContext.setCurrentTenant(tenantId);
 *
 * // Get context in service layer
 * UUID tenantId = TenantContext.getCurrentTenant();
 *
 * // Always clear in finally block
 * TenantContext.clear();
 * </pre>
 */
public class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_MEMBERSHIP = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_USER = new ThreadLocal<>();

    private TenantContext() {
        // Utility class
    }

    public static UUID getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void setCurrentTenant(UUID tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static UUID getCurrentMembership() {
        return CURRENT_MEMBERSHIP.get();
    }

    public static void setCurrentMembership(UUID membershipId) {
        CURRENT_MEMBERSHIP.set(membershipId);
    }

    public static UUID getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void setCurrentUser(UUID userId) {
        CURRENT_USER.set(userId);
    }

    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_MEMBERSHIP.remove();
        CURRENT_USER.remove();
    }

    public static boolean hasTenantContext() {
        return CURRENT_TENANT.get() != null;
    }

    public static boolean hasUserContext() {
        return CURRENT_USER.get() != null;
    }

    /**
     * Ensures tenant context exists, throws exception if not.
     * @throws TenantRequiredException if no tenant context is set
     */
    public static void requireTenant() {
        if (!hasTenantContext()) {
            throw new TenantRequiredException("Tenant context is required for this operation");
        }
    }

    /**
     * Ensures user context exists, throws exception if not.
     * @throws TenantRequiredException if no user context is set
     */
    public static void requireUser() {
        if (!hasUserContext()) {
            throw new TenantRequiredException("User context is required for this operation");
        }
    }
}
