package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.KanbanCard;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRepositoryKanbanCard extends CrudRepository<KanbanCard, UUID> {

    @Query("SELECT * FROM KANB_CARDS WHERE list_id = :listId ORDER BY position ASC")
    List<KanbanCard> findByListIdOrderByPositionAsc(@Param("listId") Long listId);

    @Query("SELECT COALESCE(MAX(position), -1) FROM KANB_CARDS WHERE list_id = :listId")
    Integer findMaxPositionByListId(@Param("listId") Long listId);

    @Query("SELECT COUNT(*) FROM KANB_CARDS c JOIN KANB_LISTS l ON c.LIST_ID = l.ID JOIN KANB_BOARDS b ON l.BOARD_ID = b.ID WHERE b.TENANT_ID = :tenantId")
    long countByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT c.* FROM KANB_CARDS c JOIN KANB_LISTS l ON c.LIST_ID = l.ID JOIN KANB_BOARDS b ON l.BOARD_ID = b.ID WHERE b.TENANT_ID = :tenantId ORDER BY c.CREATED_AT DESC LIMIT :limit OFFSET :offset")
    List<KanbanCard> findByTenantIdPaged(@Param("tenantId") UUID tenantId, @Param("limit") int limit, @Param("offset") int offset);
}
