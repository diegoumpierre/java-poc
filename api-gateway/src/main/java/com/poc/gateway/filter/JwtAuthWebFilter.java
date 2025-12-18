package com.poc.gateway.filter;

import com.poc.gateway.service.JwtService;
import com.poc.gateway.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
@Slf4j
public class JwtAuthWebFilter implements WebFilter {

    private final JwtService jwtService;
    private final ProxyService proxyService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Skip non-API paths
        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }

        // Skip public paths
        if (proxyService.isPublicPath(path)) {
            log.debug("Public path, skipping JWT validation: {}", path);
            return chain.filter(exchange);
        }

        // Validate JWT
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            var claims = jwtService.validateToken(token);
            if (claims == null) {
                log.debug("Invalid JWT token for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Add user info to headers for downstream services
            // JWT standard uses "sub" (subject) for user ID
            String userId = claims.getSubject(); // Use standard JWT subject claim
            String email = claims.get("email", String.class);
            String tenantId = claims.get("tenantId", String.class);
            String membershipId = claims.get("membershipId", String.class);

            log.debug("JWT claims - userId(sub): {}, email: {}, tenantId: {}, membershipId: {}", userId, email, tenantId, membershipId);

            ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();

            if (userId != null) {
                requestBuilder.header("X-User-Id", userId);
            }
            if (email != null) {
                requestBuilder.header("X-User-Email", email);
            }
            if (tenantId != null) {
                requestBuilder.header("X-Tenant-Id", tenantId);
            }
            if (membershipId != null) {
                requestBuilder.header("X-Membership-Id", membershipId);
            }

            ServerHttpRequest mutatedRequest = requestBuilder.build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
