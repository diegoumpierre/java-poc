package com.poc.auth.repository;

import com.poc.auth.domain.TwoFactorCode;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TwoFactorCodeRepository extends CrudRepository<TwoFactorCode, UUID> {

    @Query("SELECT * FROM AUTH_TWO_FACTOR_CODES WHERE USER_ID = :userId AND CODE = :code AND USED = 0 ORDER BY CREATED_AT DESC LIMIT 1")
    Optional<TwoFactorCode> findValidCode(@Param("userId") UUID userId, @Param("code") String code);

    @Query("SELECT * FROM AUTH_TWO_FACTOR_CODES WHERE USER_ID = :userId AND USED = 0 AND EXPIRES_AT > :now ORDER BY CREATED_AT DESC LIMIT 1")
    Optional<TwoFactorCode> findLatestValidCode(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE AUTH_TWO_FACTOR_CODES SET USED = 1 WHERE USER_ID = :userId AND USED = 0")
    void invalidatePreviousCodes(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM AUTH_TWO_FACTOR_CODES WHERE EXPIRES_AT < :now")
    void deleteExpiredCodes(@Param("now") Instant now);

    @Query("SELECT COUNT(*) FROM AUTH_TWO_FACTOR_CODES WHERE USER_ID = :userId AND CREATED_AT > :since AND USED = 0")
    int countRecentAttempts(@Param("userId") UUID userId, @Param("since") Instant since);
}
