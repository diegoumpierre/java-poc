package com.poc.tenant.tenant.repository;

import com.poc.tenant.tenant.domain.Tenant;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends CrudRepository<Tenant, UUID> {

    Optional<Tenant> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT * FROM TNT_CORE_TENANTS WHERE DELETED_AT IS NULL ORDER BY NAME")
    List<Tenant> findAllActive();

    @Query("SELECT * FROM TNT_CORE_TENANTS WHERE DELETED_AT IS NULL AND NAME LIKE CONCAT('%', :query, '%') ORDER BY NAME")
    List<Tenant> searchByName(@Param("query") String query);

    @Query("SELECT * FROM TNT_CORE_TENANTS WHERE SUBSCRIPTION_STATUS = 'TRIAL' AND TRIAL_ENDS_AT < NOW() AND DELETED_AT IS NULL")
    List<Tenant> findExpiredTrials();

    @Query("SELECT COUNT(*) FROM TNT_CORE_TENANTS WHERE SUBSCRIPTION_STATUS = 'TRIAL' AND TRIAL_ENDS_AT > NOW() AND DELETED_AT IS NULL")
    long countActiveTrials();

    @Query("SELECT * FROM TNT_CORE_TENANTS WHERE SUBSCRIPTION_STATUS = 'TRIAL' AND TRIAL_ENDS_AT > NOW() AND DELETED_AT IS NULL ORDER BY TRIAL_ENDS_AT ASC")
    List<Tenant> findActiveTrials();

    @Query("SELECT * FROM TNT_CORE_TENANTS WHERE TENANT_TYPE = 'PLATFORM' AND DELETED_AT IS NULL LIMIT 1")
    Optional<Tenant> findPlatformTenant();

    @Query("SELECT * FROM TNT_CORE_TENANTS WHERE PARENT_TENANT_ID = :parentId AND DELETED_AT IS NULL ORDER BY NAME")
    List<Tenant> findByParentTenantId(@Param("parentId") UUID parentId);

    @Query("SELECT * FROM TNT_CORE_TENANTS WHERE TENANT_TYPE = :tenantType AND DELETED_AT IS NULL ORDER BY NAME")
    List<Tenant> findByTenantType(@Param("tenantType") String tenantType);

    @Query("SELECT * FROM TNT_CORE_TENANTS WHERE STATUS = :status AND DELETED_AT IS NULL ORDER BY NAME")
    List<Tenant> findByStatus(@Param("status") String status);

    @Query("SELECT COUNT(*) FROM TNT_CORE_TENANTS WHERE PARENT_TENANT_ID = :parentId AND DELETED_AT IS NULL")
    long countByParentTenantId(@Param("parentId") UUID parentId);

    // Hierarchy stats queries
    @Query("SELECT COUNT(*) FROM TNT_CORE_TENANTS WHERE DELETED_AT IS NULL")
    long countTotal();

    @Query("SELECT COUNT(*) FROM TNT_CORE_TENANTS WHERE TENANT_TYPE = 'RESELLER' AND DELETED_AT IS NULL")
    long countPartners();

    @Query("SELECT COUNT(*) FROM TNT_CORE_TENANTS WHERE TENANT_TYPE = 'CLIENT' AND DELETED_AT IS NULL")
    long countClients();

    @Query("SELECT COUNT(*) FROM TNT_CORE_TENANTS WHERE TENANT_TYPE = 'RESELLER' AND STATUS = 'ACTIVE' AND DELETED_AT IS NULL")
    long countActivePartners();

    @Query("SELECT COUNT(*) FROM TNT_CORE_TENANTS WHERE TENANT_TYPE = 'CLIENT' AND STATUS = 'ACTIVE' AND DELETED_AT IS NULL")
    long countActiveClients();

    @Query("SELECT COUNT(*) FROM TNT_CORE_TENANTS WHERE STATUS = 'SUSPENDED' AND DELETED_AT IS NULL")
    long countSuspended();

    @Query("SELECT COUNT(*) FROM TNT_CORE_TENANTS WHERE SUBSCRIPTION_STATUS = 'TRIAL' AND DELETED_AT IS NULL")
    long countOnTrial();

    @Query("SELECT * FROM TNT_CORE_TENANTS WHERE ID IN (:ids) AND DELETED_AT IS NULL")
    List<Tenant> findAllByIdIn(@Param("ids") List<UUID> ids);
}
