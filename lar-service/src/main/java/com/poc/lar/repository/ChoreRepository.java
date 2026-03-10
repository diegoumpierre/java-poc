package com.poc.lar.repository;

import com.poc.lar.domain.Chore;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChoreRepository extends CrudRepository<Chore, UUID> {

    @Query("SELECT * FROM LAR_CHORES WHERE TENANT_ID = :tenantId AND ACTIVE = 1 ORDER BY NAME")
    List<Chore> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_CHORES WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<Chore> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_CHORES WHERE TENANT_ID = :tenantId AND ASSIGNED_TO = :memberId AND ACTIVE = 1 ORDER BY NAME")
    List<Chore> findByAssignedTo(@Param("tenantId") UUID tenantId, @Param("memberId") UUID memberId);
}
