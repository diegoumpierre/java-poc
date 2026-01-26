package com.poc.auth.repository;

import com.poc.auth.domain.VerificationCode;
import com.poc.auth.domain.enums.VerificationType;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRepositoryVerificationCode extends CrudRepository<VerificationCode, UUID> {

    @Query("SELECT * FROM AUTH_VERIFICATION_CODES WHERE LOWER(email) = LOWER(:email) AND code = :code AND type = :type AND used = false LIMIT 1")
    Optional<VerificationCode> findByEmailIgnoreCaseAndCodeAndTypeAndUsedFalse(
            @Param("email") String email, @Param("code") String code, @Param("type") VerificationType type);

    @Modifying
    @Query("UPDATE AUTH_VERIFICATION_CODES SET used = true WHERE LOWER(email) = LOWER(:email) AND type = :type AND used = false")
    void invalidatePreviousCodes(@Param("email") String email, @Param("type") VerificationType type);

    @Modifying
    @Query("DELETE FROM AUTH_VERIFICATION_CODES WHERE expires_at < :now")
    void deleteExpiredCodes(@Param("now") Instant now);
}
