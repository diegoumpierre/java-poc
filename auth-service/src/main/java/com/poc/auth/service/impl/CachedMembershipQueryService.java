package com.poc.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poc.auth.model.response.MembershipResponse;
import com.poc.auth.service.MembershipQueryService;
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
 * Cached Membership Query Service
 *
 * Provides caching layer for membership queries using Redis to reduce:
 * - HTTP calls to organization-service
 * - Latency (from ~50-100ms to ~1-5ms)
 *
 * Cache Strategy:
 * - Cache TTL: 5 minutes (configurable)
 * - Cache key: membership:user:{userId}
 * - Invalidation: Manual or via Kafka events from organization-service
 */
@Service("cachedMembershipQueryService")
@Primary
@ConditionalOnProperty(name = "app.cache.membership.enabled", havingValue = "true")
@Slf4j
public class CachedMembershipQueryService implements MembershipQueryService {

    private final MembershipQueryService delegate;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.cache.membership.ttl:5}")
    private int cacheTtlMinutes;

    @Value("${app.cache.membership.enabled:true}")
    private boolean cacheEnabled;

    private static final String CACHE_KEY_PREFIX = "membership:user:";
    private static final String CACHE_KEY_ACTIVE_PREFIX = "membership:active:user:";
    private static final String CACHE_KEY_BY_ID_PREFIX = "membership:id:";

    @Autowired
    public CachedMembershipQueryService(
            @Qualifier("feignMembershipQueryService") MembershipQueryService delegate,
            @Autowired(required = false) RedisTemplate<String, String> redisTemplate) {
        this.delegate = delegate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public List<MembershipResponse> findByUserId(UUID userId) {
        if (!cacheEnabled || redisTemplate == null) {
            log.debug("[CachedMembership] Cache disabled, using delegate for user: {}", userId);
            return delegate.findByUserId(userId);
        }

        try {
            String cacheKey = CACHE_KEY_PREFIX + userId;
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);

            if (cachedJson != null && !cachedJson.isEmpty()) {
                log.debug("[CachedMembership] Cache HIT for user: {}", userId);
                return deserializeMemberships(cachedJson);
            }

            log.debug("[CachedMembership] Cache MISS for user: {}", userId);
            List<MembershipResponse> memberships = delegate.findByUserId(userId);

            // Only cache successful responses (delegate throws on Feign failure)
            cacheMemberships(cacheKey, memberships);
            return memberships;

        } catch (RuntimeException e) {
            // Delegate threw - do NOT cache, propagate error with full stack trace
            log.error("[CachedMembership] Delegate failed for user: {} - NOT caching empty result", userId, e);
            throw e;
        } catch (Exception e) {
            // Redis error only - fall back to delegate
            log.error("[CachedMembership] Cache error for user: {}, falling back to delegate", userId, e);
            return delegate.findByUserId(userId);
        }
    }

    @Override
    public List<MembershipResponse> findActiveByUserId(UUID userId) {
        if (!cacheEnabled || redisTemplate == null) {
            log.debug("[CachedMembership] Cache disabled, using delegate for active memberships of user: {}", userId);
            return delegate.findActiveByUserId(userId);
        }

        try {
            String cacheKey = CACHE_KEY_ACTIVE_PREFIX + userId;
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);

            if (cachedJson != null && !cachedJson.isEmpty()) {
                List<MembershipResponse> cached = deserializeMemberships(cachedJson);
                // Check for stale cache (missing roleIds - indicates old cache format)
                if (!cached.isEmpty() && cached.get(0).getRoleIds() == null) {
                    log.debug("[CachedMembership] Stale cache detected (missing roleIds), refreshing for user: {}", userId);
                    redisTemplate.delete(cacheKey);
                } else {
                    log.debug("[CachedMembership] Cache HIT for active memberships of user: {}", userId);
                    return cached;
                }
            }

            log.debug("[CachedMembership] Cache MISS for active memberships of user: {}", userId);
            List<MembershipResponse> memberships = delegate.findActiveByUserId(userId);

            // Only cache successful responses (delegate throws on Feign failure)
            cacheMemberships(cacheKey, memberships);
            return memberships;

        } catch (RuntimeException e) {
            // Delegate threw - do NOT cache, propagate error with full stack trace
            log.error("[CachedMembership] Delegate failed for active memberships of user: {} - NOT caching empty result", userId, e);
            throw e;
        } catch (Exception e) {
            // Redis error only - fall back to delegate
            log.error("[CachedMembership] Cache error for active memberships of user: {}, falling back to delegate", userId, e);
            return delegate.findActiveByUserId(userId);
        }
    }

    @Override
    public MembershipResponse findById(UUID id) {
        if (!cacheEnabled || redisTemplate == null) {
            log.debug("[CachedMembership] Cache disabled, using delegate for membership: {}", id);
            return delegate.findById(id);
        }

        try {
            String cacheKey = CACHE_KEY_BY_ID_PREFIX + id;
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);

            if (cachedJson != null && !cachedJson.isEmpty()) {
                log.debug("[CachedMembership] Cache HIT for membership: {}", id);
                return objectMapper.readValue(cachedJson, MembershipResponse.class);
            }

            log.debug("[CachedMembership] Cache MISS for membership: {}", id);
            MembershipResponse membership = delegate.findById(id);

            cacheMembership(cacheKey, membership);
            return membership;

        } catch (Exception e) {
            log.error("[CachedMembership] Cache error for membership: {}, falling back to delegate", id, e);
            return delegate.findById(id);
        }
    }

    /**
     * Invalidate all cached memberships for a user
     *
     * Call this when:
     * - User joins/leaves an organization
     * - User's roles change
     * - Tenant info changes
     */
    public void invalidateUserCache(UUID userId) {
        if (!cacheEnabled || redisTemplate == null) {
            log.debug("[CachedMembership] Cache disabled, skipping invalidation for user: {}", userId);
            return;
        }

        try {
            String cacheKey = CACHE_KEY_PREFIX + userId;
            String activeCacheKey = CACHE_KEY_ACTIVE_PREFIX + userId;

            Boolean deleted1 = redisTemplate.delete(cacheKey);
            Boolean deleted2 = redisTemplate.delete(activeCacheKey);

            if (Boolean.TRUE.equals(deleted1) || Boolean.TRUE.equals(deleted2)) {
                log.info("[CachedMembership] Invalidated cache for user: {}", userId);
            }
        } catch (Exception e) {
            log.error("[CachedMembership] Failed to invalidate cache for user: {}", userId, e);
        }
    }

    /**
     * Invalidate cached membership by ID
     */
    public void invalidateMembershipCache(UUID membershipId) {
        if (!cacheEnabled || redisTemplate == null) {
            return;
        }

        try {
            String cacheKey = CACHE_KEY_BY_ID_PREFIX + membershipId;
            redisTemplate.delete(cacheKey);
            log.info("[CachedMembership] Invalidated cache for membership: {}", membershipId);
        } catch (Exception e) {
            log.error("[CachedMembership] Failed to invalidate cache for membership: {}", membershipId, e);
        }
    }

    private void cacheMemberships(String cacheKey, List<MembershipResponse> memberships) {
        try {
            String json = objectMapper.writeValueAsString(memberships);
            redisTemplate.opsForValue().set(cacheKey, json, cacheTtlMinutes, TimeUnit.MINUTES);
            log.debug("[CachedMembership] Cached {} memberships (key: {}, TTL: {} min)",
                    memberships.size(), cacheKey, cacheTtlMinutes);
        } catch (JsonProcessingException e) {
            log.error("[CachedMembership] Failed to serialize memberships for caching", e);
        }
    }

    private void cacheMembership(String cacheKey, MembershipResponse membership) {
        try {
            String json = objectMapper.writeValueAsString(membership);
            redisTemplate.opsForValue().set(cacheKey, json, cacheTtlMinutes, TimeUnit.MINUTES);
            log.debug("[CachedMembership] Cached membership (key: {}, TTL: {} min)", cacheKey, cacheTtlMinutes);
        } catch (JsonProcessingException e) {
            log.error("[CachedMembership] Failed to serialize membership for caching", e);
        }
    }

    private List<MembershipResponse> deserializeMemberships(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<MembershipResponse>>() {});
        } catch (JsonProcessingException e) {
            log.error("[CachedMembership] Failed to deserialize memberships from cache", e);
            return Collections.emptyList();
        }
    }

    /**
     * Check if cache is enabled and available
     */
    public boolean isCacheAvailable() {
        return cacheEnabled && redisTemplate != null;
    }
}
