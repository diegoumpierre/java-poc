package com.poc.gateway.filter;

import com.poc.gateway.service.PermissionCacheService;
import com.poc.gateway.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Enriches requests with X-User-Permissions header.
 * Does NOT block requests — only adds permissions for downstream services.
 * Runs AFTER JwtAuthWebFilter (+10) and BEFORE EntitlementWebFilter (+15).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 12)
@RequiredArgsConstructor
@Slf4j
public class PermissionWebFilter implements WebFilter {

    private final PermissionCacheService permissionCacheService;
    private final ProxyService proxyService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }

        if (proxyService.isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String membershipId = exchange.getRequest().getHeaders().getFirst("X-Membership-Id");
        if (membershipId == null || membershipId.isEmpty()) {
            return chain.filter(exchange);
        }

        return permissionCacheService.getPermissions(membershipId)
                .flatMap(permissions -> {
                    if (permissions.isEmpty()) {
                        return chain.filter(exchange);
                    }

                    String permissionsHeader = String.join(",", permissions);

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Permissions", permissionsHeader)
                            .build();

                    log.debug("Added X-User-Permissions header with {} permissions for membership {}",
                            permissions.size(), membershipId);

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .onErrorResume(ex -> {
                    log.warn("Permission enrichment failed for membership {}: {} - continuing without permissions",
                            membershipId, ex.getMessage());
                    return chain.filter(exchange);
                });
    }
}
