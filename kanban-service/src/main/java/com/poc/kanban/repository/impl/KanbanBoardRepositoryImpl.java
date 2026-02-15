package com.poc.kanban.repository.impl;

import com.poc.kanban.converter.KanbanBoardModelToKanbanBoardConverter;
import com.poc.kanban.converter.KanbanBoardToKanbanBoardModelConverter;
import com.poc.kanban.domain.KanbanBoard;
import com.poc.kanban.model.KanbanBoardModel;
import com.poc.kanban.repository.KanbanBoardRepository;
import com.poc.kanban.repository.jpa.JpaRepositoryKanbanBoard;
import com.poc.kanban.repository.jpa.JpaRepositoryKanbanList;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class KanbanBoardRepositoryImpl implements KanbanBoardRepository {

    private final JpaRepositoryKanbanBoard jpaRepository;
    private final JpaRepositoryKanbanList listRepository;
    private final KanbanBoardToKanbanBoardModelConverter domainToModelConverter;
    private final KanbanBoardModelToKanbanBoardConverter modelToDomainConverter;

    public KanbanBoardRepositoryImpl(
            JpaRepositoryKanbanBoard jpaRepository,
            JpaRepositoryKanbanList listRepository,
            KanbanBoardToKanbanBoardModelConverter domainToModelConverter,
            KanbanBoardModelToKanbanBoardConverter modelToDomainConverter) {
        this.jpaRepository = jpaRepository;
        this.listRepository = listRepository;
        this.domainToModelConverter = domainToModelConverter;
        this.modelToDomainConverter = modelToDomainConverter;
    }

    @Override
    public List<KanbanBoardModel> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(domainToModelConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<KanbanBoardModel> findByIdAndUserId(UUID id, UUID userId) {
        return jpaRepository.findByIdAndUserId(id, userId)
                .map(domainToModelConverter::convert);
    }

    @Override
    public Optional<KanbanBoardModel> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(domainToModelConverter::convert);
    }

    @Override
    public KanbanBoardModel save(KanbanBoardModel boardModel) {
        KanbanBoard board = modelToDomainConverter.convert(boardModel);
        if (board != null) {
            if (boardModel.getUserId() != null) {
                board.setUserId(boardModel.getUserId());
            }
            KanbanBoard savedBoard = jpaRepository.save(board);
            return domainToModelConverter.convert(savedBoard);
        }
        return null;
    }

    @Override
    public void delete(KanbanBoardModel boardModel) {
        if (boardModel.getId() != null) {
            jpaRepository.deleteById(boardModel.getId());
        }
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Integer findMaxListPositionByBoardId(UUID boardId) {
        return listRepository.findMaxPositionByBoardId(boardId);
    }

    @Override
    public List<KanbanBoardModel> findByUserIdAndTenantId(UUID userId, UUID tenantId) {
        return jpaRepository.findByUserIdAndTenantId(userId, tenantId).stream()
                .map(domainToModelConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<KanbanBoardModel> findByIdAndUserIdAndTenantId(UUID id, UUID userId, UUID tenantId) {
        return jpaRepository.findByIdAndUserIdAndTenantId(id, userId, tenantId)
                .map(domainToModelConverter::convert);
    }

    @Override
    public Optional<KanbanBoardModel> findByIdAndTenantId(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId)
                .map(domainToModelConverter::convert);
    }

    @Override
    public long countByTenantId(UUID tenantId) {
        return jpaRepository.countByTenantId(tenantId);
    }

    @Override
    public List<KanbanBoardModel> findByTenantIdPaged(UUID tenantId, int limit, int offset) {
        return jpaRepository.findByTenantIdPaged(tenantId, limit, offset).stream()
                .map(domainToModelConverter::convert)
                .collect(Collectors.toList());
    }
}
