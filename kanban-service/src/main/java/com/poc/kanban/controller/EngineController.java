package com.poc.kanban.controller;

import com.poc.kanban.model.ApprovalModel;
import com.poc.kanban.model.BoardTypeModel;
import com.poc.kanban.model.CountModel;
import com.poc.kanban.model.CreateEngineBoardRequest;
import com.poc.kanban.model.CreateEngineCardRequest;
import com.poc.kanban.model.EngineBoardModel;
import com.poc.kanban.model.EngineCardModel;
import com.poc.kanban.model.CardHistoryModel;
import com.poc.kanban.model.KanbanAttachmentModel;
import com.poc.kanban.model.KanbanBoardModel;
import com.poc.kanban.model.KanbanCommentModel;
import com.poc.kanban.model.KanbanLabelModel;
import com.poc.kanban.model.KanbanSubTaskModel;
import com.poc.kanban.model.MoveCardRequest;
import com.poc.kanban.model.PageResponse;
import com.poc.kanban.model.WorkflowModel;
import com.poc.kanban.service.EngineService;
import com.poc.shared.security.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Kanban Engine", description = "Kanban Engine APIs - workflow-driven board management for products")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/kanban/engine")
@RequiredArgsConstructor
public class EngineController {

    private final EngineService engineService;

    // ==================== Board Type Operations ====================

