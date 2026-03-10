package com.poc.lar.repository;

import com.poc.lar.domain.ShoppingList;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingListRepository extends CrudRepository<ShoppingList, UUID> {

    @Query("SELECT * FROM LAR_SHOPPING_LISTS WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC")
    List<ShoppingList> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_SHOPPING_LISTS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<ShoppingList> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_SHOPPING_LISTS WHERE TENANT_ID = :tenantId AND STATUS IN ('ACTIVE', 'SHOPPING') ORDER BY CREATED_AT DESC")
    List<ShoppingList> findActive(@Param("tenantId") UUID tenantId);
}
