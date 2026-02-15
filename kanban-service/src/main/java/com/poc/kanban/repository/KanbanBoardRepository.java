package com.poc.kanban.repository;

import com.poc.kanban.model.KanbanBoardModel;
import com.poc.kanban.model.PageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KanbanBoardRepository {

    List<KanbanBoardModel> findByUserId(UUID userId);

    Optional<KanbanBoardModel> findByIdAndUserId(UUID id, UUID userId);

    Optional<KanbanBoardModel> findById(UUID id);

    KanbanBoardModel save(KanbanBoardModel boardModel);

    void delete(KanbanBoardModel boardModel);

    void deleteById(UUID id);

    Integer findMaxListPositionByBoardId(UUID boardId);

    // Multi-tenant methods
    List<KanbanBoardModel> findByUserIdAndTenantId(UUID userId, UUID tenantId);

    Optional<KanbanBoardModel> findByIdAndUserIdAndTenantId(UUID id, UUID userId, UUID tenantId);

    Optional<KanbanBoardModel> findByIdAndTenantId(UUID id, UUID tenantId);

    long countByTenantId(UUID tenantId);

    List<KanbanBoardModel> findByTenantIdPaged(UUID tenantId, int limit, int offset);
}
