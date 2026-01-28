package com.poc.auth.service;

import com.poc.auth.profile.service.CachedAvatarUrlService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CachedAvatarUrlService
 * Tests caching behavior for presigned URLs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CachedAvatarUrlService Unit Tests")
class CachedAvatarUrlServiceTest {

    @Mock
    private AvatarUrlService avatarUrlService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private CachedAvatarUrlService cachedAvatarUrlService;

    private static final String USER_ID = "user-123";
    private static final String AVATAR_PATH = "avatars/user-123/avatar.png";
    private static final String PRESIGNED_URL = "http://minio/auth-files/avatars/user-123/avatar.png?X-Amz-Signature=abc123&X-Amz-Expires=1800";
    private static final String CACHE_KEY = "avatar:presigned:user-123";
    private static final int CACHE_TTL = 30;

    @BeforeEach
    void setUp() {
        cachedAvatarUrlService = new CachedAvatarUrlService(avatarUrlService, redisTemplate);

        // Set properties via reflection
        ReflectionTestUtils.setField(cachedAvatarUrlService, "cacheTtlMinutes", CACHE_TTL);
        ReflectionTestUtils.setField(cachedAvatarUrlService, "cacheEnabled", true);
    }

    @Test
    @DisplayName("Should return cached URL on cache hit")
    void shouldReturnCachedUrlOnCacheHit() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY)).thenReturn(PRESIGNED_URL);

        // When
        String result = cachedAvatarUrlService.getAvatarUrl(USER_ID, AVATAR_PATH);

        // Then
        assertThat(result).isEqualTo(PRESIGNED_URL);
        verify(valueOperations).get(CACHE_KEY);
        verifyNoInteractions(avatarUrlService); // Should NOT call avatar service on cache hit
    }

    @Test
    @DisplayName("Should generate and cache URL on cache miss")
    void shouldGenerateAndCacheUrlOnCacheMiss() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY)).thenReturn(null); // Cache miss
        when(avatarUrlService.getAvatarUrl(AVATAR_PATH)).thenReturn(PRESIGNED_URL);

        // When
        String result = cachedAvatarUrlService.getAvatarUrl(USER_ID, AVATAR_PATH);

        // Then
        assertThat(result).isEqualTo(PRESIGNED_URL);
        verify(valueOperations).get(CACHE_KEY);
        verify(avatarUrlService).getAvatarUrl(AVATAR_PATH);
        verify(valueOperations).set(CACHE_KEY, PRESIGNED_URL, 30L, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("Should return null for null avatar path")
    void shouldReturnNullForNullAvatarPath() {
        // When
        String result = cachedAvatarUrlService.getAvatarUrl(USER_ID, null);

        // Then
        assertThat(result).isNull();
        verifyNoInteractions(valueOperations, avatarUrlService);
    }

    @Test
    @DisplayName("Should return null for empty avatar path")
    void shouldReturnNullForEmptyAvatarPath() {
        // When
        String result = cachedAvatarUrlService.getAvatarUrl(USER_ID, "");

        // Then
        assertThat(result).isNull();
        verifyNoInteractions(valueOperations, avatarUrlService);
    }

    @Test
    @DisplayName("Should return null for blank avatar path")
    void shouldReturnNullForBlankAvatarPath() {
        // When
        String result = cachedAvatarUrlService.getAvatarUrl(USER_ID, "   ");

        // Then
        assertThat(result).isNull();
        verifyNoInteractions(valueOperations, avatarUrlService);
    }

    @Test
    @DisplayName("Should use direct service when cache is disabled")
    void shouldUseDirectServiceWhenCacheDisabled() {
        // Given
        ReflectionTestUtils.setField(cachedAvatarUrlService, "cacheEnabled", false);
        when(avatarUrlService.getAvatarUrl(AVATAR_PATH)).thenReturn(PRESIGNED_URL);

        // When
        String result = cachedAvatarUrlService.getAvatarUrl(USER_ID, AVATAR_PATH);

        // Then
        assertThat(result).isEqualTo(PRESIGNED_URL);
        verify(avatarUrlService).getAvatarUrl(AVATAR_PATH);
        verifyNoInteractions(valueOperations);
    }

    @Test
    @DisplayName("Should use direct service when Redis template is null")
    void shouldUseDirectServiceWhenRedisTemplateNull() {
        // Given
        cachedAvatarUrlService = new CachedAvatarUrlService(avatarUrlService, null);
        ReflectionTestUtils.setField(cachedAvatarUrlService, "cacheEnabled", true);
        when(avatarUrlService.getAvatarUrl(AVATAR_PATH)).thenReturn(PRESIGNED_URL);

        // When
        String result = cachedAvatarUrlService.getAvatarUrl(USER_ID, AVATAR_PATH);

        // Then
        assertThat(result).isEqualTo(PRESIGNED_URL);
        verify(avatarUrlService).getAvatarUrl(AVATAR_PATH);
    }

    @Test
    @DisplayName("Should NOT cache local paths")
    void shouldNotCacheLocalPaths() {
        // Given
        String localPath = "/api/profile/avatar/local.png";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);
        when(avatarUrlService.getAvatarUrl(AVATAR_PATH)).thenReturn(localPath);

        // When
        String result = cachedAvatarUrlService.getAvatarUrl(USER_ID, AVATAR_PATH);

        // Then
        assertThat(result).isEqualTo(localPath);
        verify(valueOperations).get(CACHE_KEY);
        verify(avatarUrlService).getAvatarUrl(AVATAR_PATH);
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("Should invalidate cache successfully")
    void shouldInvalidateCacheSuccessfully() {
        // Given
        when(redisTemplate.delete(CACHE_KEY)).thenReturn(true);

        // When
        cachedAvatarUrlService.invalidateCache(USER_ID);

        // Then
        verify(redisTemplate).delete(CACHE_KEY);
    }

    @Test
    @DisplayName("Should handle cache invalidation when entry does not exist")
    void shouldHandleCacheInvalidationWhenEntryNotExists() {
        // Given
        when(redisTemplate.delete(CACHE_KEY)).thenReturn(false);

        // When
        cachedAvatarUrlService.invalidateCache(USER_ID);

        // Then
        verify(redisTemplate).delete(CACHE_KEY);
    }

    @Test
    @DisplayName("Should skip invalidation when cache is disabled")
    void shouldSkipInvalidationWhenCacheDisabled() {
        // Given
        ReflectionTestUtils.setField(cachedAvatarUrlService, "cacheEnabled", false);

        // When
        cachedAvatarUrlService.invalidateCache(USER_ID);

        // Then
        verifyNoInteractions(redisTemplate);
    }

    @Test
    @DisplayName("Should fallback to direct service on cache errors")
    void shouldFallbackToDirectServiceOnCacheErrors() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY)).thenThrow(new RuntimeException("Redis connection error"));
        when(avatarUrlService.getAvatarUrl(AVATAR_PATH)).thenReturn(PRESIGNED_URL);

        // When
        String result = cachedAvatarUrlService.getAvatarUrl(USER_ID, AVATAR_PATH);

        // Then
        assertThat(result).isEqualTo(PRESIGNED_URL);
        verify(avatarUrlService).getAvatarUrl(AVATAR_PATH);
    }

    @Test
    @DisplayName("Should check cache availability correctly")
    void shouldCheckCacheAvailability() {
        // When cache is enabled and Redis is available
        ReflectionTestUtils.setField(cachedAvatarUrlService, "cacheEnabled", true);
        assertThat(cachedAvatarUrlService.isCacheAvailable()).isTrue();

        // When cache is disabled
        ReflectionTestUtils.setField(cachedAvatarUrlService, "cacheEnabled", false);
        assertThat(cachedAvatarUrlService.isCacheAvailable()).isFalse();

        // When Redis template is null
        cachedAvatarUrlService = new CachedAvatarUrlService(avatarUrlService, null);
        ReflectionTestUtils.setField(cachedAvatarUrlService, "cacheEnabled", true);
        assertThat(cachedAvatarUrlService.isCacheAvailable()).isFalse();
    }

    @Test
    @DisplayName("Should get cache stats successfully")
    void shouldGetCacheStatsSuccessfully() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY)).thenReturn(PRESIGNED_URL);
        when(redisTemplate.getExpire(CACHE_KEY, TimeUnit.SECONDS)).thenReturn(1500L);

        // When
        CachedAvatarUrlService.CacheStats stats = cachedAvatarUrlService.getCacheStats(USER_ID);

        // Then
        assertThat(stats.cacheEnabled()).isTrue();
        assertThat(stats.hasValue()).isTrue();
        assertThat(stats.ttlSeconds()).isEqualTo(1500L);
    }

    @Test
    @DisplayName("Should handle cache stats when entry not exists")
    void shouldHandleCacheStatsWhenEntryNotExists() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);

        // When
        CachedAvatarUrlService.CacheStats stats = cachedAvatarUrlService.getCacheStats(USER_ID);

        // Then
        assertThat(stats.cacheEnabled()).isTrue();
        assertThat(stats.hasValue()).isFalse();
    }

    @Test
    @DisplayName("Should handle cache stats when cache is disabled")
    void shouldHandleCacheStatsWhenCacheDisabled() {
        // Given
        ReflectionTestUtils.setField(cachedAvatarUrlService, "cacheEnabled", false);

        // When
        CachedAvatarUrlService.CacheStats stats = cachedAvatarUrlService.getCacheStats(USER_ID);

        // Then
        assertThat(stats.cacheEnabled()).isFalse();
        assertThat(stats.hasValue()).isNull();
        assertThat(stats.ttlSeconds()).isNull();
    }

    @Test
    @DisplayName("Should cache with correct TTL")
    void shouldCacheWithCorrectTtl() {
        // Given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);
        when(avatarUrlService.getAvatarUrl(AVATAR_PATH)).thenReturn(PRESIGNED_URL);

        // When
        cachedAvatarUrlService.getAvatarUrl(USER_ID, AVATAR_PATH);

        // Then
        verify(valueOperations).set(
            eq(CACHE_KEY),
            eq(PRESIGNED_URL),
            eq(30L),
            eq(TimeUnit.MINUTES)
        );
    }

    @Test
    @DisplayName("Should handle concurrent cache access gracefully")
    void shouldHandleConcurrentCacheAccessGracefully() {
        // Given - simulate race condition where cache is populated between get and set
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY))
            .thenReturn(null)      // First call: cache miss
            .thenReturn(PRESIGNED_URL); // Second call: cache hit
        when(avatarUrlService.getAvatarUrl(AVATAR_PATH)).thenReturn(PRESIGNED_URL);

        // When - call twice
        String result1 = cachedAvatarUrlService.getAvatarUrl(USER_ID, AVATAR_PATH);
        String result2 = cachedAvatarUrlService.getAvatarUrl(USER_ID, AVATAR_PATH);

        // Then
        assertThat(result1).isEqualTo(PRESIGNED_URL);
        assertThat(result2).isEqualTo(PRESIGNED_URL);
        verify(avatarUrlService, times(1)).getAvatarUrl(AVATAR_PATH); // Called only once
    }
}
