package com.poc.kanban.controller;

import com.poc.kanban.model.CardDetailResponse;
import com.poc.kanban.model.CardHistoryModel;
import com.poc.kanban.model.CreateHistoryRequest;
import com.poc.kanban.model.KanbanAttachmentModel;
import com.poc.kanban.model.KanbanAcceptanceCriteriaModel;
import com.poc.kanban.model.KanbanBoardModel;
import com.poc.kanban.model.KanbanCardModel;
import com.poc.kanban.model.KanbanCommentModel;
import com.poc.kanban.model.KanbanLabelModel;
import com.poc.kanban.model.KanbanListModel;
import com.poc.kanban.model.KanbanSubTaskModel;
import com.poc.kanban.service.CardHistoryService;
import com.poc.kanban.service.KanbanAttachmentService;
import com.poc.kanban.service.KanbanBoardService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Kanban", description = "Kanban Board Management APIs")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/kanban")
@RequiredArgsConstructor
public class KanbanController {

    private final KanbanBoardService kanbanBoardService;
    private final KanbanAttachmentService kanbanAttachmentService;
    private final CardHistoryService cardHistoryService;

    // ==================== Board Operations ====================

    @Operation(summary = "List all boards", description = "Get all Kanban boards for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boards retrieved successfully")
    })
    @GetMapping("/boards")
    public ResponseEntity<List<KanbanBoardModel>> listBoards(
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.findAllByUserId(userId));
    }

    @Operation(summary = "Get board by ID", description = "Get a specific Kanban board with all lists and cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<KanbanBoardModel> getBoard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.findByIdAndUserId(boardId, userId));
    }

    @Operation(summary = "Create board", description = "Create a new Kanban board. Board title must be unique per user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request - board title is empty or a board with this name already exists")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/boards")
    public ResponseEntity<KanbanBoardModel> createBoard(
            @Valid @RequestBody CreateBoardRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.create(userId, request.title()));
    }

    @Operation(summary = "Update board", description = "Update a Kanban board title. Board title must be unique per user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request - board title is empty or a board with this name already exists"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/boards/{boardId}")
    public ResponseEntity<KanbanBoardModel> updateBoard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Valid @RequestBody CreateBoardRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.update(boardId, userId, request.title()));
    }

    @Operation(summary = "Delete board", description = "Delete a Kanban board and all its contents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Board deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.delete(boardId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Save board state", description = "Save the entire board state (bulk update)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board state saved successfully")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/boards/{boardId}/state")
    public ResponseEntity<KanbanBoardModel> saveBoardState(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Valid @RequestBody KanbanBoardModel boardModel,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        boardModel.setId(boardId);
        return ResponseEntity.ok(kanbanBoardService.saveBoardState(userId, boardModel));
    }

    // ==================== List Operations ====================

    @Operation(summary = "Add list to board", description = "Add a new list to a Kanban board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List added successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/boards/{boardId}/lists")
    public ResponseEntity<KanbanListModel> addList(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Valid @RequestBody CreateListRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.addList(boardId, userId, request.title()));
    }

    @Operation(summary = "Update list title", description = "Update a list title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List updated successfully"),
            @ApiResponse(responseCode = "404", description = "Board or list not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/boards/{boardId}/lists/{listId}")
    public ResponseEntity<KanbanListModel> updateListTitle(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "List ID") @PathVariable UUID listId,
            @Valid @RequestBody CreateListRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.updateListTitle(boardId, listId, userId, request.title()));
    }

    @Operation(summary = "Delete list", description = "Delete a list and all its cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "List deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Board or list not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/boards/{boardId}/lists/{listId}")
    public ResponseEntity<Void> deleteList(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "List ID") @PathVariable UUID listId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.deleteList(boardId, listId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Card Operations ====================

    @Operation(summary = "Get card by ID or code", description = "Get a card with its board and list context. Accepts UUID or card code (e.g. KB-0001)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/cards/{cardIdOrCode}")
    public ResponseEntity<CardDetailResponse> getCard(
            @Parameter(description = "Card ID (UUID) or card code (e.g. KB-0001)") @PathVariable String cardIdOrCode,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        try {
            UUID cardId = UUID.fromString(cardIdOrCode);
            return ResponseEntity.ok(kanbanBoardService.findCardDetailById(cardId, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(kanbanBoardService.findCardDetailByCode(cardIdOrCode, userId));
        }
    }

    @Operation(summary = "Search cards", description = "Search and filter cards in a board by multiple criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @GetMapping("/boards/{boardId}/cards/search")
    public ResponseEntity<List<KanbanCardModel>> searchCards(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "Assignee User ID") @RequestParam(required = false) UUID assigneeUserId,
            @Parameter(description = "Priority title (e.g., HIGH, MEDIUM, LOW)") @RequestParam(required = false) String priority,
            @Parameter(description = "Due date from (inclusive)") @RequestParam(required = false) java.time.LocalDate dueDateFrom,
            @Parameter(description = "Due date to (inclusive)") @RequestParam(required = false) java.time.LocalDate dueDateTo,
            @Parameter(description = "Completed status") @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Search text in title/description") @RequestParam(required = false) String search,
            @Parameter(description = "Label ID") @RequestParam(required = false) UUID labelId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.searchCards(
                boardId, userId, assigneeUserId, priority, dueDateFrom, dueDateTo, completed, search, labelId
        ));
    }

    @Operation(summary = "Add card to list", description = "Add a new card to a list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card added successfully"),
            @ApiResponse(responseCode = "404", description = "Board or list not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/boards/{boardId}/lists/{listId}/cards")
    public ResponseEntity<KanbanCardModel> addCard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "List ID") @PathVariable UUID listId,
            @Valid @RequestBody KanbanCardModel cardModel,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.addCard(boardId, listId, userId, cardModel));
    }

    @Operation(summary = "Update card", description = "Update a card's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card updated successfully"),
            @ApiResponse(responseCode = "404", description = "Board, list, or card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/boards/{boardId}/lists/{listId}/cards/{cardId}")
    public ResponseEntity<KanbanCardModel> updateCard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "List ID") @PathVariable UUID listId,
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Valid @RequestBody KanbanCardModel cardModel,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.updateCard(boardId, listId, cardId, userId, cardModel));
    }

    @Operation(summary = "Delete card", description = "Delete a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Board, list, or card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/boards/{boardId}/lists/{listId}/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "List ID") @PathVariable UUID listId,
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.deleteCard(boardId, listId, cardId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Move card", description = "Move a card to another list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card moved successfully"),
            @ApiResponse(responseCode = "404", description = "Board, list, or card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/boards/{boardId}/lists/{sourceListId}/cards/{cardId}/move")
    public ResponseEntity<Void> moveCard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "Source List ID") @PathVariable UUID sourceListId,
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Valid @RequestBody MoveCardRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.moveCard(boardId, sourceListId, request.targetListId(), cardId, userId, request.targetIndex());
        return ResponseEntity.noContent().build();
    }

    // ==================== Card History Operations ====================

    @Operation(summary = "Get card history", description = "Get the complete change history of a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/cards/{cardId}/history")
    public ResponseEntity<List<CardHistoryModel>> getCardHistory(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        return ResponseEntity.ok(cardHistoryService.getCardHistory(cardId));
    }

    @Operation(summary = "Create history entry", description = "Create a new history entry for a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History entry created successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/cards/{cardId}/history")
    public ResponseEntity<CardHistoryModel> createHistoryEntry(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Valid @RequestBody CreateHistoryRequest request,
            @RequestHeader("X-User-Id") String userIdHeader,
            @RequestHeader(value = "X-User-Name", defaultValue = "Unknown") String userName) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(cardHistoryService.createHistory(cardId, request, userId, userName));
    }

    @Operation(summary = "Get last change", description = "Get the most recent change for a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Last change retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found or no history")
    })
    @GetMapping("/cards/{cardId}/history/last")
    public ResponseEntity<CardHistoryModel> getLastChange(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        CardHistoryModel lastChange = cardHistoryService.getLastChange(cardId);
        if (lastChange == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lastChange);
    }

    @Operation(summary = "Get history count", description = "Get the number of history entries for a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History count retrieved successfully")
    })
    @GetMapping("/cards/{cardId}/history/count")
    public ResponseEntity<Long> getHistoryCount(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        return ResponseEntity.ok(cardHistoryService.getHistoryCount(cardId));
    }

    @Operation(summary = "Get recent history", description = "Get recent history entries across all cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recent history retrieved successfully")
    })
    @GetMapping("/history/recent")
    public ResponseEntity<List<CardHistoryModel>> getRecentHistory(
            @Parameter(description = "Maximum number of entries") @RequestParam(defaultValue = "50") int limit,
            @RequestHeader("X-User-Id") String userIdHeader) {
        return ResponseEntity.ok(cardHistoryService.getRecentHistory(limit));
    }

    @Operation(summary = "Delete card history", description = "Delete all history entries for a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card history deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/cards/{cardId}/history")
    public ResponseEntity<Void> deleteCardHistory(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        cardHistoryService.deleteCardHistory(cardId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete history entry", description = "Delete a specific history entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "History entry deleted successfully"),
            @ApiResponse(responseCode = "404", description = "History entry not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/history/{historyId}")
    public ResponseEntity<Void> deleteHistoryEntry(
            @Parameter(description = "History ID") @PathVariable UUID historyId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        cardHistoryService.deleteHistoryEntry(historyId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Attachment Operations ====================

    @Operation(summary = "List attachments", description = "Get all attachments for a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachments retrieved successfully")
    })
    @GetMapping("/cards/{cardId}/attachments")
    public ResponseEntity<List<KanbanAttachmentModel>> listAttachments(
            @Parameter(description = "Card ID") @PathVariable UUID cardId) {
        return ResponseEntity.ok(kanbanAttachmentService.findByCardId(cardId));
    }

    @Operation(summary = "Upload attachment", description = "Upload a file attachment to a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment uploaded successfully")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping(value = "/cards/{cardId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KanbanAttachmentModel> uploadAttachment(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(kanbanAttachmentService.upload(cardId, file));
    }

    @Operation(summary = "Download attachment", description = "Download a file attachment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Attachment not found")
    })
    @GetMapping("/cards/{cardId}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Attachment ID") @PathVariable UUID attachmentId) {
        Resource resource = kanbanAttachmentService.download(cardId, attachmentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body(resource);
    }

    @Operation(summary = "Delete attachment", description = "Delete a file attachment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Attachment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Attachment not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/cards/{cardId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Attachment ID") @PathVariable UUID attachmentId) {
        kanbanAttachmentService.delete(cardId, attachmentId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Comment Operations ====================

    @Operation(summary = "Add comment to card", description = "Add a new comment to a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/cards/{cardId}/comments")
    public ResponseEntity<KanbanCommentModel> addComment(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Valid @RequestBody CreateCommentRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.addComment(cardId, userId, request.text()));
    }

    @Operation(summary = "Update comment", description = "Update a comment's text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Card or comment not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/cards/{cardId}/comments/{commentId}")
    public ResponseEntity<KanbanCommentModel> updateComment(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Comment ID") @PathVariable UUID commentId,
            @Valid @RequestBody CreateCommentRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.updateComment(cardId, commentId, userId, request.text()));
    }

    @Operation(summary = "Delete comment", description = "Delete a comment from a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card or comment not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/cards/{cardId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Comment ID") @PathVariable UUID commentId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.deleteComment(cardId, commentId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== SubTask Operations ====================

    @Operation(summary = "Add subtask to card", description = "Add a new subtask to a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubTask added successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/cards/{cardId}/subtasks")
    public ResponseEntity<KanbanSubTaskModel> addSubTask(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Valid @RequestBody KanbanSubTaskModel subTaskModel,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.addSubTask(cardId, userId, subTaskModel));
    }

    @Operation(summary = "Update subtask", description = "Update a subtask's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubTask updated successfully"),
            @ApiResponse(responseCode = "404", description = "Card or subtask not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/cards/{cardId}/subtasks/{subtaskId}")
    public ResponseEntity<KanbanSubTaskModel> updateSubTask(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "SubTask ID") @PathVariable UUID subtaskId,
            @Valid @RequestBody KanbanSubTaskModel subTaskModel,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.updateSubTask(cardId, subtaskId, userId, subTaskModel));
    }

    @Operation(summary = "Delete subtask", description = "Delete a subtask from a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "SubTask deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card or subtask not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/cards/{cardId}/subtasks/{subtaskId}")
    public ResponseEntity<Void> deleteSubTask(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "SubTask ID") @PathVariable UUID subtaskId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.deleteSubTask(cardId, subtaskId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Acceptance Criteria Operations ====================

    @Operation(summary = "Add acceptance criteria to card", description = "Add a new acceptance criteria to a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acceptance criteria added successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/cards/{cardId}/acceptance-criteria")
    public ResponseEntity<KanbanAcceptanceCriteriaModel> addAcceptanceCriteria(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Valid @RequestBody KanbanAcceptanceCriteriaModel model,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.addAcceptanceCriteria(cardId, userId, model));
    }

    @Operation(summary = "Update acceptance criteria", description = "Update an acceptance criteria's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acceptance criteria updated successfully"),
            @ApiResponse(responseCode = "404", description = "Card or acceptance criteria not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/cards/{cardId}/acceptance-criteria/{criteriaId}")
    public ResponseEntity<KanbanAcceptanceCriteriaModel> updateAcceptanceCriteria(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Criteria ID") @PathVariable UUID criteriaId,
            @Valid @RequestBody KanbanAcceptanceCriteriaModel model,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.updateAcceptanceCriteria(cardId, criteriaId, userId, model));
    }

    @Operation(summary = "Delete acceptance criteria", description = "Delete an acceptance criteria from a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Acceptance criteria deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card or acceptance criteria not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/cards/{cardId}/acceptance-criteria/{criteriaId}")
    public ResponseEntity<Void> deleteAcceptanceCriteria(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Criteria ID") @PathVariable UUID criteriaId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.deleteAcceptanceCriteria(cardId, criteriaId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Label Operations ====================

    @Operation(summary = "Get board labels", description = "Get all labels for a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Labels retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @GetMapping("/boards/{boardId}/labels")
    public ResponseEntity<List<KanbanLabelModel>> getBoardLabels(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.getBoardLabels(boardId, userId));
    }

    @Operation(summary = "Create label", description = "Create a new label for a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label created successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/boards/{boardId}/labels")
    public ResponseEntity<KanbanLabelModel> createLabel(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Valid @RequestBody CreateLabelRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.createLabel(boardId, userId, request.name(), request.color()));
    }

    @Operation(summary = "Update label", description = "Update a label's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label updated successfully"),
            @ApiResponse(responseCode = "404", description = "Board or label not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/boards/{boardId}/labels/{labelId}")
    public ResponseEntity<KanbanLabelModel> updateLabel(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "Label ID") @PathVariable UUID labelId,
            @Valid @RequestBody CreateLabelRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(kanbanBoardService.updateLabel(boardId, labelId, userId, request.name(), request.color()));
    }

    @Operation(summary = "Delete label", description = "Delete a label from a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Label deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Board or label not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/boards/{boardId}/labels/{labelId}")
    public ResponseEntity<Void> deleteLabel(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "Label ID") @PathVariable UUID labelId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.deleteLabel(boardId, labelId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add label to card", description = "Assign a label to a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Label assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Card or label not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/cards/{cardId}/labels/{labelId}")
    public ResponseEntity<Void> addLabelToCard(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Label ID") @PathVariable UUID labelId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.addLabelToCard(cardId, labelId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove label from card", description = "Remove a label assignment from a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Label removed successfully"),
            @ApiResponse(responseCode = "404", description = "Card or label not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/cards/{cardId}/labels/{labelId}")
    public ResponseEntity<Void> removeLabelFromCard(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Label ID") @PathVariable UUID labelId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        kanbanBoardService.removeLabelFromCard(cardId, labelId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Request DTOs ====================

    public record CreateBoardRequest(
            @NotBlank(message = "Board title is required")
            @Size(min = 1, max = 100, message = "Board title must be between 1 and 100 characters")
            String title
    ) {}

    public record CreateListRequest(
            @NotBlank(message = "List title is required")
            @Size(min = 1, max = 100, message = "List title must be between 1 and 100 characters")
            String title
    ) {}

    public record MoveCardRequest(
            @NotNull(message = "Target list ID is required")
            UUID targetListId,
            @Min(value = 0, message = "Target index must be non-negative")
            Integer targetIndex
    ) {}

    public record CreateCommentRequest(
            @NotBlank(message = "Comment text is required")
            @Size(min = 1, max = 2000, message = "Comment must be between 1 and 2000 characters")
            String text
    ) {}

    public record CreateLabelRequest(
            @NotBlank(message = "Label name is required")
            @Size(min = 1, max = 50, message = "Label name must be between 1 and 50 characters")
            String name,
            @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color code (e.g., #FF5733)")
            String color
    ) {}
}
