package com.poc.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poc.auth.model.response.TenantResponse;
import com.poc.auth.service.TenantQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Cached Tenant Query Service
 *
 * Provides caching layer for tenant queries using Redis to reduce:
 * - HTTP calls to tenant-service
 * - N+1 latency on login (tenant selection iterates all user tenants)
 *
 * Cache Strategy:
 * - Cache TTL: 15 minutes (configurable) - tenants rarely change during a session
 * - Cache key: tenant:id:{tenantId}, tenant:slug:{slug}
 * - Invalidation: TTL-based (tenant data is stable)
 *
 * Requires: app.cache.tenant.enabled=true
 */
@Service("cachedTenantQueryService")
@Primary
@ConditionalOnProperty(name = "app.cache.tenant.enabled", havingValue = "true")
@Slf4j
public class CachedTenantQueryService implements TenantQueryService {

    private final TenantQueryService delegate;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.cache.tenant.ttl:15}")
    private int cacheTtlMinutes;

    @Value("${app.cache.tenant.enabled:true}")
    private boolean cacheEnabled;

    private static final String CACHE_KEY_ID_PREFIX = "tenant:id:";
    private static final String CACHE_KEY_SLUG_PREFIX = "tenant:slug:";

    @Autowired
    public CachedTenantQueryService(
            @Qualifier("feignTenantQueryService") TenantQueryService delegate,
            @Autowired(required = false) RedisTemplate<String, String> redisTemplate) {
        this.delegate = delegate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public List<TenantResponse> findAll() {
        // Not cached - rarely called, returns full list
        return delegate.findAll();
    }

    @Override
    public TenantResponse findById(UUID id) {
        if (!cacheEnabled || redisTemplate == null) {
            return delegate.findById(id);
        }

        try {
            String cacheKey = CACHE_KEY_ID_PREFIX + id;
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);

            if (cachedJson != null && !cachedJson.isEmpty()) {
                log.debug("[CachedTenant] Cache HIT for tenant: {}", id);
                return deserializeTenant(cachedJson);
            }

            log.debug("[CachedTenant] Cache MISS for tenant: {}", id);
            TenantResponse tenant = delegate.findById(id);

            cacheTenant(cacheKey, tenant);
            return tenant;

        } catch (Exception e) {
            log.error("[CachedTenant] Cache error for tenant: {}, falling back to delegate", id, e);
            return delegate.findById(id);
        }
    }

    @Override
    public TenantResponse findBySlug(String slug) {
        if (!cacheEnabled || redisTemplate == null) {
            return delegate.findBySlug(slug);
        }

        try {
            String cacheKey = CACHE_KEY_SLUG_PREFIX + slug;
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);

            if (cachedJson != null && !cachedJson.isEmpty()) {
                log.debug("[CachedTenant] Cache HIT for slug: {}", slug);
                return deserializeTenant(cachedJson);
            }

            log.debug("[CachedTenant] Cache MISS for slug: {}", slug);
            TenantResponse tenant = delegate.findBySlug(slug);

            cacheTenant(cacheKey, tenant);
            return tenant;

        } catch (Exception e) {
            log.error("[CachedTenant] Cache error for slug: {}, falling back to delegate", slug, e);
            return delegate.findBySlug(slug);
        }
    }

    @Override
    public List<TenantResponse> search(String query) {
        // Not cached - dynamic search results
        return delegate.search(query);
    }

    @Override
    public List<TenantResponse> findByParentId(UUID parentId) {
        // Not cached - hierarchy queries are infrequent
        return delegate.findByParentId(parentId);
    }

    /**
     * Invalidate cached tenant by ID
     */
    public void invalidateTenantCache(UUID tenantId) {
        if (!cacheEnabled || redisTemplate == null) {
            return;
        }

        try {
            String cacheKey = CACHE_KEY_ID_PREFIX + tenantId;
            Boolean deleted = redisTemplate.delete(cacheKey);

            if (Boolean.TRUE.equals(deleted)) {
                log.info("[CachedTenant] Invalidated cache for tenant: {}", tenantId);
            }
        } catch (Exception e) {
            log.error("[CachedTenant] Failed to invalidate cache for tenant: {}", tenantId, e);
        }
    }

    private void cacheTenant(String cacheKey, TenantResponse tenant) {
        try {
            String json = objectMapper.writeValueAsString(tenant);
            redisTemplate.opsForValue().set(cacheKey, json, cacheTtlMinutes, TimeUnit.MINUTES);
            log.debug("[CachedTenant] Cached tenant (key: {}, TTL: {} min)", cacheKey, cacheTtlMinutes);
        } catch (JsonProcessingException e) {
            log.error("[CachedTenant] Failed to serialize tenant for caching: {}", e.getMessage());
        }
    }

    private TenantResponse deserializeTenant(String json) {
        try {
            return objectMapper.readValue(json, TenantResponse.class);
        } catch (JsonProcessingException e) {
            log.error("[CachedTenant] Failed to deserialize tenant from cache: {}", e.getMessage());
            return null;
        }
    }
}
