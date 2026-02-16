package com.poc.kanban.service;

import com.poc.kanban.model.CardHistoryModel;
import com.poc.kanban.model.CreateHistoryRequest;

import java.util.List;
import java.util.UUID;

public interface CardHistoryService {

    List<CardHistoryModel> getCardHistory(UUID cardId);

    CardHistoryModel createHistory(UUID cardId, CreateHistoryRequest request, UUID userId, String userName);

    CardHistoryModel getLastChange(UUID cardId);

    long getHistoryCount(UUID cardId);

    List<CardHistoryModel> getRecentHistory(int limit);

    void deleteCardHistory(UUID cardId);

    void deleteHistoryEntry(UUID historyId);

    // Auto-track changes
    void trackCardCreated(UUID cardId, UUID boardId, UUID userId, String userName, Object cardSnapshot);

    void trackCardUpdated(UUID cardId, UUID boardId, UUID userId, String userName,
                          List<CardHistoryModel.FieldChange> changes, Object cardSnapshot);

    void trackCardDeleted(UUID cardId, UUID boardId, UUID userId, String userName);

    void trackCommentAdded(UUID cardId, UUID boardId, UUID userId, String userName, String comment);
}
