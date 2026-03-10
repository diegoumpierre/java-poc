package com.poc.lar.repository;

import com.poc.lar.domain.OutingRequest;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OutingRequestRepository extends CrudRepository<OutingRequest, UUID> {

    @Query("SELECT * FROM LAR_OUTING_REQUESTS WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<OutingRequest> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_OUTING_REQUESTS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<OutingRequest> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_OUTING_REQUESTS WHERE TENANT_ID = :tenantId AND STATUS = :status ORDER BY CREATED_AT DESC")
    List<OutingRequest> findByStatus(@Param("tenantId") UUID tenantId, @Param("status") String status);

    @Query("SELECT * FROM LAR_OUTING_REQUESTS WHERE TENANT_ID = :tenantId AND MEMBER_ID = :memberId ORDER BY CREATED_AT DESC")
    List<OutingRequest> findByMemberId(@Param("tenantId") UUID tenantId, @Param("memberId") UUID memberId);

    @Query("SELECT * FROM LAR_OUTING_REQUESTS WHERE TENANT_ID = :tenantId AND STATUS = 'PENDING' ORDER BY CREATED_AT DESC")
    List<OutingRequest> findPending(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_OUTING_REQUESTS WHERE TENANT_ID = :tenantId AND STATUS = 'DEPARTED' ORDER BY ACTUAL_DEPARTURE DESC")
    List<OutingRequest> findActive(@Param("tenantId") UUID tenantId);
}
