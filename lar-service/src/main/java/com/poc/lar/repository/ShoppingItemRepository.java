package com.poc.lar.repository;

import com.poc.lar.domain.ShoppingItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingItemRepository extends CrudRepository<ShoppingItem, UUID> {

    @Query("SELECT * FROM LAR_SHOPPING_ITEMS WHERE LIST_ID = :listId ORDER BY CATEGORY, NAME")
    List<ShoppingItem> findByListId(@Param("listId") UUID listId);

    @Query("SELECT * FROM LAR_SHOPPING_ITEMS WHERE ID = :id AND LIST_ID = :listId")
    Optional<ShoppingItem> findByIdAndListId(@Param("id") UUID id, @Param("listId") UUID listId);
}
