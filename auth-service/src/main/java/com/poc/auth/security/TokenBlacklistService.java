package com.poc.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for managing JWT token blacklist using Redis.
 * Tokens are added to the blacklist on logout and checked during validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Add a token to the blacklist.
     * The token will be stored until its original expiration time.
     *
     * @param tokenId The JWT token ID (jti claim)
     * @param remainingExpirationMs Remaining time until token expiration in milliseconds
     */
    public void blacklistToken(String tokenId, long remainingExpirationMs) {
        if (tokenId == null || tokenId.isEmpty()) {
            log.warn("Attempted to blacklist null or empty token ID");
            return;
        }

        String key = BLACKLIST_PREFIX + tokenId;

        // Only store if there's remaining time
        if (remainingExpirationMs > 0) {
            redisTemplate.opsForValue().set(key, "revoked", remainingExpirationMs, TimeUnit.MILLISECONDS);
            log.debug("Token blacklisted: {} (expires in {} ms)", tokenId, remainingExpirationMs);
        } else {
            log.debug("Token already expired, not blacklisting: {}", tokenId);
        }
    }

    /**
     * Check if a token is blacklisted.
     *
     * @param tokenId The JWT token ID (jti claim)
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return false;
        }

        String key = BLACKLIST_PREFIX + tokenId;
        Boolean exists = redisTemplate.hasKey(key);

        if (Boolean.TRUE.equals(exists)) {
            log.debug("Token is blacklisted: {}", tokenId);
            return true;
        }

        return false;
    }

    /**
     * Remove a token from the blacklist (used for testing or admin operations).
     *
     * @param tokenId The JWT token ID (jti claim)
     */
    public void removeFromBlacklist(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return;
        }

        String key = BLACKLIST_PREFIX + tokenId;
        redisTemplate.delete(key);
        log.debug("Token removed from blacklist: {}", tokenId);
    }
}
