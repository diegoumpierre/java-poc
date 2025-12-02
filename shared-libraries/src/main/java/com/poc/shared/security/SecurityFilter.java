package com.poc.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filter that extracts user permissions from X-User-Permissions header (injected by API Gateway).
 * Runs after TenantFilter (Order 1).
 */
@Component
@Order(2)
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    public static final String PERMISSIONS_HEADER = "X-User-Permissions";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            extractAndSetPermissions(request);
            filterChain.doFilter(request, response);
        } finally {
            SecurityContext.clear();
        }
    }

    private void extractAndSetPermissions(HttpServletRequest request) {
        String permissionsHeader = request.getHeader(PERMISSIONS_HEADER);

        if (permissionsHeader != null && !permissionsHeader.isEmpty()) {
            Set<String> permissions = Arrays.stream(permissionsHeader.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            SecurityContext.setPermissions(permissions);
            log.debug("Security context set with {} permissions", permissions.size());
        } else {
            SecurityContext.setPermissions(Collections.emptySet());
        }
    }
}
