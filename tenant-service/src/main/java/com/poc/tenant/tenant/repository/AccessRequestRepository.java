package com.poc.tenant.tenant.repository;

import com.poc.tenant.tenant.domain.AccessRequest;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccessRequestRepository extends CrudRepository<AccessRequest, UUID> {

    @Query("SELECT * FROM TNT_ACC_ACCESS_REQUESTS WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<AccessRequest> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_ACC_ACCESS_REQUESTS WHERE USER_ID = :userId ORDER BY CREATED_AT DESC")
    List<AccessRequest> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT * FROM TNT_ACC_ACCESS_REQUESTS WHERE TENANT_ID = :tenantId AND STATUS = 'PENDING' ORDER BY CREATED_AT DESC")
    List<AccessRequest> findPendingByTenantId(@Param("tenantId") UUID tenantId);

    Optional<AccessRequest> findByUserIdAndTenantId(UUID userId, UUID tenantId);

    boolean existsByUserIdAndTenantId(UUID userId, UUID tenantId);
}
