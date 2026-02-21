package com.poc.notification.repository;

import com.poc.notification.domain.TenantConfig;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantConfigRepository extends CrudRepository<TenantConfig, Long> {

    @Query("SELECT * FROM NOTF_TENANT_CONFIG WHERE TENANT_ID = :tenantId AND CONFIG_TYPE = :configType")
    Optional<TenantConfig> findByTenantIdAndConfigType(
            @Param("tenantId") String tenantId,
            @Param("configType") String configType);

    @Query("SELECT * FROM NOTF_TENANT_CONFIG WHERE TENANT_ID = :tenantId")
    List<TenantConfig> findByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT * FROM NOTF_TENANT_CONFIG WHERE ENABLED = 1")
    List<TenantConfig> findAllEnabled();

    @Query("SELECT * FROM NOTF_TENANT_CONFIG WHERE ENABLED = 1 AND CONFIG_TYPE = :configType")
    List<TenantConfig> findAllEnabledByConfigType(@Param("configType") String configType);

    @Query("SELECT * FROM NOTF_TENANT_CONFIG WHERE ENABLED = 1 AND IMAP_HOST IS NOT NULL AND IMAP_HOST != ''")
    List<TenantConfig> findAllEnabledWithImap();
}
