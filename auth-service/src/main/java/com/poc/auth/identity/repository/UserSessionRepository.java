package com.poc.auth.repository;

import com.poc.auth.domain.UserSession;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends CrudRepository<UserSession, UUID> {

    @Query("SELECT * FROM AUTH_USER_SESSIONS WHERE USER_ID = :userId AND REVOKED = 0 ORDER BY CREATED_AT DESC")
    List<UserSession> findActiveByUserId(@Param("userId") UUID userId);

    @Query("SELECT * FROM AUTH_USER_SESSIONS WHERE TOKEN_ID = :tokenId")
    Optional<UserSession> findByTokenId(@Param("tokenId") String tokenId);

    @Query("SELECT COUNT(*) FROM AUTH_USER_SESSIONS WHERE USER_ID = :userId AND REVOKED = 0 AND EXPIRES_AT > :now")
    int countActiveSessions(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE AUTH_USER_SESSIONS SET REVOKED = 1, REVOKED_AT = :now, REVOKED_REASON = :reason WHERE USER_ID = :userId AND REVOKED = 0")
    void revokeAllUserSessions(@Param("userId") UUID userId, @Param("now") Instant now, @Param("reason") String reason);

    @Modifying
    @Query("UPDATE AUTH_USER_SESSIONS SET REVOKED = 1, REVOKED_AT = :now, REVOKED_REASON = :reason WHERE ID = :sessionId")
    void revokeSession(@Param("sessionId") UUID sessionId, @Param("now") Instant now, @Param("reason") String reason);

    @Modifying
    @Query("UPDATE AUTH_USER_SESSIONS SET REVOKED = 1, REVOKED_AT = :now, REVOKED_REASON = :reason WHERE TOKEN_ID = :tokenId")
    void revokeByTokenId(@Param("tokenId") String tokenId, @Param("now") Instant now, @Param("reason") String reason);

    @Modifying
    @Query("UPDATE AUTH_USER_SESSIONS SET LAST_ACTIVITY_AT = :now WHERE TOKEN_ID = :tokenId")
    void updateLastActivity(@Param("tokenId") String tokenId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE AUTH_USER_SESSIONS SET IS_CURRENT = 0 WHERE USER_ID = :userId")
    void clearCurrentFlag(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM AUTH_USER_SESSIONS WHERE EXPIRES_AT < :now AND REVOKED = 1")
    void deleteExpiredAndRevoked(@Param("now") Instant now);

    @Query("""
        SELECT * FROM AUTH_USER_SESSIONS
        WHERE USER_ID = :userId AND REVOKED = 0 AND EXPIRES_AT > :now
        ORDER BY CREATED_AT ASC
        LIMIT :limit
        """)
    List<UserSession> findOldestActiveSessions(@Param("userId") UUID userId, @Param("now") Instant now, @Param("limit") int limit);

    @Modifying
    @Query("UPDATE AUTH_USER_SESSIONS SET REVOKED = 1, REVOKED_AT = :now, REVOKED_REASON = :reason WHERE REVOKED = 0")
    void revokeAllSessions(@Param("now") Instant now, @Param("reason") String reason);

    @Query("SELECT COUNT(*) FROM AUTH_USER_SESSIONS WHERE REVOKED = 0")
    int countAllActiveSessions();
}
