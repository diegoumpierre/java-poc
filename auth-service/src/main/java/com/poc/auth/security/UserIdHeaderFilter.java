package com.poc.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * Filter that injects X-User-Id header from authenticated JWT token
 * This allows controllers to use @RequestHeader("X-User-Id") pattern
 * even when called directly (without API Gateway)
 */
@Component
@Slf4j
public class UserIdHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userId = userDetails.getId().toString();
            String tenantId = userDetails.getTenantId() != null ? userDetails.getTenantId().toString() : null;

            // Wrap request to add X-User-Id and X-Tenant-Id headers
            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if ("X-User-Id".equalsIgnoreCase(name)) {
                        return userId;
                    }
                    if ("X-Tenant-Id".equalsIgnoreCase(name) && tenantId != null) {
                        return tenantId;
                    }
                    return super.getHeader(name);
                }

                @Override
                public Enumeration<String> getHeaders(String name) {
                    if ("X-User-Id".equalsIgnoreCase(name)) {
                        return Collections.enumeration(Collections.singletonList(userId));
                    }
                    if ("X-Tenant-Id".equalsIgnoreCase(name) && tenantId != null) {
                        return Collections.enumeration(Collections.singletonList(tenantId));
                    }
                    return super.getHeaders(name);
                }

                @Override
                public Enumeration<String> getHeaderNames() {
                    Set<String> names = new HashSet<>();
                    Enumeration<String> original = super.getHeaderNames();
                    while (original.hasMoreElements()) {
                        names.add(original.nextElement());
                    }
                    names.add("X-User-Id");
                    if (tenantId != null) {
                        names.add("X-Tenant-Id");
                    }
                    return Collections.enumeration(names);
                }
            };

            log.debug("Injected X-User-Id: {}, X-Tenant-Id: {}", userId, tenantId);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
