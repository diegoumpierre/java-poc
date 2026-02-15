package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.KanbanBoard;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRepositoryKanbanBoard extends CrudRepository<KanbanBoard, UUID> {

    @Query("SELECT * FROM KANB_BOARDS WHERE user_id = :userId")
    List<KanbanBoard> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT * FROM KANB_BOARDS WHERE id = :id AND user_id = :userId")
    Optional<KanbanBoard> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT * FROM KANB_BOARDS WHERE LOWER(title) = LOWER(:title) AND user_id = :userId")
    Optional<KanbanBoard> findByTitleAndUserId(@Param("title") String title, @Param("userId") UUID userId);

    @Query("SELECT * FROM KANB_BOARDS WHERE LOWER(title) = LOWER(:title) AND user_id = :userId AND id != :id")
    Optional<KanbanBoard> findByTitleAndUserIdExcludingBoard(@Param("title") String title, @Param("userId") UUID userId, @Param("id") UUID id);

    // Multi-tenant methods
    @Query("SELECT * FROM KANB_BOARDS WHERE user_id = :userId AND tenant_id = :tenantId")
    List<KanbanBoard> findByUserIdAndTenantId(@Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM KANB_BOARDS WHERE id = :id AND user_id = :userId AND tenant_id = :tenantId")
    Optional<KanbanBoard> findByIdAndUserIdAndTenantId(@Param("id") UUID id, @Param("userId") UUID userId, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM KANB_BOARDS WHERE id = :id AND tenant_id = :tenantId")
    Optional<KanbanBoard> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM KANB_BOARDS WHERE board_code = :boardCode AND tenant_id = :tenantId LIMIT 1")
    Optional<KanbanBoard> findByBoardCodeAndTenantId(@Param("boardCode") String boardCode, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM KANB_BOARDS WHERE tenant_id = :tenantId")
    List<KanbanBoard> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM KANB_BOARDS WHERE BOARD_TYPE_CODE = :boardTypeCode AND TENANT_ID = :tenantId")
    List<KanbanBoard> findByBoardTypeCodeAndTenantId(@Param("boardTypeCode") String boardTypeCode, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM KANB_BOARDS WHERE BOARD_TYPE_CODE = :boardTypeCode AND TENANT_ID = :tenantId AND USER_ID = :userId LIMIT 1")
    Optional<KanbanBoard> findByBoardTypeCodeAndTenantIdAndUserId(@Param("boardTypeCode") String boardTypeCode, @Param("tenantId") UUID tenantId, @Param("userId") UUID userId);

    @Query("SELECT COUNT(*) FROM KANB_BOARDS WHERE TENANT_ID = :tenantId")
    long countByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM KANB_BOARDS WHERE TENANT_ID = :tenantId ORDER BY CREATED_AT DESC LIMIT :limit OFFSET :offset")
    List<KanbanBoard> findByTenantIdPaged(@Param("tenantId") UUID tenantId, @Param("limit") int limit, @Param("offset") int offset);
}
