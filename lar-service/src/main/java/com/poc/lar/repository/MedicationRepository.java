package com.poc.lar.repository;

import com.poc.lar.domain.Medication;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicationRepository extends CrudRepository<Medication, UUID> {

    @Query("SELECT * FROM LAR_MEDICATIONS WHERE TENANT_ID = :tenantId AND MEMBER_ID = :memberId ORDER BY NAME")
    List<Medication> findByMemberId(@Param("tenantId") UUID tenantId, @Param("memberId") UUID memberId);

    @Query("SELECT * FROM LAR_MEDICATIONS WHERE TENANT_ID = :tenantId AND ACTIVE = 1 ORDER BY NAME")
    List<Medication> findActive(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_MEDICATIONS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<Medication> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);
}
