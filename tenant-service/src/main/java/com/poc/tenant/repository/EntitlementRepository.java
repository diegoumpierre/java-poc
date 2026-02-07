package com.poc.tenant.repository;

import com.poc.tenant.domain.Entitlement;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntitlementRepository extends CrudRepository<Entitlement, UUID> {

    @Query("SELECT * FROM TNT_CORE_ENTITLEMENTS WHERE TENANT_ID = :tenantId AND ENABLED = true")
    List<Entitlement> findActiveByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_CORE_ENTITLEMENTS WHERE TENANT_ID = :tenantId AND PRODUCT_ID = :productId AND ENABLED = true")
    List<Entitlement> findActiveByTenantIdAndProductId(@Param("tenantId") UUID tenantId, @Param("productId") UUID productId);

    @Query("SELECT * FROM TNT_CORE_ENTITLEMENTS WHERE TENANT_ID = :tenantId AND FEATURE_CODE = :featureCode AND ENABLED = true")
    Optional<Entitlement> findByTenantIdAndFeatureCode(@Param("tenantId") UUID tenantId, @Param("featureCode") String featureCode);

    @Modifying
    @Query("UPDATE TNT_CORE_ENTITLEMENTS SET ENABLED = :enabled, UPDATED_AT = CURRENT_TIMESTAMP(6) WHERE ID = :id")
    void updateEnabled(@Param("id") UUID id, @Param("enabled") boolean enabled);

    @Modifying
    @Query("DELETE FROM TNT_CORE_ENTITLEMENTS WHERE TENANT_ID = :tenantId AND SOURCE = :source")
    void deleteByTenantIdAndSource(@Param("tenantId") UUID tenantId, @Param("source") String source);

    @Query("SELECT COUNT(*) > 0 FROM TNT_CORE_ENTITLEMENTS WHERE TENANT_ID = :tenantId AND FEATURE_CODE = :featureCode AND ENABLED = true AND (EXPIRES_AT IS NULL OR EXPIRES_AT > CURRENT_TIMESTAMP(6))")
    boolean hasActiveEntitlement(@Param("tenantId") UUID tenantId, @Param("featureCode") String featureCode);
}
