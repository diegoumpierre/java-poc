package com.poc.kanban.service;

import com.poc.kanban.model.CardDetailResponse;
import com.poc.kanban.model.KanbanBoardModel;
import com.poc.kanban.model.KanbanCardModel;
import com.poc.kanban.model.KanbanCommentModel;
import com.poc.kanban.model.KanbanLabelModel;
import com.poc.kanban.model.KanbanListModel;
import com.poc.kanban.model.KanbanSubTaskModel;
import com.poc.kanban.model.KanbanAcceptanceCriteriaModel;

import java.util.List;
import java.util.UUID;

public interface KanbanBoardService {

    List<KanbanBoardModel> findAllByUserId(UUID userId);

    KanbanBoardModel findByIdAndUserId(UUID id, UUID userId);

    KanbanBoardModel create(UUID userId, String title);

    KanbanBoardModel update(UUID id, UUID userId, String title);

    void delete(UUID id, UUID userId);

    // List operations
    KanbanListModel addList(UUID boardId, UUID userId, String title);

    KanbanListModel updateListTitle(UUID boardId, UUID listId, UUID userId, String title);

    void deleteList(UUID boardId, UUID listId, UUID userId);

    // Card operations
    KanbanCardModel addCard(UUID boardId, UUID listId, UUID userId, KanbanCardModel cardModel);

    KanbanCardModel updateCard(UUID boardId, UUID listId, UUID cardId, UUID userId, KanbanCardModel cardModel);

    void deleteCard(UUID boardId, UUID listId, UUID cardId, UUID userId);

    void moveCard(UUID boardId, UUID sourceListId, UUID targetListId, UUID cardId, UUID userId, Integer targetIndex);

    KanbanCardModel findCardById(UUID cardId, UUID userId);

    CardDetailResponse findCardDetailById(UUID cardId, UUID userId);

    CardDetailResponse findCardDetailByCode(String cardCode, UUID userId);

    // Card search/filter
    List<KanbanCardModel> searchCards(
            UUID boardId,
            UUID userId,
            UUID assigneeUserId,
            String priority,
            java.time.LocalDate dueDateFrom,
            java.time.LocalDate dueDateTo,
            Boolean completed,
            String search,
            UUID labelId
    );

    // Full board save (for bulk updates)
    KanbanBoardModel saveBoardState(UUID userId, KanbanBoardModel boardModel);

    // Comment operations
    KanbanCommentModel addComment(UUID cardId, UUID userId, String text);

    KanbanCommentModel updateComment(UUID cardId, UUID commentId, UUID userId, String text);

    void deleteComment(UUID cardId, UUID commentId, UUID userId);

    // SubTask operations
    KanbanSubTaskModel addSubTask(UUID cardId, UUID userId, KanbanSubTaskModel subTaskModel);

    KanbanSubTaskModel updateSubTask(UUID cardId, UUID subtaskId, UUID userId, KanbanSubTaskModel subTaskModel);

    void deleteSubTask(UUID cardId, UUID subtaskId, UUID userId);

    // Acceptance Criteria operations
    KanbanAcceptanceCriteriaModel addAcceptanceCriteria(UUID cardId, UUID userId, KanbanAcceptanceCriteriaModel model);

    KanbanAcceptanceCriteriaModel updateAcceptanceCriteria(UUID cardId, UUID criteriaId, UUID userId, KanbanAcceptanceCriteriaModel model);

    void deleteAcceptanceCriteria(UUID cardId, UUID criteriaId, UUID userId);

    // Label operations
    KanbanLabelModel createLabel(UUID boardId, UUID userId, String name, String color);

    KanbanLabelModel updateLabel(UUID boardId, UUID labelId, UUID userId, String name, String color);

    void deleteLabel(UUID boardId, UUID labelId, UUID userId);

    java.util.List<KanbanLabelModel> getBoardLabels(UUID boardId, UUID userId);

    // Card-Label assignment operations
    void addLabelToCard(UUID cardId, UUID labelId, UUID userId);

    void removeLabelFromCard(UUID cardId, UUID labelId, UUID userId);
}
