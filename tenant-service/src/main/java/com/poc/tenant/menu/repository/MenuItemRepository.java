package com.poc.tenant.menu.repository;

import com.poc.tenant.menu.domain.MenuItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends CrudRepository<MenuItem, UUID> {

    @Query("SELECT * FROM TNT_MENU_ITEMS ORDER BY ORDER_INDEX")
    List<MenuItem> findAllOrderByOrderIndex();

    @Query("SELECT * FROM TNT_MENU_ITEMS WHERE VISIBLE = 1 ORDER BY ORDER_INDEX")
    List<MenuItem> findVisibleOrderByOrderIndex();

    @Query("SELECT * FROM TNT_MENU_ITEMS WHERE VISIBLE = 1 AND MENU_KEY NOT IN (:excludedKeys) ORDER BY ORDER_INDEX")
    List<MenuItem> findVisibleExcludingKeys(@Param("excludedKeys") Collection<String> excludedKeys);
}
