package com.poc.lar.repository;

import com.poc.lar.domain.EmergencyCard;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmergencyCardRepository extends CrudRepository<EmergencyCard, UUID> {

    @Query("SELECT * FROM LAR_EMERGENCY_CARDS WHERE MEMBER_ID = :memberId AND TENANT_ID = :tenantId")
    Optional<EmergencyCard> findByMemberIdAndTenantId(@Param("memberId") UUID memberId, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_EMERGENCY_CARDS WHERE PUBLIC_TOKEN = :token AND ACTIVE = 1")
    Optional<EmergencyCard> findByPublicToken(@Param("token") UUID token);

    @Query("SELECT * FROM LAR_EMERGENCY_CARDS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<EmergencyCard> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);
}
