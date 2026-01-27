package com.poc.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poc.auth.client.dto.EntitlementResponse;
import com.poc.auth.service.EntitlementQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Cached Entitlement Query Service
 *
 * Provides caching layer for entitlement queries using Redis to reduce:
 * - HTTP calls to tenant-service
 * - Latency (from ~50-100ms to ~1-5ms)
 *
 * Cache Strategy:
 * - Cache TTL: 10 minutes (configurable) - longer than memberships since entitlements change less frequently
 * - Cache key: entitlement:tenant:{tenantId}
 * - Invalidation: Via Kafka events from tenant-service
 *
 * Requires: app.cache.entitlement.enabled=true
 */
@Service("cachedEntitlementQueryService")
@Primary
@ConditionalOnProperty(name = "app.cache.entitlement.enabled", havingValue = "true")
@Slf4j
public class CachedEntitlementQueryService implements EntitlementQueryService {

    private final EntitlementQueryService delegate;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.cache.entitlement.ttl:10}")
    private int cacheTtlMinutes;

    @Value("${app.cache.entitlement.enabled:true}")
    private boolean cacheEnabled;

    private static final String CACHE_KEY_PREFIX = "entitlement:tenant:";

    @Autowired
    public CachedEntitlementQueryService(
            @Qualifier("feignEntitlementQueryService") EntitlementQueryService delegate,
            @Autowired(required = false) RedisTemplate<String, String> redisTemplate) {
        this.delegate = delegate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public List<EntitlementResponse> findByTenantId(UUID tenantId) {
        if (!cacheEnabled || redisTemplate == null) {
            log.debug("[CachedEntitlement] Cache disabled, using delegate for tenant: {}", tenantId);
            return delegate.findByTenantId(tenantId);
        }

        try {
            String cacheKey = CACHE_KEY_PREFIX + tenantId;
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);

            if (cachedJson != null && !cachedJson.isEmpty()) {
                log.debug("[CachedEntitlement] Cache HIT for tenant: {}", tenantId);
                return deserializeEntitlements(cachedJson);
            }

            log.debug("[CachedEntitlement] Cache MISS for tenant: {}", tenantId);
            List<EntitlementResponse> entitlements = delegate.findByTenantId(tenantId);

            cacheEntitlements(cacheKey, entitlements);
            return entitlements;

        } catch (Exception e) {
            log.error("[CachedEntitlement] Cache error for tenant: {}, falling back to delegate", tenantId, e);
            return delegate.findByTenantId(tenantId);
        }
    }

    /**
     * Invalidate cached entitlements for a tenant
     *
     * Call this when:
     * - Subscription changes (activated, cancelled)
     * - Entitlement is granted/revoked
     */
    public void invalidateTenantCache(UUID tenantId) {
        if (!cacheEnabled || redisTemplate == null) {
            log.debug("[CachedEntitlement] Cache disabled, skipping invalidation for tenant: {}", tenantId);
            return;
        }

        try {
            String cacheKey = CACHE_KEY_PREFIX + tenantId;
            Boolean deleted = redisTemplate.delete(cacheKey);

            if (Boolean.TRUE.equals(deleted)) {
                log.info("[CachedEntitlement] Invalidated cache for tenant: {}", tenantId);
            } else {
                log.debug("[CachedEntitlement] No cache to invalidate for tenant: {}", tenantId);
            }
        } catch (Exception e) {
            log.error("[CachedEntitlement] Failed to invalidate cache for tenant: {}", tenantId, e);
        }
    }

    private void cacheEntitlements(String cacheKey, List<EntitlementResponse> entitlements) {
        try {
            String json = objectMapper.writeValueAsString(entitlements);
            redisTemplate.opsForValue().set(cacheKey, json, cacheTtlMinutes, TimeUnit.MINUTES);
            log.debug("[CachedEntitlement] Cached {} entitlements (key: {}, TTL: {} min)",
                    entitlements.size(), cacheKey, cacheTtlMinutes);
        } catch (JsonProcessingException e) {
            log.error("[CachedEntitlement] Failed to serialize entitlements: {}", e.getMessage());
        }
    }

    private List<EntitlementResponse> deserializeEntitlements(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<EntitlementResponse>>() {});
        } catch (JsonProcessingException e) {
            log.error("[CachedEntitlement] Failed to deserialize entitlements: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
