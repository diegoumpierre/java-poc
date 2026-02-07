package com.poc.tenant.repository;

import com.poc.tenant.domain.Supplier;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends CrudRepository<Supplier, UUID> {

    @Query("SELECT * FROM TNT_CORE_SUPPLIERS WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<Supplier> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_CORE_SUPPLIERS WHERE TENANT_ID = :tenantId AND ACTIVE = 1 ORDER BY CREATED_AT DESC")
    List<Supplier> findByTenantIdAndActiveTrue(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_CORE_SUPPLIERS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<Supplier> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT COUNT(*) FROM TNT_CORE_SUPPLIERS WHERE TENANT_ID = :tenantId")
    long countByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_CORE_SUPPLIERS WHERE TENANT_ID = :tenantId AND (LOWER(NAME) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(EMAIL) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(DOCUMENT) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY NAME")
    List<Supplier> searchByTenantId(@Param("tenantId") UUID tenantId, @Param("query") String query);

    @Query("SELECT * FROM TNT_CORE_SUPPLIERS WHERE CATEGORY = :category AND TENANT_ID = :tenantId AND ACTIVE = 1 ORDER BY NAME")
    List<Supplier> findByCategoryAndTenantId(@Param("category") String category, @Param("tenantId") UUID tenantId);
}
