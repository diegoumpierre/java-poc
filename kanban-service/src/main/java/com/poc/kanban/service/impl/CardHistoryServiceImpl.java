package com.poc.kanban.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kanban.domain.KanbanCardHistory;
import com.poc.kanban.model.CardHistoryModel;
import com.poc.kanban.model.CreateHistoryRequest;
import com.poc.kanban.repository.jpa.JpaRepositoryCardHistory;
import com.poc.kanban.service.CardHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CardHistoryServiceImpl implements CardHistoryService {

    private final JpaRepositoryCardHistory historyRepository;
    private final ObjectMapper objectMapper;

    public CardHistoryServiceImpl(JpaRepositoryCardHistory historyRepository,
                                   ObjectMapper objectMapper) {
        this.historyRepository = historyRepository;
        this.objectMapper = objectMapper;
    }

    private String resolveUserName(UUID userId, String providedName) {
        if (providedName != null && !providedName.isEmpty() && !"Unknown".equals(providedName)) {
            return providedName;
        }
        return "Unknown User";
    }

    @Override
    public List<CardHistoryModel> getCardHistory(UUID cardId) {
        return historyRepository.findByCardIdOrderByCreatedAtDesc(cardId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardHistoryModel createHistory(UUID cardId, CreateHistoryRequest request, UUID userId, String userName) {
        String resolvedUserName = resolveUserName(userId, userName);

        KanbanCardHistory history = KanbanCardHistory.builder()
                .id(UUID.randomUUID())
                .cardId(cardId)
                .changedBy(userId)
                .changedByName(resolvedUserName)
                .changeType(request.getChangeType())
                .changesJson(toJson(request.getChanges()))
                .comment(request.getComment())
                .snapshotJson(toJson(request.getSnapshot()))
                .createdAt(Instant.now())
                .isNew(true)
                .build();

        KanbanCardHistory saved = historyRepository.save(history);
        log.info("Created history entry for card: {} by user: {}", cardId, resolvedUserName);
        return toModel(saved);
    }

    @Override
    public CardHistoryModel getLastChange(UUID cardId) {
        return historyRepository.findLastByCardId(cardId)
                .map(this::toModel)
                .orElse(null);
    }

    @Override
    public long getHistoryCount(UUID cardId) {
        return historyRepository.countByCardId(cardId);
    }

    @Override
    public List<CardHistoryModel> getRecentHistory(int limit) {
        return historyRepository.findRecentHistory(limit).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCardHistory(UUID cardId) {
        historyRepository.deleteByCardId(cardId);
        log.info("Deleted all history for card: {}", cardId);
    }

    @Override
    @Transactional
    public void deleteHistoryEntry(UUID historyId) {
        historyRepository.deleteById(historyId);
        log.info("Deleted history entry: {}", historyId);
    }

    @Override
    @Transactional
    public void trackCardCreated(UUID cardId, UUID boardId, UUID userId, String userName, Object cardSnapshot) {
        String resolvedUserName = resolveUserName(userId, userName);

        KanbanCardHistory history = KanbanCardHistory.builder()
                .id(UUID.randomUUID())
                .cardId(cardId)
                .boardId(boardId)
                .changedBy(userId)
                .changedByName(resolvedUserName)
                .changeType("CREATE")
                .changesJson(null)
                .comment(null)
                .snapshotJson(toJson(cardSnapshot))
                .createdAt(Instant.now())
                .isNew(true)
                .build();

        historyRepository.save(history);
        log.debug("Tracked card creation: {} by {}", cardId, resolvedUserName);
    }

    @Override
    @Transactional
    public void trackCardUpdated(UUID cardId, UUID boardId, UUID userId, String userName,
                                  List<CardHistoryModel.FieldChange> changes, Object cardSnapshot) {
        if (changes == null || changes.isEmpty()) {
            return; // No changes to track
        }

        String resolvedUserName = resolveUserName(userId, userName);

        KanbanCardHistory history = KanbanCardHistory.builder()
                .id(UUID.randomUUID())
                .cardId(cardId)
                .boardId(boardId)
                .changedBy(userId)
                .changedByName(resolvedUserName)
                .changeType("UPDATE")
                .changesJson(toJson(changes))
                .comment(null)
                .snapshotJson(toJson(cardSnapshot))
                .createdAt(Instant.now())
                .isNew(true)
                .build();

        historyRepository.save(history);
        log.debug("Tracked card update: {} by {} with {} changes", cardId, resolvedUserName, changes.size());
    }

    @Override
    @Transactional
    public void trackCardDeleted(UUID cardId, UUID boardId, UUID userId, String userName) {
        String resolvedUserName = resolveUserName(userId, userName);

        KanbanCardHistory history = KanbanCardHistory.builder()
                .id(UUID.randomUUID())
                .cardId(cardId)
                .boardId(boardId)
                .changedBy(userId)
                .changedByName(resolvedUserName)
                .changeType("DELETE")
                .changesJson(null)
                .comment(null)
                .snapshotJson(null)
                .createdAt(Instant.now())
                .isNew(true)
                .build();

        historyRepository.save(history);
        log.debug("Tracked card deletion: {} by {}", cardId, resolvedUserName);
    }

    @Override
    @Transactional
    public void trackCommentAdded(UUID cardId, UUID boardId, UUID userId, String userName, String comment) {
        String resolvedUserName = resolveUserName(userId, userName);

        KanbanCardHistory history = KanbanCardHistory.builder()
                .id(UUID.randomUUID())
                .cardId(cardId)
                .boardId(boardId)
                .changedBy(userId)
                .changedByName(resolvedUserName)
                .changeType("UPDATE")
                .changesJson(null)
                .comment(comment)
                .snapshotJson(null)
                .createdAt(Instant.now())
                .isNew(true)
                .build();

        historyRepository.save(history);
        log.debug("Tracked comment added to card: {} by {}", cardId, resolvedUserName);
    }

    private CardHistoryModel toModel(KanbanCardHistory entity) {
        return CardHistoryModel.builder()
                .id(entity.getId())
                .entityType("kanban-cards")
                .entityId(entity.getCardId())
                .changedBy(entity.getChangedBy())
                .changedByName(entity.getChangedByName())
                .changedAt(entity.getCreatedAt())
                .changeType(entity.getChangeType())
                .changes(parseChanges(entity.getChangesJson()))
                .comment(entity.getComment())
                .snapshot(parseSnapshot(entity.getSnapshotJson()))
                .build();
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Error serializing to JSON", e);
            return null;
        }
    }

    private List<CardHistoryModel.FieldChange> parseChanges(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<CardHistoryModel.FieldChange>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error parsing changes JSON", e);
            return new ArrayList<>();
        }
    }

    private Object parseSnapshot(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing snapshot JSON", e);
            return null;
        }
    }
}
