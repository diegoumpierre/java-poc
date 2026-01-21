package com.poc.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Support for key rotation - old secret for validation only
    @Value("${jwt.secret-old:}")
    private String jwtSecretOld;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getOldSigningKey() {
        if (jwtSecretOld == null || jwtSecretOld.isEmpty()) {
            return null;
        }
        byte[] keyBytes = jwtSecretOld.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return generateTokenFromUserDetails(userDetails);
    }

    public String generateTokenFromUserId(UUID userId) {
        // This method is kept for backward compatibility but should include user details
        // For now, it only includes userId - use generateTokenFromUserDetails when possible
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // JTI for blacklisting
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateTokenFromUserDetails(CustomUserDetails userDetails) {
        return generateTokenFromUserDetails(userDetails, false);
    }

    public String generateTokenFromUserDetails(CustomUserDetails userDetails, boolean rememberMe) {
        Date now = new Date();
        // If rememberMe is true, use 30 days; otherwise use default expiration
        long expirationMs = rememberMe ? (30L * 24 * 60 * 60 * 1000) : jwtExpirationMs;
        Date expiryDate = new Date(now.getTime() + expirationMs);

        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString()) // JTI for blacklisting
                .subject(userDetails.getId().toString())
                .claim("email", userDetails.getEmail());

        // Add multi-tenancy claims if present
        if (userDetails.getTenantId() != null) {
            builder.claim("tenantId", userDetails.getTenantId().toString());
        }
        if (userDetails.getMembershipId() != null) {
            builder.claim("membershipId", userDetails.getMembershipId().toString());
        }

        return builder
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Get expiration in milliseconds based on rememberMe flag
     */
    public long getExpirationMs(boolean rememberMe) {
        return rememberMe ? (30L * 24 * 60 * 60 * 1000) : jwtExpirationMs;
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UUID.fromString(claims.getSubject());
    }

    public boolean validateToken(String token) {
        // Try with current secret first
        if (validateTokenWithKey(token, getSigningKey())) {
            return true;
        }

        // If rotation is configured, try with old secret
        SecretKey oldKey = getOldSigningKey();
        if (oldKey != null) {
            log.debug("Trying validation with old secret key");
            return validateTokenWithKey(token, oldKey);
        }

        return false;
    }

    private boolean validateTokenWithKey(String token, SecretKey key) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    /**
     * Extract the token ID (jti claim) from a JWT token
     */
    public String getTokenIdFromToken(String token) {
        Claims claims = parseClaimsWithRotation(token);
        return claims != null ? claims.getId() : null;
    }

    /**
     * Get remaining expiration time in milliseconds
     */
    public long getRemainingExpirationMs(String token) {
        Claims claims = parseClaimsWithRotation(token);
        if (claims != null && claims.getExpiration() != null) {
            long expirationTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();
            return Math.max(0, expirationTime - currentTime);
        }
        return 0;
    }

    /**
     * Parse claims with key rotation support
     */
    private Claims parseClaimsWithRotation(String token) {
        // Try with current secret first
        Claims claims = parseClaimsWithKey(token, getSigningKey());
        if (claims != null) {
            return claims;
        }

        // If rotation is configured, try with old secret
        SecretKey oldKey = getOldSigningKey();
        if (oldKey != null) {
            return parseClaimsWithKey(token, oldKey);
        }

        return null;
    }

    private Claims parseClaimsWithKey(String token, SecretKey key) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            return null;
        }
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    /**
     * Generate a refresh token with longer expiration
     */
    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // JTI for blacklisting
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generate a refresh token from authentication
     */
    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return generateRefreshToken(userDetails.getId());
    }

    /**
     * Validate if token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception ex) {
            log.error("Error checking token type: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Generate a temporary token for 2FA verification (short-lived, 5 minutes)
     */
    public String generateTempToken(UUID userId, UUID membershipId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (5 * 60 * 1000)); // 5 minutes

        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.toString())
                .claim("type", "2fa_temp");

        if (membershipId != null) {
            builder.claim("membershipId", membershipId.toString());
        }

        return builder
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validate if token is a 2FA temporary token
     */
    public boolean is2FATempToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return "2fa_temp".equals(claims.get("type", String.class));
        } catch (Exception ex) {
            log.error("Error checking token type: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Get tenant ID from token
     */
    public UUID getTenantIdFromToken(String token) {
        Claims claims = parseClaimsWithRotation(token);
        if (claims != null) {
            String tenantId = claims.get("tenantId", String.class);
            return tenantId != null ? UUID.fromString(tenantId) : null;
        }
        return null;
    }

    /**
     * Get membership ID from token
     */
    public UUID getMembershipIdFromToken(String token) {
        Claims claims = parseClaimsWithRotation(token);
        if (claims != null) {
            String membershipId = claims.get("membershipId", String.class);
            return membershipId != null ? UUID.fromString(membershipId) : null;
        }
        return null;
    }
}
