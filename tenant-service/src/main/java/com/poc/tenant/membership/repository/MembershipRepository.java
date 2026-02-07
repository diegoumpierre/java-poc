package com.poc.tenant.membership.repository;

import com.poc.tenant.membership.domain.Membership;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipRepository extends CrudRepository<Membership, UUID> {

    List<Membership> findByUserId(UUID userId);

    List<Membership> findByTenantId(UUID tenantId);

    Optional<Membership> findByUserIdAndTenantId(UUID userId, UUID tenantId);

    boolean existsByUserIdAndTenantId(UUID userId, UUID tenantId);

    @Query("SELECT * FROM TNT_ACC_MEMBERSHIPS WHERE USER_ID = :userId AND UPPER(STATUS) = 'ACTIVE' AND DELETED_AT IS NULL")
    List<Membership> findActiveByUserId(@Param("userId") UUID userId);

    @Query("SELECT * FROM TNT_ACC_MEMBERSHIPS WHERE TENANT_ID = :tenantId AND UPPER(STATUS) = 'ACTIVE' AND DELETED_AT IS NULL")
    List<Membership> findActiveByTenantId(@Param("tenantId") UUID tenantId);

    @Modifying
    @Query("UPDATE TNT_ACC_MEMBERSHIPS SET STATUS = :status WHERE ID = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") String status);
}
