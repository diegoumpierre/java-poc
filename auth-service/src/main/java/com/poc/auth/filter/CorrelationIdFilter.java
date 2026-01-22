package com.poc.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that adds contextual information to MDC for logging and distributed tracing.
 *
 * MDC keys added:
 * - correlationId: From X-Correlation-ID header or generated
 * - requestId: Unique ID for this specific request
 * - tenantId: From X-Tenant-Id header (set by API Gateway)
 * - userId: From X-User-Id header (set by API Gateway)
 * - clientIp: Client IP address (handles X-Forwarded-For)
 *
 * All logs will automatically include these fields for:
 * - Distributed tracing across services
 * - Multi-tenant log segregation
 * - User activity tracking
 * - Security auditing
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String TENANT_ID_HEADER = "X-Tenant-Id";
    public static final String USER_ID_HEADER = "X-User-Id";

    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    public static final String REQUEST_ID_MDC_KEY = "requestId";
    public static final String TENANT_ID_MDC_KEY = "tenantId";
    public static final String USER_ID_MDC_KEY = "userId";
    public static final String CLIENT_IP_MDC_KEY = "clientIp";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Get or generate correlation ID
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = generateCorrelationId();
            }

            // Generate unique request ID for this specific request
            String requestId = generateRequestId();

            // Get tenant and user IDs from headers (set by API Gateway after JWT validation)
            String tenantId = request.getHeader(TENANT_ID_HEADER);
            String userId = request.getHeader(USER_ID_HEADER);

            // Get client IP (handle X-Forwarded-For for proxy/load balancer)
            String clientIp = getClientIp(request);

            // Add to MDC for logging - all subsequent logs will include these
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            MDC.put(REQUEST_ID_MDC_KEY, requestId);

            // Only add if present to avoid null values in logs
            if (tenantId != null && !tenantId.isBlank()) {
                MDC.put(TENANT_ID_MDC_KEY, tenantId);
            }
            if (userId != null && !userId.isBlank()) {
                MDC.put(USER_ID_MDC_KEY, userId);
            }
            MDC.put(CLIENT_IP_MDC_KEY, clientIp);

            // Add to response headers for client reference
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            response.setHeader("X-Request-ID", requestId);

            filterChain.doFilter(request, response);
        } finally {
            // Clean up MDC to prevent memory leaks
            MDC.remove(CORRELATION_ID_MDC_KEY);
            MDC.remove(REQUEST_ID_MDC_KEY);
            MDC.remove(TENANT_ID_MDC_KEY);
            MDC.remove(USER_ID_MDC_KEY);
            MDC.remove(CLIENT_IP_MDC_KEY);
        }
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    private String generateRequestId() {
        // Shorter ID for request-level tracing
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take first IP if multiple (client, proxy1, proxy2...)
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
