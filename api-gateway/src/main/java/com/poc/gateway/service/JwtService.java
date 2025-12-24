package com.poc.gateway.service;

import com.poc.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtConfig jwtConfig;

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtConfig.getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String getUserIdFromClaims(Claims claims) {
        return claims.getSubject();
    }

    public String getEmailFromClaims(Claims claims) {
        return claims.get("email", String.class);
    }

    public String getRoleFromClaims(Claims claims) {
        return claims.get("role", String.class);
    }

    public String getTenantIdFromClaims(Claims claims) {
        return claims.get("tenantId", String.class);
    }

    public String getMembershipIdFromClaims(Claims claims) {
        return claims.get("membershipId", String.class);
    }

    /**
     * Get tenant ID directly from token string.
     * Used by rate limiter filter before full authentication.
     */
    public String getTenantId(String token) {
        Claims claims = validateToken(token);
        return getTenantIdFromClaims(claims);
    }

    /**
     * Get user ID directly from token string.
     * Used by rate limiter filter before full authentication.
     */
    public String getUserId(String token) {
        Claims claims = validateToken(token);
        return getUserIdFromClaims(claims);
    }
}
