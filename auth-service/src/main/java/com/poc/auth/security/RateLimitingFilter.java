package com.poc.auth.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting filter to prevent brute force attacks
 * Uses token bucket algorithm via Bucket4j
 */
@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    @Value("${app.security.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${app.security.rate-limit.capacity:5}")
    private long capacity;

    @Value("${app.security.rate-limit.refill-tokens:5}")
    private long refillTokens;

    @Value("${app.security.rate-limit.refill-duration-minutes:1}")
    private long refillDurationMinutes;

    // Cache of buckets per IP address
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Only apply rate limiting to auth endpoints
        String path = request.getRequestURI();
        if (!rateLimitEnabled || !shouldApplyRateLimit(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get or create bucket for this IP
        String clientIp = getClientIP(request);
        Bucket bucket = resolveBucket(clientIp);

        // Try to consume a token
        if (bucket.tryConsume(1)) {
            // Request allowed
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, path);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"success\":false,\"message\":\"Too many requests. Please try again later.\",\"retryAfter\":%d}",
                refillDurationMinutes * 60
            ));
        }
    }

    /**
     * Determine if rate limiting should be applied to this path
     */
    private boolean shouldApplyRateLimit(String path) {
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/register") ||
               path.startsWith("/api/auth/forgot-password") ||
               path.startsWith("/api/auth/reset-password") ||
               path.startsWith("/api/auth/verify-code");
    }

    /**
     * Get or create a bucket for the given key (IP address)
     */
    private Bucket resolveBucket(String key) {
        return bucketCache.computeIfAbsent(key, k -> createNewBucket());
    }

    /**
     * Create a new rate limiting bucket with configured capacity and refill rate
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(
            capacity,
            Refill.intervally(refillTokens, Duration.ofMinutes(refillDurationMinutes))
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    /**
     * Extract client IP address from request
     * Checks X-Forwarded-For header for proxy scenarios
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Clean up old buckets periodically to prevent memory leaks
     * In production, consider using Redis or similar for distributed rate limiting
     */
    public void clearCache() {
        bucketCache.clear();
        log.info("Rate limiting cache cleared");
    }
}
