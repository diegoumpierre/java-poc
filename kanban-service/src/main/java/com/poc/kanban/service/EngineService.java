package com.poc.kanban.service;

import com.poc.kanban.model.ApprovalModel;
import com.poc.kanban.model.BoardTypeModel;
import com.poc.kanban.model.CountModel;
import com.poc.kanban.model.CreateEngineBoardRequest;
import com.poc.kanban.model.CreateEngineCardRequest;
import com.poc.kanban.model.EngineBoardModel;
import com.poc.kanban.model.EngineCardModel;
import com.poc.kanban.model.KanbanAttachmentModel;
import com.poc.kanban.model.KanbanBoardModel;
import com.poc.kanban.model.KanbanCommentModel;
import com.poc.kanban.model.KanbanLabelModel;
import com.poc.kanban.model.KanbanSubTaskModel;
import com.poc.kanban.model.CardHistoryModel;
import com.poc.kanban.model.MoveCardRequest;
import com.poc.kanban.model.PageResponse;
import com.poc.kanban.model.WorkflowModel;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EngineService {

    // ==================== Board Types ====================

    List<BoardTypeModel> getBoardTypes();

    BoardTypeModel getBoardType(String code);

    WorkflowModel getWorkflow(String boardTypeCode);

    // ==================== Boards ====================

    List<EngineBoardModel> getBoards(UUID tenantId, String type);

    PageResponse<EngineBoardModel> getBoardsPaged(UUID tenantId, int page, int size);

    EngineBoardModel findBoard(String type, UUID tenantId, UUID userId);

    EngineBoardModel createBoard(CreateEngineBoardRequest request, UUID tenantId, UUID userId);

    EngineBoardModel getBoard(UUID boardId, UUID tenantId);

    EngineBoardModel updateBoard(UUID boardId, EngineBoardModel boardModel, UUID tenantId, UUID userId);

    void deleteBoard(UUID boardId, UUID tenantId, UUID userId);

    EngineBoardModel saveBoardState(UUID boardId, KanbanBoardModel boardModel, UUID tenantId, UUID userId);

    // ==================== Cards ====================

    EngineCardModel createCard(UUID boardId, CreateEngineCardRequest request, UUID tenantId, UUID userId);

    EngineCardModel getCard(UUID cardId, UUID tenantId);

    EngineCardModel getCardByCode(String code, UUID tenantId);

    EngineCardModel updateCard(UUID cardId, EngineCardModel cardModel, UUID tenantId, UUID userId);

    void deleteCard(UUID cardId, UUID tenantId, UUID userId);

    EngineCardModel moveCard(UUID cardId, MoveCardRequest request, UUID tenantId, UUID userId);

    List<EngineCardModel> searchCards(UUID boardId, UUID tenantId, UUID assigneeUserId,
                                       String priority, LocalDate dueDateFrom, LocalDate dueDateTo,
                                       Boolean completed, String search, UUID labelId, String sourceService);

    // ==================== Comments ====================

    KanbanCommentModel addComment(UUID cardId, String text, UUID tenantId, UUID userId);

    KanbanCommentModel updateComment(UUID cardId, UUID commentId, String text, UUID tenantId, UUID userId);

    void deleteComment(UUID cardId, UUID commentId, UUID tenantId, UUID userId);

    // ==================== Subtasks ====================

    KanbanSubTaskModel addSubTask(UUID cardId, KanbanSubTaskModel subTaskModel, UUID tenantId, UUID userId);

    KanbanSubTaskModel updateSubTask(UUID cardId, UUID subtaskId, KanbanSubTaskModel subTaskModel, UUID tenantId, UUID userId);

    void deleteSubTask(UUID cardId, UUID subtaskId, UUID tenantId, UUID userId);

    // ==================== Labels ====================

    void addLabelToCard(UUID cardId, UUID labelId, UUID tenantId, UUID userId);

    void removeLabelFromCard(UUID cardId, UUID labelId, UUID tenantId, UUID userId);

    List<KanbanLabelModel> getBoardLabels(UUID boardId, UUID tenantId);

    KanbanLabelModel createLabel(UUID boardId, String name, String color, UUID tenantId, UUID userId);

    KanbanLabelModel updateLabel(UUID boardId, UUID labelId, String name, String color, UUID tenantId, UUID userId);

    void deleteLabel(UUID boardId, UUID labelId, UUID tenantId, UUID userId);

    // ==================== Attachments ====================

    List<KanbanAttachmentModel> getAttachments(UUID cardId, UUID tenantId);

    KanbanAttachmentModel uploadAttachment(UUID cardId, MultipartFile file, UUID tenantId, UUID userId);

    Resource downloadAttachment(UUID cardId, UUID attachmentId, UUID tenantId);

    void deleteAttachment(UUID cardId, UUID attachmentId, UUID tenantId, UUID userId);

    // ==================== Assignees ====================

    void addAssignee(UUID cardId, UUID assigneeUserId, UUID tenantId, UUID userId);

    void removeAssignee(UUID cardId, UUID assigneeUserId, UUID tenantId, UUID userId);

    // ==================== History ====================

    List<CardHistoryModel> getCardHistory(UUID cardId, UUID tenantId);

    // ==================== Approvals ====================

    List<ApprovalModel> getApprovals(UUID tenantId, String status, UUID boardId, UUID cardId);

    CountModel countApprovals(UUID tenantId, String status);

    ApprovalModel approveCard(UUID approvalId, String comment, UUID tenantId, UUID userId);

    ApprovalModel rejectCard(UUID approvalId, String comment, UUID tenantId, UUID userId);
}
