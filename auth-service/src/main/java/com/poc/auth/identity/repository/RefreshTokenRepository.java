package com.poc.auth.repository;

import com.poc.auth.domain.RefreshToken;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Refresh token repository using Spring Data JDBC
 */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {

    /**
     * Find refresh token by token string
     */
    @Query("SELECT * FROM AUTH_REFRESH_TOKENS WHERE token = :token")
    Optional<RefreshToken> findByToken(@Param("token") String token);

    /**
     * Delete all refresh tokens for a user
     */
    @Modifying
    @Query("DELETE FROM AUTH_REFRESH_TOKENS WHERE user_id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    /**
     * Delete refresh token by token string
     */
    @Modifying
    @Query("DELETE FROM AUTH_REFRESH_TOKENS WHERE token = :token")
    void deleteByToken(@Param("token") String token);
}
