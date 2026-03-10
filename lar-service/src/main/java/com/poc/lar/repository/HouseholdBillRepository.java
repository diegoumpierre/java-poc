package com.poc.lar.repository;

import com.poc.lar.domain.HouseholdBill;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HouseholdBillRepository extends CrudRepository<HouseholdBill, UUID> {

    @Query("SELECT * FROM LAR_HOUSEHOLD_BILLS WHERE TENANT_ID = :tenantId AND ACTIVE = 1 ORDER BY NAME")
    List<HouseholdBill> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_HOUSEHOLD_BILLS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<HouseholdBill> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);
}
