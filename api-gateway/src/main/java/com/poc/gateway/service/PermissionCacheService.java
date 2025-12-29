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
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionCacheService {

    private static final String CACHE_PREFIX = "permission:membership:";
    private static final TypeReference<Set<String>> PERMISSION_SET_TYPE = new TypeReference<>() {};

    private final ReactiveStringRedisTemplate redisTemplate;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${permission.auth-url:http://localhost:8091}")
    private String authUrl;

    @Value("${permission.cache-ttl-minutes:5}")
    private int cacheTtlMinutes;

    public Mono<Set<String>> getPermissions(String membershipId) {
        String cacheKey = CACHE_PREFIX + membershipId;

        return redisTemplate.opsForValue().get(cacheKey)
                .flatMap(this::parsePermissions)
                .switchIfEmpty(fetchAndCache(membershipId, cacheKey))
                .onErrorResume(ex -> {
                    log.warn("Redis error fetching permissions for membership {}: {}", membershipId, ex.getMessage());
                    return fetchFromAuth(membershipId);
                });
    }

    private Mono<Set<String>> parsePermissions(String json) {
        try {
            Set<String> permissions = objectMapper.readValue(json, PERMISSION_SET_TYPE);
            return Mono.just(permissions);
        } catch (Exception e) {
            log.warn("Failed to parse cached permissions: {}", e.getMessage());
            return Mono.empty();
        }
    }

    private Mono<Set<String>> fetchAndCache(String membershipId, String cacheKey) {
        return fetchFromAuth(membershipId)
                .flatMap(permissions -> {
                    if (permissions.isEmpty()) {
                        return Mono.just(permissions);
                    }
                    return cachePermissions(cacheKey, permissions)
                            .thenReturn(permissions);
                });
    }

    private Mono<Set<String>> fetchFromAuth(String membershipId) {
        var cb = circuitBreakerRegistry.circuitBreaker("auth-service");
        return webClient.get()
                .uri(authUrl + "/api/auth/internal/permissions?membershipId=" + membershipId)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(body -> {
                    try {
                        Set<String> permissions = objectMapper.readValue(body, PERMISSION_SET_TYPE);
                        log.debug("Fetched {} permissions for membership {}", permissions.size(), membershipId);
                        return Mono.just(permissions);
                    } catch (Exception e) {
                        log.warn("Failed to parse auth response for membership {}: {}", membershipId, e.getMessage());
                        return Mono.just(Collections.<String>emptySet());
                    }
                })
                .transformDeferred(CircuitBreakerOperator.of(cb))
                .onErrorResume(CallNotPermittedException.class, ex -> {
                    log.warn("Circuit breaker OPEN for auth-service (permissions), membership {}", membershipId);
                    return Mono.just(Collections.emptySet());
                })
                .onErrorResume(ex -> {
                    log.warn("Auth service unavailable for membership {}: {}", membershipId, ex.getMessage());
                    return Mono.just(Collections.emptySet());
                });
    }

    private Mono<Boolean> cachePermissions(String cacheKey, Set<String> permissions) {
        try {
            String json = objectMapper.writeValueAsString(List.copyOf(permissions));
            return redisTemplate.opsForValue()
                    .set(cacheKey, json, Duration.ofMinutes(cacheTtlMinutes))
                    .onErrorResume(ex -> {
                        log.warn("Failed to cache permissions: {}", ex.getMessage());
                        return Mono.just(false);
                    });
        } catch (Exception e) {
            log.warn("Failed to serialize permissions for cache: {}", e.getMessage());
            return Mono.just(false);
        }
    }
}
