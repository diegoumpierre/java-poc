package com.poc.gateway.filter;

import com.poc.gateway.service.JwtService;
import com.poc.gateway.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

/**
 * Rate limiting filter with tenant-aware support.
 *
 * Rate limiting strategy:
 * 1. For authenticated requests with tenant: rate limit per tenant + path
 * 2. For authenticated requests without tenant: rate limit per user + path
 * 3. For unauthenticated requests: rate limit per IP + path
 *
 * This ensures:
 * - Tenant isolation: one tenant's traffic doesn't affect another
 * - Fair usage: limits apply per tenant, not globally
 * - Protection: unauthenticated endpoints are still protected by IP
 *
 * Redis key format:
 * - Tenant: rate:{tenantId}:{path}
 * - User: rate:user:{userId}:{path}
 * - IP: rate:ip:{clientIp}:{path}
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@RequiredArgsConstructor
@Slf4j
public class RateLimiterWebFilter implements WebFilter {

    private static final String RATE_PREFIX = "rate:";
    private static final String TENANT_PREFIX = "tenant:";
    private static final String USER_PREFIX = "user:";
    private static final String IP_PREFIX = "ip:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ProxyService proxyService;
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Check if path has rate limiting
        Optional<Integer> rateLimit = proxyService.getRateLimit(path);
        if (rateLimit.isEmpty()) {
            return chain.filter(exchange);
        }

        int limit = rateLimit.get();
        String rateLimitKey = buildRateLimitKey(exchange, path);

        return redisTemplate.opsForValue().increment(rateLimitKey)
                .flatMap(count -> {
                    if (count == 1) {
                        // First request, set expiry
                        return redisTemplate.expire(rateLimitKey, Duration.ofSeconds(1))
                                .then(addRateLimitHeaders(exchange, limit, limit - 1))
                                .then(chain.filter(exchange));
                    } else if (count <= limit) {
                        addRateLimitHeaders(exchange, limit, (int) (limit - count));
                        return chain.filter(exchange);
                    } else {
                        String identifier = extractIdentifier(exchange);
                        log.warn("Rate limit exceeded for {} - identifier: {} (key: {})", path, identifier, rateLimitKey);
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(limit));
                        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
                        exchange.getResponse().getHeaders().add("X-RateLimit-Reset", "1");
                        exchange.getResponse().getHeaders().add("Retry-After", "1");
                        return exchange.getResponse().setComplete();
                    }
                })
                .onErrorResume(ex -> {
                    // If Redis is unavailable, allow the request
                    log.warn("Redis unavailable for rate limiting, allowing request: {}", ex.getMessage());
                    return chain.filter(exchange);
                });
    }

    /**
     * Build rate limit key based on authentication context.
     * Priority: tenantId > userId > clientIp
     */
    private String buildRateLimitKey(ServerWebExchange exchange, String path) {
        // Try to get tenant ID from JWT
        String tenantId = extractTenantId(exchange);
        if (tenantId != null && !tenantId.isEmpty()) {
            return RATE_PREFIX + TENANT_PREFIX + tenantId + ":" + normalizePath(path);
        }

        // Try to get user ID from JWT
        String userId = extractUserId(exchange);
        if (userId != null && !userId.isEmpty()) {
            return RATE_PREFIX + USER_PREFIX + userId + ":" + normalizePath(path);
        }

        // Fall back to IP-based rate limiting
        String clientIp = getClientIp(exchange);
        return RATE_PREFIX + IP_PREFIX + clientIp + ":" + normalizePath(path);
    }

    /**
     * Extract tenant ID from JWT token
     */
    private String extractTenantId(ServerWebExchange exchange) {
        String token = extractToken(exchange);
        if (token != null) {
            try {
                return jwtService.getTenantId(token);
            } catch (Exception ex) {
                log.debug("Could not extract tenantId from token: {}", ex.getMessage());
            }
        }
        return null;
    }

    /**
     * Extract user ID from JWT token
     */
    private String extractUserId(ServerWebExchange exchange) {
        String token = extractToken(exchange);
        if (token != null) {
            try {
                return jwtService.getUserId(token);
            } catch (Exception ex) {
                log.debug("Could not extract userId from token: {}", ex.getMessage());
            }
        }
        return null;
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Get identifier for logging purposes
     */
    private String extractIdentifier(ServerWebExchange exchange) {
        String tenantId = extractTenantId(exchange);
        if (tenantId != null) {
            return "tenant:" + tenantId;
        }

        String userId = extractUserId(exchange);
        if (userId != null) {
            return "user:" + userId;
        }

        return "ip:" + getClientIp(exchange);
    }

    /**
     * Normalize path for consistent rate limiting
     * e.g., /api/auth/login and /api/auth/login/ should be the same
     */
    private String normalizePath(String path) {
        // Remove trailing slash
        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        // Remove query params would be handled elsewhere (this is just path)
        return path;
    }

    private String getClientIp(ServerWebExchange exchange) {
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        var remoteAddress = exchange.getRequest().getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
    }

    private Mono<Void> addRateLimitHeaders(ServerWebExchange exchange, int limit, int remaining) {
        exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(limit));
        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));
        exchange.getResponse().getHeaders().add("X-RateLimit-Reset", "1");
        return Mono.empty();
    }
}
