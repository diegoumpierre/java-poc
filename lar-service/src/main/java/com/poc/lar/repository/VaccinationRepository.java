package com.poc.lar.repository;

import com.poc.lar.domain.Vaccination;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VaccinationRepository extends CrudRepository<Vaccination, UUID> {

    @Query("SELECT * FROM LAR_VACCINATIONS WHERE TENANT_ID = :tenantId AND MEMBER_ID = :memberId ORDER BY DATE_ADMINISTERED DESC")
    List<Vaccination> findByMemberId(@Param("tenantId") UUID tenantId, @Param("memberId") UUID memberId);

    @Query("SELECT * FROM LAR_VACCINATIONS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<Vaccination> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);
}
