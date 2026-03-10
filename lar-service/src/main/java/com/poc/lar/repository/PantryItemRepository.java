package com.poc.lar.repository;

import com.poc.lar.domain.PantryItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PantryItemRepository extends CrudRepository<PantryItem, UUID> {

    @Query("SELECT * FROM LAR_PANTRY_ITEMS WHERE TENANT_ID = :tenantId ORDER BY CATEGORY, NAME")
    List<PantryItem> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_PANTRY_ITEMS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<PantryItem> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_PANTRY_ITEMS WHERE TENANT_ID = :tenantId AND STATUS IN ('LOW', 'EMPTY') ORDER BY NAME")
    List<PantryItem> findLow(@Param("tenantId") UUID tenantId);
}
