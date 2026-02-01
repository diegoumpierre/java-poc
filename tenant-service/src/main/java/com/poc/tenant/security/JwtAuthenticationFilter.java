package com.poc.tenant.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.poc.shared.security.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${app.security.enabled:false}")
    private boolean securityEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            extractUserInfoFromHeaders(request);

            UUID userId = TenantContext.getUserId();
            if (userId != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            SecurityContext.clear();
        }
    }

    private void extractUserInfoFromHeaders(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        String tenantIdHeader = request.getHeader("X-Tenant-Id");
        String userEmailHeader = request.getHeader("X-User-Email");

        if (StringUtils.hasText(userIdHeader)) {
            try {
                TenantContext.setUserId(UUID.fromString(userIdHeader));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid X-User-Id header: {}", userIdHeader);
            }
        }
        if (StringUtils.hasText(tenantIdHeader)) {
            try {
                TenantContext.setTenantId(UUID.fromString(tenantIdHeader));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid X-Tenant-Id header: {}", tenantIdHeader);
            }
        }
        if (StringUtils.hasText(userEmailHeader)) {
            TenantContext.setUserEmail(userEmailHeader);
        }

        String rolesHeader = request.getHeader("X-User-Roles");
        if (StringUtils.hasText(rolesHeader)) {
            Set<String> roles = Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
            TenantContext.setUserRoles(roles);
        }

        String permissionsHeader = request.getHeader("X-User-Permissions");
        if (StringUtils.hasText(permissionsHeader)) {
            Set<String> permissions = Arrays.stream(permissionsHeader.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            SecurityContext.setPermissions(permissions);
        }
    }
}
