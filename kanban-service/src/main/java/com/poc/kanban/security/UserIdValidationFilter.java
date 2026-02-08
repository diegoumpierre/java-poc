package com.poc.kanban.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that validates the X-User-Id header format.
 * Ensures the header contains a valid UUID to prevent header injection attacks.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@Slf4j
public class UserIdValidationFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader(USER_ID_HEADER);

        // Skip validation for paths that don't require authentication
        if (isPublicPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Validate X-User-Id if present
        if (userId != null && !userId.isEmpty()) {
            if (!isValidUUID(userId)) {
                log.warn("Invalid X-User-Id header format: {}", sanitize(userId));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\":\"Bad Request\",\"message\":\"Invalid X-User-Id header format\"}");
                return;
            }
            log.debug("Valid X-User-Id header: {}", userId);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidUUID(String value) {
        if (value == null || value.length() != 36) {
            return false;
        }
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isPublicPath(String path) {
        return path.contains("/actuator/health") ||
               path.contains("/actuator/info") ||
               path.contains("/swagger-ui") ||
               path.contains("/api-docs") ||
               path.contains("/v3/api-docs") ||
               path.equals("/error");
    }

    /**
     * Sanitize user input for logging to prevent log injection
     */
    private String sanitize(String input) {
        if (input == null) {
            return "null";
        }
        // Remove newlines and limit length
        return input.replaceAll("[\\r\\n]", "").substring(0, Math.min(input.length(), 50));
    }
}
