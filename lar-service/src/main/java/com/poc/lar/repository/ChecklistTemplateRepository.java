package com.poc.lar.repository;

import com.poc.lar.domain.ChecklistTemplate;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistTemplateRepository extends CrudRepository<ChecklistTemplate, UUID> {

    @Query("SELECT * FROM LAR_CHECKLIST_TEMPLATES WHERE TENANT_ID = :tenantId AND ACTIVE = 1 ORDER BY CREATED_AT DESC")
    List<ChecklistTemplate> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_CHECKLIST_TEMPLATES WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<ChecklistTemplate> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_CHECKLIST_TEMPLATES WHERE TENANT_ID = :tenantId AND TYPE = :type AND ACTIVE = 1 ORDER BY CREATED_AT DESC")
    List<ChecklistTemplate> findByType(@Param("tenantId") UUID tenantId, @Param("type") String type);
}
