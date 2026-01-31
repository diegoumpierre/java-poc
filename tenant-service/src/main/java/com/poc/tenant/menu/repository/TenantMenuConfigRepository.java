package com.poc.tenant.menu.repository;

import com.poc.tenant.menu.domain.TenantMenuConfig;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantMenuConfigRepository extends CrudRepository<TenantMenuConfig, UUID> {

    @Query("SELECT * FROM TNT_MENU_CONFIG WHERE TENANT_ID = :tenantId")
    List<TenantMenuConfig> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_MENU_CONFIG WHERE TENANT_ID = :tenantId AND MENU_ID = :menuId")
    Optional<TenantMenuConfig> findByTenantIdAndMenuId(@Param("tenantId") UUID tenantId, @Param("menuId") String menuId);

    @Query("SELECT * FROM TNT_MENU_CONFIG WHERE TENANT_ID = :tenantId AND ENABLED = 0")
    List<TenantMenuConfig> findDisabledByTenantId(@Param("tenantId") UUID tenantId);
}
