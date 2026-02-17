package com.poc.kanban.integration;

import com.poc.kanban.model.*;
import com.poc.kanban.service.EngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for the Kanban Engine API.
 * Uses H2 in-memory database with Liquibase for schema creation and seed data.
 * Board types, workflow steps, transitions, and approval rules are seeded by Liquibase.
 */
@DisplayName("Engine Integration Tests")
class EngineIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EngineService engineService;

    // Tenant and user IDs for multi-tenant isolation
    private static final UUID TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    // HELPDESK workflow step IDs from Liquibase seed data (002-seed-engine-board-types.sql)
    private static final String HELPDESK_TYPE = "HELPDESK";
    private static final UUID STEP_NEW = UUID.fromString("00000000-0000-0000-1001-000000000001");
    private static final UUID STEP_OPEN = UUID.fromString("00000000-0000-0000-1001-000000000002");
    private static final UUID STEP_PENDING = UUID.fromString("00000000-0000-0000-1001-000000000003");
    private static final UUID STEP_RESOLVED = UUID.fromString("00000000-0000-0000-1001-000000000005");
    private static final UUID STEP_CLOSED = UUID.fromString("00000000-0000-0000-1001-000000000006");

    // ==================== Board Type Tests ====================

    @Nested
    @DisplayName("Board Types")
    class BoardTypeTests {

        @Test
        @DisplayName("Should list all seeded board types")
        void shouldListAllBoardTypes() {
            // When
            List<BoardTypeModel> boardTypes = engineService.getBoardTypes();

            // Then
            assertThat(boardTypes).isNotEmpty();
            assertThat(boardTypes)
                    .extracting(BoardTypeModel::code)
                    .contains("KANBAN", "HELPDESK", "GTD", "FINANCIAL");
        }

        @Test
        @DisplayName("Should get HELPDESK board type with features")
        void shouldGetHelpdeskBoardType() {
            // When
            BoardTypeModel helpdesk = engineService.getBoardType(HELPDESK_TYPE);

            // Then
            assertThat(helpdesk).isNotNull();
            assertThat(helpdesk.code()).isEqualTo(HELPDESK_TYPE);
            assertThat(helpdesk.name()).isEqualTo("Help Desk");
            assertThat(helpdesk.singleton()).isTrue();
            assertThat(helpdesk.scope()).isEqualTo("TENANT");
            assertThat(helpdesk.numberPrefix()).isEqualTo("HD");
            assertThat(helpdesk.allowCustomLists()).isFalse();
            // HELPDESK has COMMENTS, LABELS, ATTACHMENTS, ASSIGNEES, DUE_DATE, PRIORITY, HISTORY, CARD_NUMBER, DESCRIPTION enabled
            assertThat(helpdesk.enabledFeatures()).contains("COMMENTS", "LABELS", "ATTACHMENTS", "CARD_NUMBER");
            // SUBTASKS and PROGRESS are disabled for HELPDESK
            assertThat(helpdesk.enabledFeatures()).doesNotContain("SUBTASKS", "PROGRESS");
        }

        @Test
        @DisplayName("Should throw when getting non-existent board type")
        void shouldThrowWhenBoardTypeNotFound() {
            // When/Then
            assertThatThrownBy(() -> engineService.getBoardType("NONEXISTENT"))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board type not found");
        }

        @Test
        @DisplayName("Should get HELPDESK workflow with steps and transitions")
        void shouldGetHelpdeskWorkflow() {
            // When
            WorkflowModel workflow = engineService.getWorkflow(HELPDESK_TYPE);

            // Then
            assertThat(workflow).isNotNull();
            assertThat(workflow.boardTypeCode()).isEqualTo(HELPDESK_TYPE);
            assertThat(workflow.steps()).hasSize(6);
            assertThat(workflow.steps())
                    .extracting(WorkflowStepModel::stepCode)
                    .containsExactly("NEW", "OPEN", "PENDING", "ON_HOLD", "RESOLVED", "CLOSED");

            // NEW should be initial step
            WorkflowStepModel newStep = workflow.steps().stream()
                    .filter(s -> "NEW".equals(s.stepCode())).findFirst().orElseThrow();
            assertThat(newStep.isInitial()).isTrue();
            assertThat(newStep.isFinal()).isFalse();

            // CLOSED should be final step
            WorkflowStepModel closedStep = workflow.steps().stream()
                    .filter(s -> "CLOSED".equals(s.stepCode())).findFirst().orElseThrow();
            assertThat(closedStep.isFinal()).isTrue();

            // Should have transitions
            assertThat(workflow.transitions()).isNotEmpty();
            // RESOLVED -> CLOSED requires approval
            WorkflowTransitionModel resolvedToClosed = workflow.transitions().stream()
                    .filter(t -> "RESOLVED".equals(t.fromStepCode()) && "CLOSED".equals(t.toStepCode()))
                    .findFirst().orElseThrow();
            assertThat(resolvedToClosed.requiresApproval()).isTrue();
        }
    }

    // ==================== Board CRUD Tests ====================

    @Nested
    @DisplayName("Board CRUD")
    class BoardCrudTests {

        @Test
        @DisplayName("Should create HELPDESK board with workflow lists")
        void shouldCreateHelpdeskBoard() {
            // Given
            CreateEngineBoardRequest request = new CreateEngineBoardRequest(HELPDESK_TYPE, "My Helpdesk", null);

            // When
            EngineBoardModel board = engineService.createBoard(request, TENANT_ID, USER_ID);

            // Then
            assertThat(board).isNotNull();
            assertThat(board.id()).isNotNull();
            assertThat(board.title()).isEqualTo("My Helpdesk");
            assertThat(board.boardTypeCode()).isEqualTo(HELPDESK_TYPE);
            assertThat(board.tenantId()).isEqualTo(TENANT_ID);
            assertThat(board.numberPrefix()).isEqualTo("HD");
            // Lists should be created from workflow steps
            assertThat(board.lists()).hasSize(6);
            assertThat(board.lists())
                    .extracting(EngineBoardModel.EngineBoardListModel::title)
                    .containsExactly("New", "Open", "Pending", "On Hold", "Resolved", "Closed");
        }

        @Test
        @DisplayName("Should return existing board for singleton board type")
        void shouldReturnExistingForSingleton() {
            // Given - Create the first board
            CreateEngineBoardRequest request = new CreateEngineBoardRequest(HELPDESK_TYPE, "First Board", null);
            EngineBoardModel firstBoard = engineService.createBoard(request, TENANT_ID, USER_ID);

            // When - Try to create another board of the same singleton type
            CreateEngineBoardRequest secondRequest = new CreateEngineBoardRequest(HELPDESK_TYPE, "Second Board", null);
            EngineBoardModel secondBoard = engineService.createBoard(secondRequest, TENANT_ID, USER_ID);

            // Then - Should return the same board
            assertThat(secondBoard.id()).isEqualTo(firstBoard.id());
            assertThat(secondBoard.title()).isEqualTo("First Board");
        }

        @Test
        @DisplayName("Should get board by id")
        void shouldGetBoardById() {
            // Given
            CreateEngineBoardRequest request = new CreateEngineBoardRequest(HELPDESK_TYPE, "Test Board", null);
            EngineBoardModel created = engineService.createBoard(request, TENANT_ID, USER_ID);

            // When
            EngineBoardModel fetched = engineService.getBoard(created.id(), TENANT_ID);

            // Then
            assertThat(fetched).isNotNull();
            assertThat(fetched.id()).isEqualTo(created.id());
            assertThat(fetched.title()).isEqualTo("Test Board");
        }

        @Test
        @DisplayName("Should list boards by tenant")
        void shouldListBoardsByTenant() {
            // Given - Create a KANBAN board (not singleton)
            CreateEngineBoardRequest request = new CreateEngineBoardRequest("KANBAN", "Team Board", "TEAM");
            engineService.createBoard(request, TENANT_ID, USER_ID);

            // When
            List<EngineBoardModel> boards = engineService.getBoards(TENANT_ID, null);

            // Then
            assertThat(boards).isNotEmpty();
            assertThat(boards).extracting(EngineBoardModel::tenantId)
                    .containsOnly(TENANT_ID);
        }

        @Test
        @DisplayName("Should list boards filtered by type")
        void shouldListBoardsFilteredByType() {
            // Given
            CreateEngineBoardRequest helpdeskReq = new CreateEngineBoardRequest(HELPDESK_TYPE, "HD Board", null);
            engineService.createBoard(helpdeskReq, TENANT_ID, USER_ID);

            CreateEngineBoardRequest kanbanReq = new CreateEngineBoardRequest("KANBAN", "KB Board", "KB");
            engineService.createBoard(kanbanReq, TENANT_ID, USER_ID);

            // When
            List<EngineBoardModel> helpdeskBoards = engineService.getBoards(TENANT_ID, HELPDESK_TYPE);

            // Then
            assertThat(helpdeskBoards).allMatch(b -> HELPDESK_TYPE.equals(b.boardTypeCode()));
        }

        @Test
        @DisplayName("Should update board title")
        void shouldUpdateBoardTitle() {
            // Given
            CreateEngineBoardRequest request = new CreateEngineBoardRequest(HELPDESK_TYPE, "Original Title", null);
            EngineBoardModel created = engineService.createBoard(request, TENANT_ID, USER_ID);

            EngineBoardModel updateModel = new EngineBoardModel(
                    null, "Updated Title", null, null, null, null,
                    null, null, null, null, null, null, null);

            // When
            EngineBoardModel updated = engineService.updateBoard(created.id(), updateModel, TENANT_ID, USER_ID);

            // Then
            assertThat(updated.title()).isEqualTo("Updated Title");
        }

        @Test
        @DisplayName("Should delete board")
        void shouldDeleteBoard() {
            // Given
            CreateEngineBoardRequest request = new CreateEngineBoardRequest("KANBAN", "To Delete", "DEL");
            EngineBoardModel created = engineService.createBoard(request, TENANT_ID, USER_ID);

            // When
            engineService.deleteBoard(created.id(), TENANT_ID, USER_ID);

            // Then
            assertThatThrownBy(() -> engineService.getBoard(created.id(), TENANT_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board not found");
        }

        @Test
        @DisplayName("Should find board by type and tenant")
        void shouldFindBoardByType() {
            // Given
            CreateEngineBoardRequest request = new CreateEngineBoardRequest(HELPDESK_TYPE, "Find Me", null);
            EngineBoardModel created = engineService.createBoard(request, TENANT_ID, USER_ID);

            // When
            EngineBoardModel found = engineService.findBoard(HELPDESK_TYPE, TENANT_ID, null);

            // Then
            assertThat(found).isNotNull();
            assertThat(found.id()).isEqualTo(created.id());
        }
    }

    // ==================== Card CRUD and Workflow Tests ====================

    @Nested
    @DisplayName("Card Operations")
    class CardTests {

        private UUID boardId;
        private UUID newListId;
        private UUID openListId;

        @BeforeEach
        void setUpBoard() {
            CreateEngineBoardRequest request = new CreateEngineBoardRequest(HELPDESK_TYPE, "Test HD", null);
            EngineBoardModel board = engineService.createBoard(request, TENANT_ID, USER_ID);
            boardId = board.id();

            // Find list IDs from the seeded workflow steps
            newListId = board.lists().stream()
                    .filter(l -> "New".equals(l.title()))
                    .map(EngineBoardModel.EngineBoardListModel::id)
                    .findFirst().orElseThrow();
            openListId = board.lists().stream()
                    .filter(l -> "Open".equals(l.title()))
                    .map(EngineBoardModel.EngineBoardListModel::id)
                    .findFirst().orElseThrow();
        }

        @Test
        @DisplayName("Should create card on initial step")
        void shouldCreateCard() {
            // Given
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Bug Report", "Description of the bug",
                    "#FF0000", "HIGH",
                    LocalDate.now(), LocalDate.now().plusDays(7),
                    null, null, "helpdesk-service");

            // When
            EngineCardModel card = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            // Then
            assertThat(card).isNotNull();
            assertThat(card.id()).isNotNull();
            assertThat(card.title()).isEqualTo("Bug Report");
            assertThat(card.description()).isEqualTo("Description of the bug");
            assertThat(card.completed()).isFalse();
            assertThat(card.progress()).isEqualTo(0);
            assertThat(card.listId()).isEqualTo(newListId);
            assertThat(card.cardCode()).startsWith("HD-");
            assertThat(card.sourceService()).isEqualTo("helpdesk-service");
            assertThat(card.priority()).isNotNull();
            assertThat(card.priority().getTitle()).isEqualTo("HIGH");
        }

        @Test
        @DisplayName("Should create card with target step code")
        void shouldCreateCardWithTargetStep() {
            // Given
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Pre-opened ticket", null, null, null,
                    null, null, "OPEN", null, null);

            // When
            EngineCardModel card = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            // Then
            assertThat(card).isNotNull();
            assertThat(card.listId()).isEqualTo(openListId);
        }

        @Test
        @DisplayName("Should create card with target list id")
        void shouldCreateCardWithTargetListId() {
            // Given
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Direct to open", null, null, null,
                    null, null, null, openListId, null);

            // When
            EngineCardModel card = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            // Then
            assertThat(card).isNotNull();
            assertThat(card.listId()).isEqualTo(openListId);
        }

        @Test
        @DisplayName("Should assign sequential card numbers")
        void shouldAssignSequentialCardNumbers() {
            // Given
            CreateEngineCardRequest request1 = new CreateEngineCardRequest(
                    "First", null, null, null, null, null, null, null, null);
            CreateEngineCardRequest request2 = new CreateEngineCardRequest(
                    "Second", null, null, null, null, null, null, null, null);

            // When
            EngineCardModel card1 = engineService.createCard(boardId, request1, TENANT_ID, USER_ID);
            EngineCardModel card2 = engineService.createCard(boardId, request2, TENANT_ID, USER_ID);

            // Then
            assertThat(card1.cardCode()).isEqualTo("HD-0001");
            assertThat(card2.cardCode()).isEqualTo("HD-0002");
        }

        @Test
        @DisplayName("Should get card by id")
        void shouldGetCardById() {
            // Given
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Test Card", "Test Desc", null, null, null, null, null, null, null);
            EngineCardModel created = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            // When
            EngineCardModel fetched = engineService.getCard(created.id(), TENANT_ID);

            // Then
            assertThat(fetched).isNotNull();
            assertThat(fetched.id()).isEqualTo(created.id());
            assertThat(fetched.title()).isEqualTo("Test Card");
        }

        @Test
        @DisplayName("Should get card by code")
        void shouldGetCardByCode() {
            // Given
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Coded Card", null, null, null, null, null, null, null, null);
            EngineCardModel created = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            // When
            EngineCardModel fetched = engineService.getCardByCode(created.cardCode(), TENANT_ID);

            // Then
            assertThat(fetched).isNotNull();
            assertThat(fetched.id()).isEqualTo(created.id());
        }

        @Test
        @DisplayName("Should update card")
        void shouldUpdateCard() {
            // Given
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Original", "Desc", null, null, null, null, null, null, null);
            EngineCardModel created = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            EngineCardModel updateModel = new EngineCardModel(
                    null, null, "Updated Title", "Updated Desc",
                    LocalDate.now(), LocalDate.now().plusDays(14),
                    true, 50, null, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null, null, null, null, null);

            // When
            EngineCardModel updated = engineService.updateCard(created.id(), updateModel, TENANT_ID, USER_ID);

            // Then
            assertThat(updated.title()).isEqualTo("Updated Title");
            assertThat(updated.description()).isEqualTo("Updated Desc");
            assertThat(updated.completed()).isTrue();
            assertThat(updated.progress()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should delete card")
        void shouldDeleteCard() {
            // Given
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "To Delete", null, null, null, null, null, null, null, null);
            EngineCardModel created = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            // When
            engineService.deleteCard(created.id(), TENANT_ID, USER_ID);

            // Then
            assertThatThrownBy(() -> engineService.getCard(created.id(), TENANT_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Card not found");
        }

        @Test
        @DisplayName("Should move card between allowed lists")
        void shouldMoveCardBetweenAllowedLists() {
            // Given - Create card in NEW list
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Movable Card", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, request, TENANT_ID, USER_ID);
            assertThat(card.listId()).isEqualTo(newListId);

            // When - Move from NEW to OPEN (allowed transition)
            MoveCardRequest moveRequest = new MoveCardRequest(openListId, null, null, null);
            EngineCardModel moved = engineService.moveCard(card.id(), moveRequest, TENANT_ID, USER_ID);

            // Then
            assertThat(moved.listId()).isEqualTo(openListId);
        }

        @Test
        @DisplayName("Should move card using step code")
        void shouldMoveCardUsingStepCode() {
            // Given - Create card in NEW list
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Step Code Move", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            // When - Move from NEW to OPEN using step code
            MoveCardRequest moveRequest = new MoveCardRequest(null, "OPEN", null, null);
            EngineCardModel moved = engineService.moveCard(card.id(), moveRequest, TENANT_ID, USER_ID);

            // Then
            assertThat(moved.listId()).isEqualTo(openListId);
            assertThat(moved.stepCode()).isEqualTo("OPEN");
        }

        @Test
        @DisplayName("Should reject invalid transition")
        void shouldRejectInvalidTransition() {
            // Given - Create card in NEW list
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Invalid Move", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            // Get the Closed list ID
            EngineBoardModel board = engineService.getBoard(boardId, TENANT_ID);
            UUID closedListId = board.lists().stream()
                    .filter(l -> "Closed".equals(l.title()))
                    .map(EngineBoardModel.EngineBoardListModel::id)
                    .findFirst().orElseThrow();

            // When/Then - Try to move from NEW directly to CLOSED (not allowed)
            MoveCardRequest moveRequest = new MoveCardRequest(closedListId, null, null, null);
            assertThatThrownBy(() -> engineService.moveCard(card.id(), moveRequest, TENANT_ID, USER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("is not allowed");
        }

        @Test
        @DisplayName("Should create approval when transition requires it")
        void shouldCreateApprovalWhenRequired() {
            // Given - Create card and move it through workflow to RESOLVED
            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Approval Card", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, request, TENANT_ID, USER_ID);

            // Move: NEW -> OPEN
            MoveCardRequest toOpen = new MoveCardRequest(null, "OPEN", null, null);
            card = engineService.moveCard(card.id(), toOpen, TENANT_ID, USER_ID);

            // Move: OPEN -> RESOLVED
            MoveCardRequest toResolved = new MoveCardRequest(null, "RESOLVED", null, null);
            card = engineService.moveCard(card.id(), toResolved, TENANT_ID, USER_ID);

            // When - Move: RESOLVED -> CLOSED (requires approval)
            MoveCardRequest toClosed = new MoveCardRequest(null, "CLOSED", null, null);
            card = engineService.moveCard(card.id(), toClosed, TENANT_ID, USER_ID);

            // Then - Card should be pending approval, not yet moved
            assertThat(card.pendingApproval()).isTrue();
            assertThat(card.pendingApprovalId()).isNotNull();
            assertThat(card.stepCode()).isEqualTo("RESOLVED"); // still in Resolved

            // Verify approval was created
            List<ApprovalModel> approvals = engineService.getApprovals(TENANT_ID, "PENDING", boardId, null);
            assertThat(approvals).isNotEmpty();
            assertThat(approvals.get(0).status()).isEqualTo("PENDING");
        }
    }

    // ==================== Comments Tests ====================

    @Nested
    @DisplayName("Comments via Engine")
    class CommentTests {

        private UUID boardId;
        private UUID cardId;

        @BeforeEach
        void setUpCard() {
            CreateEngineBoardRequest boardReq = new CreateEngineBoardRequest(HELPDESK_TYPE, "Comment Board", null);
            EngineBoardModel board = engineService.createBoard(boardReq, TENANT_ID, USER_ID);
            boardId = board.id();

            CreateEngineCardRequest cardReq = new CreateEngineCardRequest(
                    "Comment Card", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, cardReq, TENANT_ID, USER_ID);
            cardId = card.id();
        }

        @Test
        @DisplayName("Should add comment to card")
        void shouldAddComment() {
            // When
            KanbanCommentModel comment = engineService.addComment(cardId, "Test comment", TENANT_ID, USER_ID);

            // Then
            assertThat(comment).isNotNull();
            assertThat(comment.getId()).isNotNull();
            assertThat(comment.getText()).isEqualTo("Test comment");
            assertThat(comment.getUserId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("Should update comment")
        void shouldUpdateComment() {
            // Given
            KanbanCommentModel created = engineService.addComment(cardId, "Original", TENANT_ID, USER_ID);

            // When
            KanbanCommentModel updated = engineService.updateComment(
                    cardId, created.getId(), "Updated comment", TENANT_ID, USER_ID);

            // Then
            assertThat(updated.getText()).isEqualTo("Updated comment");
        }

        @Test
        @DisplayName("Should delete comment")
        void shouldDeleteComment() {
            // Given
            KanbanCommentModel comment = engineService.addComment(cardId, "Delete me", TENANT_ID, USER_ID);

            // When
            engineService.deleteComment(cardId, comment.getId(), TENANT_ID, USER_ID);

            // Then - Card should have no comments
            EngineCardModel card = engineService.getCard(cardId, TENANT_ID);
            assertThat(card.comments()).isEmpty();
        }

        @Test
        @DisplayName("Should throw when updating non-existent comment")
        void shouldThrowWhenUpdatingNonExistentComment() {
            UUID nonExistentId = UUID.randomUUID();
            assertThatThrownBy(() ->
                    engineService.updateComment(cardId, nonExistentId, "Text", TENANT_ID, USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Comment not found");
        }
    }

    // ==================== SubTask Tests ====================

    @Nested
    @DisplayName("SubTasks via Engine")
    class SubTaskTests {

        private UUID boardId;
        private UUID cardId;

        @BeforeEach
        void setUpCard() {
            CreateEngineBoardRequest boardReq = new CreateEngineBoardRequest(HELPDESK_TYPE, "SubTask Board", null);
            EngineBoardModel board = engineService.createBoard(boardReq, TENANT_ID, USER_ID);
            boardId = board.id();

            CreateEngineCardRequest cardReq = new CreateEngineCardRequest(
                    "SubTask Card", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, cardReq, TENANT_ID, USER_ID);
            cardId = card.id();
        }

        @Test
        @DisplayName("Should add subtask to card")
        void shouldAddSubTask() {
            // Given
            KanbanSubTaskModel subTask = KanbanSubTaskModel.builder()
                    .text("Check logs")
                    .completed(false)
                    .dueDate(LocalDate.now().plusDays(3))
                    .build();

            // When
            KanbanSubTaskModel created = engineService.addSubTask(cardId, subTask, TENANT_ID, USER_ID);

            // Then
            assertThat(created).isNotNull();
            assertThat(created.getId()).isNotNull();
            assertThat(created.getText()).isEqualTo("Check logs");
            assertThat(created.getCompleted()).isFalse();
            assertThat(created.getPosition()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should update subtask")
        void shouldUpdateSubTask() {
            // Given
            KanbanSubTaskModel subTask = KanbanSubTaskModel.builder()
                    .text("Original").completed(false).build();
            KanbanSubTaskModel created = engineService.addSubTask(cardId, subTask, TENANT_ID, USER_ID);

            // When
            KanbanSubTaskModel updateModel = KanbanSubTaskModel.builder()
                    .text("Updated").completed(true).build();
            KanbanSubTaskModel updated = engineService.updateSubTask(
                    cardId, created.getId(), updateModel, TENANT_ID, USER_ID);

            // Then
            assertThat(updated.getText()).isEqualTo("Updated");
            assertThat(updated.getCompleted()).isTrue();
        }

        @Test
        @DisplayName("Should delete subtask and reindex positions")
        void shouldDeleteSubTaskAndReindex() {
            // Given
            KanbanSubTaskModel st1 = KanbanSubTaskModel.builder().text("ST1").completed(false).build();
            KanbanSubTaskModel st2 = KanbanSubTaskModel.builder().text("ST2").completed(false).build();
            KanbanSubTaskModel st3 = KanbanSubTaskModel.builder().text("ST3").completed(false).build();

            KanbanSubTaskModel c1 = engineService.addSubTask(cardId, st1, TENANT_ID, USER_ID);
            KanbanSubTaskModel c2 = engineService.addSubTask(cardId, st2, TENANT_ID, USER_ID);
            KanbanSubTaskModel c3 = engineService.addSubTask(cardId, st3, TENANT_ID, USER_ID);

            // When - Delete middle subtask
            engineService.deleteSubTask(cardId, c2.getId(), TENANT_ID, USER_ID);

            // Then - Card should have 2 subtasks with reindexed positions
            EngineCardModel card = engineService.getCard(cardId, TENANT_ID);
            assertThat(card.subtasks()).hasSize(2);
            assertThat(card.subtasks().get(0).getPosition()).isEqualTo(0);
            assertThat(card.subtasks().get(1).getPosition()).isEqualTo(1);
        }
    }

    // ==================== Label Tests ====================

    @Nested
    @DisplayName("Labels via Engine")
    class LabelTests {

        private UUID boardId;
        private UUID cardId;

        @BeforeEach
        void setUpCard() {
            CreateEngineBoardRequest boardReq = new CreateEngineBoardRequest(HELPDESK_TYPE, "Label Board", null);
            EngineBoardModel board = engineService.createBoard(boardReq, TENANT_ID, USER_ID);
            boardId = board.id();

            CreateEngineCardRequest cardReq = new CreateEngineCardRequest(
                    "Label Card", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, cardReq, TENANT_ID, USER_ID);
            cardId = card.id();
        }

        @Test
        @DisplayName("Should create label on board")
        void shouldCreateLabel() {
            // When
            KanbanLabelModel label = engineService.createLabel(boardId, "Bug", "#FF0000", TENANT_ID, USER_ID);

            // Then
            assertThat(label).isNotNull();
            assertThat(label.getId()).isNotNull();
            assertThat(label.getName()).isEqualTo("Bug");
            assertThat(label.getColor()).isEqualTo("#FF0000");
        }

        @Test
        @DisplayName("Should get board labels")
        void shouldGetBoardLabels() {
            // Given
            engineService.createLabel(boardId, "Bug", "#FF0000", TENANT_ID, USER_ID);
            engineService.createLabel(boardId, "Feature", "#00FF00", TENANT_ID, USER_ID);

            // When
            List<KanbanLabelModel> labels = engineService.getBoardLabels(boardId, TENANT_ID);

            // Then
            assertThat(labels).hasSize(2);
            assertThat(labels).extracting(KanbanLabelModel::getName).containsExactlyInAnyOrder("Bug", "Feature");
        }

        @Test
        @DisplayName("Should add and remove label from card")
        void shouldAddAndRemoveLabelFromCard() {
            // Given
            KanbanLabelModel label = engineService.createLabel(boardId, "Urgent", "#FF0000", TENANT_ID, USER_ID);

            // When - Add label
            engineService.addLabelToCard(cardId, label.getId(), TENANT_ID, USER_ID);

            // Then - Card should have label
            EngineCardModel cardWithLabel = engineService.getCard(cardId, TENANT_ID);
            assertThat(cardWithLabel.labels()).hasSize(1);
            assertThat(cardWithLabel.labels().get(0).getName()).isEqualTo("Urgent");

            // When - Remove label
            engineService.removeLabelFromCard(cardId, label.getId(), TENANT_ID, USER_ID);

            // Then - Card should have no labels
            EngineCardModel cardWithoutLabel = engineService.getCard(cardId, TENANT_ID);
            assertThat(cardWithoutLabel.labels()).isEmpty();
        }

        @Test
        @DisplayName("Should prevent duplicate label on card")
        void shouldPreventDuplicateLabel() {
            // Given
            KanbanLabelModel label = engineService.createLabel(boardId, "Bug", "#FF0000", TENANT_ID, USER_ID);
            engineService.addLabelToCard(cardId, label.getId(), TENANT_ID, USER_ID);

            // When/Then
            assertThatThrownBy(() -> engineService.addLabelToCard(cardId, label.getId(), TENANT_ID, USER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already assigned");
        }

        @Test
        @DisplayName("Should delete label and remove from cards")
        void shouldDeleteLabelAndRemoveFromCards() {
            // Given
            KanbanLabelModel label = engineService.createLabel(boardId, "ToDelete", "#AABB00", TENANT_ID, USER_ID);
            engineService.addLabelToCard(cardId, label.getId(), TENANT_ID, USER_ID);

            // When
            engineService.deleteLabel(boardId, label.getId(), TENANT_ID, USER_ID);

            // Then - Board has no labels, card has no labels
            List<KanbanLabelModel> labels = engineService.getBoardLabels(boardId, TENANT_ID);
            assertThat(labels).isEmpty();

            EngineCardModel card = engineService.getCard(cardId, TENANT_ID);
            assertThat(card.labels()).isEmpty();
        }
    }

    // ==================== Assignee Tests ====================

    @Nested
    @DisplayName("Assignees via Engine")
    class AssigneeTests {

        private UUID boardId;
        private UUID cardId;

        @BeforeEach
        void setUpCard() {
            CreateEngineBoardRequest boardReq = new CreateEngineBoardRequest(HELPDESK_TYPE, "Assignee Board", null);
            EngineBoardModel board = engineService.createBoard(boardReq, TENANT_ID, USER_ID);
            boardId = board.id();

            CreateEngineCardRequest cardReq = new CreateEngineCardRequest(
                    "Assignee Card", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, cardReq, TENANT_ID, USER_ID);
            cardId = card.id();
        }

        @Test
        @DisplayName("Should add and remove assignee")
        void shouldAddAndRemoveAssignee() {
            // Given
            UUID assigneeId = UUID.randomUUID();

            // When - Add assignee
            engineService.addAssignee(cardId, assigneeId, TENANT_ID, USER_ID);

            // Then
            EngineCardModel cardWithAssignee = engineService.getCard(cardId, TENANT_ID);
            assertThat(cardWithAssignee.assignees()).isNotEmpty();
            assertThat(cardWithAssignee.assignees().get(0).getUserId()).isEqualTo(assigneeId);

            // When - Remove assignee
            engineService.removeAssignee(cardId, assigneeId, TENANT_ID, USER_ID);

            // Then
            EngineCardModel cardWithoutAssignee = engineService.getCard(cardId, TENANT_ID);
            assertThat(cardWithoutAssignee.assignees()).isEmpty();
        }
    }

    // ==================== Search Tests ====================

    @Nested
    @DisplayName("Card Search")
    class SearchTests {

        private UUID boardId;

        @BeforeEach
        void setUpBoardWithCards() {
            CreateEngineBoardRequest boardReq = new CreateEngineBoardRequest(HELPDESK_TYPE, "Search Board", null);
            EngineBoardModel board = engineService.createBoard(boardReq, TENANT_ID, USER_ID);
            boardId = board.id();

            // Create multiple cards with different properties
            engineService.createCard(boardId, new CreateEngineCardRequest(
                    "Bug in login", "Login page error",
                    "#FF0000", "HIGH",
                    null, LocalDate.now().plusDays(3),
                    null, null, "helpdesk-service"), TENANT_ID, USER_ID);

            engineService.createCard(boardId, new CreateEngineCardRequest(
                    "Feature request", "New feature needed",
                    "#00FF00", "LOW",
                    null, LocalDate.now().plusDays(30),
                    null, null, "helpdesk-service"), TENANT_ID, USER_ID);

            engineService.createCard(boardId, new CreateEngineCardRequest(
                    "Urgent fix", "Needs immediate attention",
                    "#FF0000", "HIGH",
                    null, LocalDate.now().plusDays(1),
                    null, null, "chat-service"), TENANT_ID, USER_ID);
        }

        @Test
        @DisplayName("Should return all cards with no filters")
        void shouldReturnAllCards() {
            // When
            List<EngineCardModel> cards = engineService.searchCards(
                    boardId, TENANT_ID, null, null, null, null, null, null, null, null);

            // Then
            assertThat(cards).hasSize(3);
        }

        @Test
        @DisplayName("Should filter by search text")
        void shouldFilterBySearchText() {
            // When
            List<EngineCardModel> cards = engineService.searchCards(
                    boardId, TENANT_ID, null, null, null, null, null, "login", null, null);

            // Then
            assertThat(cards).hasSize(1);
            assertThat(cards.get(0).title()).isEqualTo("Bug in login");
        }

        @Test
        @DisplayName("Should filter by priority")
        void shouldFilterByPriority() {
            // When
            List<EngineCardModel> cards = engineService.searchCards(
                    boardId, TENANT_ID, null, "HIGH", null, null, null, null, null, null);

            // Then
            assertThat(cards).hasSize(2);
            assertThat(cards).allMatch(c -> "HIGH".equals(c.priority().getTitle()));
        }

        @Test
        @DisplayName("Should filter by source service")
        void shouldFilterBySourceService() {
            // When
            List<EngineCardModel> cards = engineService.searchCards(
                    boardId, TENANT_ID, null, null, null, null, null, null, null, "chat-service");

            // Then
            assertThat(cards).hasSize(1);
            assertThat(cards.get(0).title()).isEqualTo("Urgent fix");
        }

        @Test
        @DisplayName("Should filter by completed status")
        void shouldFilterByCompleted() {
            // When - All cards are not completed
            List<EngineCardModel> completedCards = engineService.searchCards(
                    boardId, TENANT_ID, null, null, null, null, true, null, null, null);

            // Then
            assertThat(completedCards).isEmpty();

            // When - Not completed
            List<EngineCardModel> notCompleted = engineService.searchCards(
                    boardId, TENANT_ID, null, null, null, null, false, null, null, null);

            // Then
            assertThat(notCompleted).hasSize(3);
        }

        @Test
        @DisplayName("Should filter by due date range")
        void shouldFilterByDueDateRange() {
            // When - Filter for cards due within next 5 days
            List<EngineCardModel> cards = engineService.searchCards(
                    boardId, TENANT_ID, null, null,
                    LocalDate.now(), LocalDate.now().plusDays(5),
                    null, null, null, null);

            // Then - Only 2 cards due within 5 days
            assertThat(cards).hasSize(2);
        }
    }

    // ==================== Approval Workflow Tests ====================

    @Nested
    @DisplayName("Approval Workflow")
    class ApprovalWorkflowTests {

        private UUID boardId;

        @BeforeEach
        void setUpBoard() {
            CreateEngineBoardRequest boardReq = new CreateEngineBoardRequest(HELPDESK_TYPE, "Approval Board", null);
            EngineBoardModel board = engineService.createBoard(boardReq, TENANT_ID, USER_ID);
            boardId = board.id();
        }

        private EngineCardModel moveCardToResolved(EngineCardModel card) {
            // NEW -> OPEN
            MoveCardRequest toOpen = new MoveCardRequest(null, "OPEN", null, null);
            card = engineService.moveCard(card.id(), toOpen, TENANT_ID, USER_ID);
            // OPEN -> RESOLVED
            MoveCardRequest toResolved = new MoveCardRequest(null, "RESOLVED", null, null);
            return engineService.moveCard(card.id(), toResolved, TENANT_ID, USER_ID);
        }

        @Test
        @DisplayName("Should approve card and move to target list")
        void shouldApproveAndMove() {
            // Given - Create card and move to RESOLVED
            CreateEngineCardRequest cardReq = new CreateEngineCardRequest(
                    "Approve Me", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, cardReq, TENANT_ID, USER_ID);
            card = moveCardToResolved(card);

            // Trigger approval (RESOLVED -> CLOSED requires approval)
            MoveCardRequest toClosed = new MoveCardRequest(null, "CLOSED", null, null);
            card = engineService.moveCard(card.id(), toClosed, TENANT_ID, USER_ID);
            assertThat(card.pendingApproval()).isTrue();

            UUID approvalId = card.pendingApprovalId();

            // When - Approve
            ApprovalModel approval = engineService.approveCard(approvalId, "Looks good", TENANT_ID, USER_ID);

            // Then
            assertThat(approval.status()).isEqualTo("APPROVED");
            assertThat(approval.comment()).isEqualTo("Looks good");

            // Card should now be in CLOSED list
            EngineCardModel movedCard = engineService.getCard(card.id(), TENANT_ID);
            assertThat(movedCard.pendingApproval()).isFalse();
            assertThat(movedCard.pendingApprovalId()).isNull();
            assertThat(movedCard.stepCode()).isEqualTo("CLOSED");
        }

        @Test
        @DisplayName("Should reject card and keep in original list")
        void shouldRejectAndKeepInPlace() {
            // Given - Create card and move to RESOLVED with pending approval
            CreateEngineCardRequest cardReq = new CreateEngineCardRequest(
                    "Reject Me", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, cardReq, TENANT_ID, USER_ID);
            card = moveCardToResolved(card);

            MoveCardRequest toClosed = new MoveCardRequest(null, "CLOSED", null, null);
            card = engineService.moveCard(card.id(), toClosed, TENANT_ID, USER_ID);
            UUID approvalId = card.pendingApprovalId();

            // When - Reject
            ApprovalModel rejection = engineService.rejectCard(approvalId, "Not ready yet", TENANT_ID, USER_ID);

            // Then
            assertThat(rejection.status()).isEqualTo("REJECTED");
            assertThat(rejection.comment()).isEqualTo("Not ready yet");

            // Card should remain in RESOLVED
            EngineCardModel stayedCard = engineService.getCard(card.id(), TENANT_ID);
            assertThat(stayedCard.pendingApproval()).isFalse();
            assertThat(stayedCard.stepCode()).isEqualTo("RESOLVED");
        }

        @Test
        @DisplayName("Should count pending approvals")
        void shouldCountPendingApprovals() {
            // Given - Create card with pending approval
            CreateEngineCardRequest cardReq = new CreateEngineCardRequest(
                    "Count Me", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, cardReq, TENANT_ID, USER_ID);
            card = moveCardToResolved(card);

            MoveCardRequest toClosed = new MoveCardRequest(null, "CLOSED", null, null);
            engineService.moveCard(card.id(), toClosed, TENANT_ID, USER_ID);

            // When
            CountModel count = engineService.countApprovals(TENANT_ID, "PENDING");

            // Then
            assertThat(count.count()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Should throw when rejecting without comment")
        void shouldThrowWhenRejectingWithoutComment() {
            // Given
            CreateEngineCardRequest cardReq = new CreateEngineCardRequest(
                    "No Comment", null, null, null, null, null, null, null, null);
            EngineCardModel card = engineService.createCard(boardId, cardReq, TENANT_ID, USER_ID);
            card = moveCardToResolved(card);

            MoveCardRequest toClosed = new MoveCardRequest(null, "CLOSED", null, null);
            card = engineService.moveCard(card.id(), toClosed, TENANT_ID, USER_ID);
            UUID approvalId = card.pendingApprovalId();

            // When/Then
            assertThatThrownBy(() -> engineService.rejectCard(approvalId, null, TENANT_ID, USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Comment is required for rejection");
        }
    }

    // ==================== Full E2E Flow Test ====================

    @Nested
    @DisplayName("Full End-to-End Flow")
    class E2ETests {

        @Test
        @DisplayName("Should complete full helpdesk ticket lifecycle")
        void shouldCompleteFullHelpdeskLifecycle() {
            // 1. Create board
            CreateEngineBoardRequest boardReq = new CreateEngineBoardRequest(HELPDESK_TYPE, "E2E Board", null);
            EngineBoardModel board = engineService.createBoard(boardReq, TENANT_ID, USER_ID);
            assertThat(board.lists()).hasSize(6);

            // 2. Create card
            CreateEngineCardRequest cardReq = new CreateEngineCardRequest(
                    "Customer Issue #42", "Customer cannot log in",
                    "#FF0000", "HIGH",
                    LocalDate.now(), LocalDate.now().plusDays(7),
                    null, null, "helpdesk-service");
            EngineCardModel card = engineService.createCard(board.id(), cardReq, TENANT_ID, USER_ID);
            assertThat(card.cardCode()).isEqualTo("HD-0001");
            assertThat(card.stepCode()).isEqualTo("NEW");

            // 3. Add label
            KanbanLabelModel urgentLabel = engineService.createLabel(board.id(), "Urgent", "#FF0000", TENANT_ID, USER_ID);
            engineService.addLabelToCard(card.id(), urgentLabel.getId(), TENANT_ID, USER_ID);

            // 4. Add comment
            KanbanCommentModel comment = engineService.addComment(card.id(), "Looking into this", TENANT_ID, USER_ID);
            assertThat(comment.getText()).isEqualTo("Looking into this");

            // 5. Add assignee
            UUID supportAgentId = UUID.randomUUID();
            engineService.addAssignee(card.id(), supportAgentId, TENANT_ID, USER_ID);

            // 6. Move through workflow: NEW -> OPEN -> RESOLVED
            MoveCardRequest toOpen = new MoveCardRequest(null, "OPEN", null, null);
            card = engineService.moveCard(card.id(), toOpen, TENANT_ID, USER_ID);
            assertThat(card.stepCode()).isEqualTo("OPEN");

            MoveCardRequest toResolved = new MoveCardRequest(null, "RESOLVED", null, null);
            card = engineService.moveCard(card.id(), toResolved, TENANT_ID, USER_ID);
            assertThat(card.stepCode()).isEqualTo("RESOLVED");

            // 7. Move RESOLVED -> CLOSED (triggers approval)
            MoveCardRequest toClosed = new MoveCardRequest(null, "CLOSED", null, null);
            card = engineService.moveCard(card.id(), toClosed, TENANT_ID, USER_ID);
            assertThat(card.pendingApproval()).isTrue();

            // 8. Approve the transition
            UUID approvalId = card.pendingApprovalId();
            ApprovalModel approval = engineService.approveCard(approvalId, "Verified fix", TENANT_ID, USER_ID);
            assertThat(approval.status()).isEqualTo("APPROVED");

            // 9. Verify final state
            EngineCardModel finalCard = engineService.getCard(card.id(), TENANT_ID);
            assertThat(finalCard.stepCode()).isEqualTo("CLOSED");
            assertThat(finalCard.pendingApproval()).isFalse();
            assertThat(finalCard.labels()).hasSize(1);
            assertThat(finalCard.comments()).isNotEmpty();
            assertThat(finalCard.assignees()).isNotEmpty();

            // 10. Search should find it
            List<EngineCardModel> searchResult = engineService.searchCards(
                    board.id(), TENANT_ID, null, "HIGH", null, null, null, null, null, null);
            assertThat(searchResult).isNotEmpty();
        }
    }
}