    @Operation(summary = "List board types", description = "Get all available board type configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board types retrieved successfully")
    })
    @GetMapping("/board-types")
    public ResponseEntity<List<BoardTypeModel>> listBoardTypes() {
        return ResponseEntity.ok(engineService.getBoardTypes());
    }

    @Operation(summary = "Get board type", description = "Get a specific board type configuration with features")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board type retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Board type not found")
    })
    @GetMapping("/board-types/{code}")
    public ResponseEntity<BoardTypeModel> getBoardType(
            @Parameter(description = "Board type code") @PathVariable String code) {
        return ResponseEntity.ok(engineService.getBoardType(code));
    }

    @Operation(summary = "Get board type workflow", description = "Get the workflow steps and transitions for a board type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workflow retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Board type not found")
    })
    @GetMapping("/board-types/{code}/workflow")
    public ResponseEntity<WorkflowModel> getBoardTypeWorkflow(
            @Parameter(description = "Board type code") @PathVariable String code) {
        return ResponseEntity.ok(engineService.getWorkflow(code));
    }

    // ==================== Board Operations ====================

    @Operation(summary = "List engine boards", description = "Get all engine boards for the tenant, optionally filtered by type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boards retrieved successfully")
    })
    @GetMapping("/boards")
    public ResponseEntity<List<EngineBoardModel>> listBoards(
            @Parameter(description = "Board type code filter (e.g., HELPDESK)") @RequestParam(required = false) String type,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.getBoards(tenantId, type));
    }

    @Operation(summary = "List engine boards (paged)", description = "Get engine boards for the tenant with server-side pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boards retrieved successfully")
    })
    @GetMapping("/boards/paged")
    public ResponseEntity<PageResponse<EngineBoardModel>> listBoardsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.getBoardsPaged(tenantId, page, size));
    }

    @Operation(summary = "Find board", description = "Find a board by type and tenant. For singleton boards, returns the single board. For scope=USER boards, userId is required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board found successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @GetMapping("/boards/find")
    public ResponseEntity<EngineBoardModel> findBoard(
            @Parameter(description = "Board type code (required)") @RequestParam String type,
            @Parameter(description = "User ID (for scope=USER boards)") @RequestParam(required = false) UUID userId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.findBoard(type, tenantId, userId));
    }

    @Operation(summary = "Create engine board", description = "Create a new engine board from a board type configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/boards")
    public ResponseEntity<EngineBoardModel> createBoard(
            @Valid @RequestBody CreateEngineBoardRequest request,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.createBoard(request, tenantId, userId));
    }

    @Operation(summary = "Get engine board", description = "Get a specific engine board with all lists and cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<EngineBoardModel> getBoard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.getBoard(boardId, tenantId));
    }

    @Operation(summary = "Update engine board", description = "Update an engine board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board updated successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/boards/{boardId}")
    public ResponseEntity<EngineBoardModel> updateBoard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Valid @RequestBody EngineBoardModel boardModel,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.updateBoard(boardId, boardModel, tenantId, userId));
    }

    @Operation(summary = "Delete engine board", description = "Delete an engine board and all its contents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Board deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.deleteBoard(boardId, tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Save board state", description = "Bulk update board state (drag-and-drop reordering)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board state saved successfully")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/boards/{boardId}/state")
    public ResponseEntity<EngineBoardModel> saveBoardState(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Valid @RequestBody KanbanBoardModel boardModel,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.saveBoardState(boardId, boardModel, tenantId, userId));
    }

    // ==================== Card Operations ====================

    @Operation(summary = "Create card", description = "Create a new card on a board. Specify targetStepCode or targetListId for placement.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/boards/{boardId}/cards")
    public ResponseEntity<EngineCardModel> createCard(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Valid @RequestBody CreateEngineCardRequest request,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.createCard(boardId, request, tenantId, userId));
    }

    @Operation(summary = "Get card by ID", description = "Get a card with full details including items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/cards/{cardId}")
    public ResponseEntity<EngineCardModel> getCard(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.getCard(cardId, tenantId));
    }

    @Operation(summary = "Get card by code", description = "Get a card by its sequential code (e.g., HD-0001)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/cards/code/{code}")
    public ResponseEntity<EngineCardModel> getCardByCode(
            @Parameter(description = "Card code (e.g., HD-0001)") @PathVariable String code,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.getCardByCode(code, tenantId));
    }

    @Operation(summary = "Update card", description = "Update a card's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card updated successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PutMapping("/cards/{cardId}")
    public ResponseEntity<EngineCardModel> updateCard(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Valid @RequestBody EngineCardModel cardModel,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.updateCard(cardId, cardModel, tenantId, userId));
    }

    @Operation(summary = "Delete card", description = "Delete a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.deleteCard(cardId, tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Move card", description = "Move a card to another list with workflow validation. If approval is required, creates a pending approval instead of moving.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card moved successfully or approval created"),
            @ApiResponse(responseCode = "400", description = "Invalid transition or missing required comment"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/cards/{cardId}/move")
    public ResponseEntity<EngineCardModel> moveCard(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Valid @RequestBody MoveCardRequest request,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.moveCard(cardId, request, tenantId, userId));
    }

    @Operation(summary = "Search cards", description = "Search and filter cards in a board by multiple criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @GetMapping("/boards/{boardId}/cards/search")
    public ResponseEntity<List<EngineCardModel>> searchCards(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @Parameter(description = "Assignee User ID") @RequestParam(required = false) UUID assigneeUserId,
            @Parameter(description = "Priority title (e.g., HIGH, MEDIUM, LOW)") @RequestParam(required = false) String priority,
            @Parameter(description = "Due date from (inclusive)") @RequestParam(required = false) LocalDate dueDateFrom,
            @Parameter(description = "Due date to (inclusive)") @RequestParam(required = false) LocalDate dueDateTo,
            @Parameter(description = "Completed status") @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Search text in title/description") @RequestParam(required = false) String search,
            @Parameter(description = "Label ID") @RequestParam(required = false) UUID labelId,
            @Parameter(description = "Source service filter") @RequestParam(required = false) String sourceService,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.searchCards(
                boardId, tenantId, assigneeUserId, priority, dueDateFrom, dueDateTo, completed, search, labelId, sourceService
        ));
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.addComment(cardId, request.text(), tenantId, userId));
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.updateComment(cardId, commentId, request.text(), tenantId, userId));
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.deleteComment(cardId, commentId, tenantId, userId);
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.addSubTask(cardId, subTaskModel, tenantId, userId));
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.updateSubTask(cardId, subtaskId, subTaskModel, tenantId, userId));
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.deleteSubTask(cardId, subtaskId, tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Label Operations ====================

    @Operation(summary = "Add label to card", description = "Assign a label to a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Card or label not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/cards/{cardId}/labels/{labelId}")
    public ResponseEntity<Void> addLabelToCard(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Label ID") @PathVariable UUID labelId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.addLabelToCard(cardId, labelId, tenantId, userId);
        return ResponseEntity.ok().build();
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.removeLabelFromCard(cardId, labelId, tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get board labels", description = "Get all labels for a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Labels retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Board not found")
    })
    @GetMapping("/boards/{boardId}/labels")
    public ResponseEntity<List<KanbanLabelModel>> getBoardLabels(
            @Parameter(description = "Board ID") @PathVariable UUID boardId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.getBoardLabels(boardId, tenantId));
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.createLabel(boardId, request.name(), request.color(), tenantId, userId));
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.updateLabel(boardId, labelId, request.name(), request.color(), tenantId, userId));
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
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.deleteLabel(boardId, labelId, tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Attachment Operations ====================

    @Operation(summary = "List attachments", description = "Get all attachments for a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachments retrieved successfully")
    })
    @GetMapping("/cards/{cardId}/attachments")
    public ResponseEntity<List<KanbanAttachmentModel>> listAttachments(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.getAttachments(cardId, tenantId));
    }

    @Operation(summary = "Upload attachment", description = "Upload a file attachment to a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment uploaded successfully")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping(value = "/cards/{cardId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KanbanAttachmentModel> uploadAttachment(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.uploadAttachment(cardId, file, tenantId, userId));
    }

    @Operation(summary = "Download attachment", description = "Download a file attachment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Attachment not found")
    })
    @GetMapping("/cards/{cardId}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Attachment ID") @PathVariable UUID attachmentId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        Resource resource = engineService.downloadAttachment(cardId, attachmentId, tenantId);
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
            @Parameter(description = "Attachment ID") @PathVariable UUID attachmentId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.deleteAttachment(cardId, attachmentId, tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Assignee Operations ====================

    @Operation(summary = "Add assignee to card", description = "Assign a user to a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignee added successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/cards/{cardId}/assignees")
    public ResponseEntity<Void> addAssignee(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Valid @RequestBody AssigneeRequest request,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.addAssignee(cardId, request.userId(), tenantId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove assignee from card", description = "Remove a user assignment from a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assignee removed successfully"),
            @ApiResponse(responseCode = "404", description = "Card or assignee not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @DeleteMapping("/cards/{cardId}/assignees/{assigneeUserId}")
    public ResponseEntity<Void> removeAssignee(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @Parameter(description = "Assignee User ID") @PathVariable UUID assigneeUserId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        engineService.removeAssignee(cardId, assigneeUserId, tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== History Operations ====================

    @Operation(summary = "Get card history", description = "Get the complete change history of a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/cards/{cardId}/history")
    public ResponseEntity<List<CardHistoryModel>> getCardHistory(
            @Parameter(description = "Card ID") @PathVariable UUID cardId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.getCardHistory(cardId, tenantId));
    }

    // ==================== Approval Operations ====================

    @Operation(summary = "List approvals", description = "Get approvals filtered by status, board, or card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approvals retrieved successfully")
    })
    @GetMapping("/approvals")
    public ResponseEntity<List<ApprovalModel>> listApprovals(
            @Parameter(description = "Approval status filter (PENDING, APPROVED, REJECTED)") @RequestParam(required = false) String status,
            @Parameter(description = "Board ID filter") @RequestParam(required = false) UUID boardId,
            @Parameter(description = "Card ID filter") @RequestParam(required = false) UUID cardId,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.getApprovals(tenantId, status, boardId, cardId));
    }

    @Operation(summary = "Count approvals", description = "Get the count of approvals by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    @GetMapping("/approvals/count")
    public ResponseEntity<CountModel> countApprovals(
            @Parameter(description = "Approval status (default: PENDING)") @RequestParam(defaultValue = "PENDING") String status,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        return ResponseEntity.ok(engineService.countApprovals(tenantId, status));
    }

    @Operation(summary = "Approve card transition", description = "Approve a pending card transition. Card will be moved to the target list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approval processed successfully"),
            @ApiResponse(responseCode = "400", description = "Approval is not in PENDING status"),
            @ApiResponse(responseCode = "404", description = "Approval not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/approvals/{approvalId}/approve")
    public ResponseEntity<ApprovalModel> approveCard(
            @Parameter(description = "Approval ID") @PathVariable UUID approvalId,
            @RequestBody(required = false) ApprovalActionRequest request,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        String comment = request != null ? request.comment() : null;
        return ResponseEntity.ok(engineService.approveCard(approvalId, comment, tenantId, userId));
    }

    @Operation(summary = "Reject card transition", description = "Reject a pending card transition. Card stays in its current list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rejection processed successfully"),
            @ApiResponse(responseCode = "400", description = "Approval is not in PENDING status or comment is missing"),
            @ApiResponse(responseCode = "404", description = "Approval not found")
    })
    @RequiresPermission("KANBAN_MANAGE")
    @PostMapping("/approvals/{approvalId}/reject")
    public ResponseEntity<ApprovalModel> rejectCard(
            @Parameter(description = "Approval ID") @PathVariable UUID approvalId,
            @Valid @RequestBody ApprovalActionRequest request,
            @RequestHeader("X-Tenant-Id") String tenantIdHeader,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID tenantId = UUID.fromString(tenantIdHeader);
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(engineService.rejectCard(approvalId, request.comment(), tenantId, userId));
    }

    // ==================== Request DTOs ====================

    public record CreateCommentRequest(
            @NotBlank(message = "Comment text is required")
            String text
    ) {}

    public record CreateLabelRequest(
            @NotBlank(message = "Label name is required")
            String name,
            String color
    ) {}

    public record AssigneeRequest(
            UUID userId
    ) {}

    public record ApprovalActionRequest(
            String comment
    ) {}
}
