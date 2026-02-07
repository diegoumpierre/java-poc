package com.poc.tenant.tenant.repository;

import com.poc.tenant.tenant.domain.TenantInvite;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantInviteRepository extends CrudRepository<TenantInvite, UUID> {

    @Query("SELECT * FROM TNT_ACC_INVITES WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<TenantInvite> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_ACC_INVITES WHERE EMAIL = :email AND STATUS = 'PENDING' ORDER BY CREATED_AT DESC")
    List<TenantInvite> findPendingByEmail(@Param("email") String email);

    Optional<TenantInvite> findByCode(String code);

    @Query("SELECT * FROM TNT_ACC_INVITES WHERE TENANT_ID = :tenantId AND EMAIL = :email AND STATUS = 'PENDING'")
    Optional<TenantInvite> findPendingByTenantIdAndEmail(@Param("tenantId") UUID tenantId, @Param("email") String email);

    @Query("SELECT * FROM TNT_ACC_INVITES WHERE STATUS = 'PENDING' AND EXPIRES_AT < NOW()")
    List<TenantInvite> findExpiredInvites();
}
