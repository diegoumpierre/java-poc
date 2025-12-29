package com.poc.gateway.service;

import com.poc.gateway.config.GatewayProperties;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProxyService {

    private final WebClient webClient;
    private final GatewayProperties gatewayProperties;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    private static final String[] HEADERS_TO_FORWARD = {
            "Authorization", "Content-Type", "Accept", "Accept-Language",
            "X-User-Id", "X-User-Email", "X-Tenant-Id", "X-Membership-Id",
            "X-User-Permissions", "X-Correlation-ID", "X-Request-ID", "Cookie"
    };

    public Mono<ResponseEntity<byte[]>> forward(ServerHttpRequest request) {
        String path = request.getPath().value();
        String query = request.getURI().getRawQuery();
        HttpMethod method = request.getMethod();

        // Block internal endpoints - they are for inter-service communication only
        if (path.contains("/internal/")) {
            log.warn("Blocked access to internal path: {}", path);
            return Mono.just(ResponseEntity.status(403)
                    .header("Content-Type", "application/json")
                    .body("{\"error\":\"Forbidden\",\"message\":\"Internal endpoints are not accessible through the gateway\"}".getBytes()));
        }

        return findTargetService(path)
                .map(entry -> {
                    String serviceName = entry.getKey();
                    GatewayProperties.RouteConfig config = entry.getValue();
                    String targetUrl = buildTargetUrl(config, path);

                    // Append query string
                    if (query != null && !query.isEmpty()) {
                        targetUrl += "?" + query;
                    }

                    log.debug("Proxying {} {} -> {}", method, path, targetUrl);

                    // Get or create circuit breaker for this service
                    var circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);

                    return webClient.method(method)
                            .uri(targetUrl)
                            .headers(headers -> copyHeaders(request.getHeaders(), headers))
                            .body(BodyInserters.fromDataBuffers(request.getBody()))
                            .exchangeToMono(response -> {
                                HttpHeaders responseHeaders = filterResponseHeaders(response.headers().asHttpHeaders());
                                HttpStatusCode statusCode = response.statusCode();

                                return response.bodyToMono(byte[].class)
                                        .defaultIfEmpty(new byte[0])
                                        .map(body -> ResponseEntity.status(statusCode)
                                                .headers(responseHeaders)
                                                .body(body));
                            })
                            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                            .onErrorResume(CallNotPermittedException.class, ex -> {
                                log.warn("Circuit breaker OPEN for service {}: {}", serviceName, ex.getMessage());
                                return Mono.just(ResponseEntity.status(503)
                                        .header("Content-Type", "application/json")
                                        .body(("{\"error\":\"Service unavailable\",\"service\":\"" + serviceName + "\",\"reason\":\"circuit-breaker-open\"}").getBytes()));
                            })
                            .onErrorResume(ex -> {
                                log.error("Proxy error to {}: {}", serviceName, ex.getMessage());
                                return Mono.just(ResponseEntity.status(503)
                                        .header("Content-Type", "application/json")
                                        .body(("{\"error\":\"Service unavailable\",\"service\":\"" + serviceName + "\"}").getBytes()));
                            });
                })
                .orElseGet(() -> {
                    log.warn("No route found for path: {}", path);
                    byte[] errorBody = ("{\"error\":\"Not found\",\"path\":\"" + path + "\"}").getBytes();
                    return Mono.just(ResponseEntity.status(404)
                            .header("Content-Type", "application/json")
                            .body(errorBody));
                });
    }

    public Optional<Map.Entry<String, GatewayProperties.RouteConfig>> findTargetService(String path) {
        return gatewayProperties.getRoutes().entrySet().stream()
                .filter(entry -> matchesAnyPath(path, entry.getValue().getPaths()))
                .findFirst();
    }

    public boolean isPublicPath(String path) {
        return gatewayProperties.getRoutes().values().stream()
                .anyMatch(config -> matchesAnyPath(path, config.getPublicPaths()));
    }

    public Optional<Integer> getRateLimit(String path) {
        return gatewayProperties.getRoutes().values().stream()
                .flatMap(config -> config.getRateLimitedPaths().entrySet().stream())
                .filter(entry -> pathMatches(path, entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private boolean matchesAnyPath(String path, java.util.List<String> patterns) {
        return patterns.stream().anyMatch(pattern -> pathMatches(path, pattern));
    }

    private boolean pathMatches(String path, String pattern) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        return path.equals(pattern) || path.startsWith(pattern + "/") || path.startsWith(pattern + "?");
    }

    private String buildTargetUrl(GatewayProperties.RouteConfig config, String path) {
        String targetPath = path;
        if (config.getStripPrefix() != null && path.startsWith(config.getStripPrefix())) {
            targetPath = path.substring(config.getStripPrefix().length());
            if (!targetPath.startsWith("/")) {
                targetPath = "/" + targetPath;
            }
        }
        return config.getUrl() + targetPath;
    }

    private void copyHeaders(HttpHeaders source, HttpHeaders target) {
        for (String header : HEADERS_TO_FORWARD) {
            var values = source.get(header);
            if (values != null && !values.isEmpty()) {
                target.addAll(header, values);
            }
        }
    }

    private HttpHeaders filterResponseHeaders(HttpHeaders source) {
        HttpHeaders filtered = new HttpHeaders();
        source.forEach((name, values) -> {
            // Skip hop-by-hop headers
            if (!name.equalsIgnoreCase("Transfer-Encoding") &&
                !name.equalsIgnoreCase("Connection") &&
                !name.equalsIgnoreCase("Keep-Alive")) {
                filtered.addAll(name, values);
            }
        });
        return filtered;
    }
}
