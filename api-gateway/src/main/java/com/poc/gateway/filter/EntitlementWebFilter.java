package com.poc.gateway.filter;

import com.poc.gateway.config.GatewayProperties;
import com.poc.gateway.service.EntitlementCacheService;
import com.poc.gateway.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enforces entitlement checks at the gateway level.
 * Runs AFTER JwtAuthWebFilter (which sets X-Tenant-Id header).
 *
 * Flow:
 * 1. Non-API or public paths → pass through
 * 2. Route has no requiredFeatures → pass through
 * 3. Missing X-Tenant-Id → 403
 * 4. Check Redis cache (or fetch from billing-service) for tenant's features
 * 5. Tenant has ANY required feature → pass through (OR logic)
 * 6. No match → 403
 * 7. Billing-service unavailable → fail-open (allow request)
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 15)
@RequiredArgsConstructor
@Slf4j
public class EntitlementWebFilter implements WebFilter {

    private final ProxyService proxyService;
    private final EntitlementCacheService entitlementCacheService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Skip if entitlement checking is disabled
        if (!entitlementCacheService.isEnabled()) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getPath().value();

        // Skip non-API paths
        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }

        // Skip public paths
        if (proxyService.isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // Find matching route and check if it has required features
        List<String> requiredFeatures = proxyService.findTargetService(path)
                .map(Map.Entry::getValue)
                .map(GatewayProperties.RouteConfig::getRequiredFeatures)
                .orElse(List.of());

        if (requiredFeatures.isEmpty()) {
            return chain.filter(exchange);
        }

        // Need tenant ID to check entitlements (set by JwtAuthWebFilter)
        String tenantId = exchange.getRequest().getHeaders().getFirst("X-Tenant-Id");
        if (tenantId == null || tenantId.isEmpty()) {
            log.debug("No X-Tenant-Id header for entitlement check on path: {}", path);
            return forbidden(exchange, requiredFeatures);
        }

        // Check entitlements
        return entitlementCacheService.getFeatureCodes(tenantId)
                .flatMap(features -> {
                    if (features.isEmpty()) {
                        // Empty set from billing = fail-open if billing was unreachable,
                        // or tenant truly has no entitlements.
                        // The cache service returns empty set on billing failure (fail-open).
                        // We log and allow to avoid blocking when billing is down.
                        log.warn("No entitlements found for tenant {} on path {} - allowing (fail-open)", tenantId, path);
                        return chain.filter(exchange);
                    }

                    // OR logic: tenant needs ANY of the required features
                    boolean hasAccess = requiredFeatures.stream()
                            .anyMatch(features::contains);

                    if (hasAccess) {
                        log.debug("Entitlement check passed for tenant {} on path {}", tenantId, path);
                        return chain.filter(exchange);
                    }

                    log.info("Entitlement denied for tenant {} on path {} - required: {}, has: {}",
                            tenantId, path, requiredFeatures, features);
                    return forbidden(exchange, requiredFeatures);
                })
                .onErrorResume(ex -> {
                    // Fail-open on any unexpected error
                    log.warn("Entitlement check error for tenant {} on path {}: {} - allowing (fail-open)",
                            tenantId, path, ex.getMessage());
                    return chain.filter(exchange);
                });
    }

    private Mono<Void> forbidden(ServerWebExchange exchange, List<String> requiredFeatures) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\":\"Feature not available\",\"requiredFeatures\":" + toJsonArray(requiredFeatures) + "}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }

    private String toJsonArray(List<String> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(items.get(i).replace("\"", "\\\"")).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }
}
