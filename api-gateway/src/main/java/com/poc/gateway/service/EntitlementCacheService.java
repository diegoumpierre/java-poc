package com.poc.gateway.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntitlementCacheService {

    private static final String CACHE_PREFIX = "entitlement:";
    private static final TypeReference<List<Map<String, Object>>> ENTITLEMENT_LIST_TYPE = new TypeReference<>() {};

    private final ReactiveStringRedisTemplate redisTemplate;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${entitlement.tenant-url:http://localhost:8094}")
    private String tenantUrl;

    @Value("${entitlement.cache-ttl-minutes:5}")
    private int cacheTtlMinutes;

    @Value("${entitlement.enabled:true}")
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Get feature codes for a tenant. Checks Redis cache first, falls back to tenant-service.
     * Returns empty set on any failure (fail-open).
     */
    public Mono<Set<String>> getFeatureCodes(String tenantId) {
        String cacheKey = CACHE_PREFIX + tenantId;

        return redisTemplate.opsForValue().get(cacheKey)
                .flatMap(this::parseFeatureCodes)
                .switchIfEmpty(fetchAndCache(tenantId, cacheKey))
                .onErrorResume(ex -> {
                    log.warn("Redis error fetching entitlements for tenant {}: {}", tenantId, ex.getMessage());
                    return fetchFromTenant(tenantId);
                });
    }

    private Mono<Set<String>> parseFeatureCodes(String json) {
        try {
            List<String> codes = objectMapper.readValue(json, new TypeReference<>() {});
            return Mono.just(Set.copyOf(codes));
        } catch (Exception e) {
            log.warn("Failed to parse cached entitlements: {}", e.getMessage());
            return Mono.empty();
        }
    }

    private Mono<Set<String>> fetchAndCache(String tenantId, String cacheKey) {
        return fetchFromTenant(tenantId)
                .flatMap(features -> {
                    if (features.isEmpty()) {
                        return Mono.just(features);
                    }
                    return cacheFeatures(cacheKey, features)
                            .thenReturn(features);
                });
    }

    private Mono<Set<String>> fetchFromTenant(String tenantId) {
        var cb = circuitBreakerRegistry.circuitBreaker("tenant-service");
        return webClient.get()
                .uri(tenantUrl + "/api/tenants/entitlements")
                .header("X-Tenant-Id", tenantId)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        List<Map<String, Object>> entitlements = objectMapper.readValue(body, ENTITLEMENT_LIST_TYPE);
                        Set<String> features = entitlements.stream()
                                .filter(e -> Boolean.TRUE.equals(e.get("enabled")))
                                .map(e -> (String) e.get("featureCode"))
                                .filter(code -> code != null && !code.isEmpty())
                                .collect(Collectors.toSet());
                        log.debug("Fetched {} entitlements for tenant {}: {}", features.size(), tenantId, features);
                        return Mono.just(features);
                    } catch (Exception e) {
                        log.warn("Failed to parse tenant response for tenant {}: {}", tenantId, e.getMessage());
                        return Mono.just(Collections.<String>emptySet());
                    }
                })
                .transformDeferred(CircuitBreakerOperator.of(cb))
                .onErrorResume(CallNotPermittedException.class, ex -> {
                    log.warn("Circuit breaker OPEN for tenant-service (entitlements), tenant {}", tenantId);
                    return Mono.just(Collections.emptySet());
                })
                .onErrorResume(ex -> {
                    log.warn("Tenant service unavailable for tenant {}: {}", tenantId, ex.getMessage());
                    return Mono.just(Collections.emptySet());
                });
    }

    private Mono<Boolean> cacheFeatures(String cacheKey, Set<String> features) {
        try {
            String json = objectMapper.writeValueAsString(List.copyOf(features));
            return redisTemplate.opsForValue()
                    .set(cacheKey, json, Duration.ofMinutes(cacheTtlMinutes))
                    .onErrorResume(ex -> {
                        log.warn("Failed to cache entitlements: {}", ex.getMessage());
                        return Mono.just(false);
                    });
        } catch (Exception e) {
            log.warn("Failed to serialize entitlements for cache: {}", e.getMessage());
            return Mono.just(false);
        }
    }
}
