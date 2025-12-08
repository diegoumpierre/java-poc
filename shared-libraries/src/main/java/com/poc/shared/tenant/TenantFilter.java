package com.poc.shared.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that extracts tenant context from request headers (injected by API Gateway).
 * Must run before any business logic to ensure tenant isolation.
 *
 * <p>Expected headers:</p>
 * <ul>
 *   <li>X-Tenant-Id - The tenant identifier</li>
 *   <li>X-Membership-Id - The user's membership in the tenant</li>
 *   <li>X-User-Id - The user identifier</li>
 * </ul>
 */
@Component
@Order(1)
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String MEMBERSHIP_HEADER = "X-Membership-Id";
    public static final String USER_HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            extractAndSetTenantContext(request);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void extractAndSetTenantContext(HttpServletRequest request) {
        String tenantIdHeader = request.getHeader(TENANT_HEADER);
        String membershipIdHeader = request.getHeader(MEMBERSHIP_HEADER);
        String userIdHeader = request.getHeader(USER_HEADER);

        if (tenantIdHeader != null && !tenantIdHeader.isEmpty()) {
            try {
                UUID tenantId = UUID.fromString(tenantIdHeader);
                TenantContext.setCurrentTenant(tenantId);
                log.debug("Tenant context set: {}", tenantId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid tenant ID format: {}", tenantIdHeader);
            }
        }

        if (membershipIdHeader != null && !membershipIdHeader.isEmpty()) {
            try {
                UUID membershipId = UUID.fromString(membershipIdHeader);
                TenantContext.setCurrentMembership(membershipId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid membership ID format: {}", membershipIdHeader);
            }
        }

        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                UUID userId = UUID.fromString(userIdHeader);
                TenantContext.setCurrentUser(userId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid user ID format: {}", userIdHeader);
            }
        }
    }
}
