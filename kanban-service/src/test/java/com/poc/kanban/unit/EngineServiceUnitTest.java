package com.poc.kanban.unit;

import com.poc.kanban.BaseUnitTest;
import com.poc.kanban.domain.*;
import com.poc.kanban.metrics.KanbanMetrics;
import com.poc.kanban.model.*;
import com.poc.kanban.repository.jpa.*;
import com.poc.kanban.service.CardHistoryService;
import com.poc.kanban.service.KanbanAttachmentService;
import com.poc.kanban.service.impl.EngineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EngineServiceImpl with mocked repositories.
 */
@DisplayName("EngineService Unit Tests")
class EngineServiceUnitTest extends BaseUnitTest {

    @Mock
    private JpaRepositoryKanbanBoard boardRepo;

    @Mock
    private JpaRepositoryBoardType boardTypeRepo;

    @Mock
    private JpaRepositoryBoardTypeFeature featureRepo;

    @Mock
    private JpaRepositoryWorkflowStep stepRepo;

    @Mock
    private JpaRepositoryWorkflowTransition transitionRepo;

    @Mock
    private JpaRepositoryApprovalRule approvalRuleRepo;

    @Mock
    private JpaRepositoryApproval approvalRepo;

    @Mock
    private KanbanAttachmentService attachmentService;

    @Mock
    private CardHistoryService historyService;

    @Mock
    private KanbanMetrics kanbanMetrics;

    @InjectMocks
    private EngineServiceImpl engineService;

    // Common test data
    private static final String BOARD_TYPE_CODE = "HELPDESK";
    private static final String BOARD_CODE = "HD";
    private UUID boardId;
    private UUID listId1;
    private UUID listId2;
    private UUID cardId;
    private UUID stepId1;
    private UUID stepId2;

    @BeforeEach
    void setUpTestData() {
        boardId = randomId();
        listId1 = randomId();
        listId2 = randomId();
        cardId = randomId();
        stepId1 = randomId();
        stepId2 = randomId();
    }

    // ==================== Helper Methods ====================

    private BoardType createBoardType(String code, String name) {
        return BoardType.builder()
                .code(code)
                .name(name)
                .productCode("helpdesk")
                .visibleInKanban(true)
                .singleton(false)
                .scope("TENANT")
                .autoCreate(false)
                .numberPrefix("HD")
                .allowCustomLists(false)
                .build();
    }

    private BoardType createSingletonBoardType(String code, String name) {
        BoardType bt = createBoardType(code, name);
        bt.setSingleton(true);
        return bt;
    }

    private BoardType createUserScopeBoardType(String code, String name) {
        BoardType bt = createBoardType(code, name);
        bt.setScope("USER");
        return bt;
    }

    private WorkflowStep createStep(UUID id, String code, String name, int position, Boolean isInitial, Boolean isFinal) {
        return WorkflowStep.builder()
                .id(id)
                .boardTypeCode(BOARD_TYPE_CODE)
                .stepCode(code)
                .stepName(name)
                .position(position)
                .color("#0000FF")
                .isInitial(isInitial)
                .isFinal(isFinal)
                .build();
    }

    private KanbanBoard createBoard(UUID id, UUID tenantId) {
        KanbanCard card = KanbanCard.builder()
                .id(cardId)
                .title("Test Card")
                .description("Test Description")
                .cardNumber(1)
                .position(0)
                .completed(false)
                .progress(0)
                .attachments(0)
                .pendingApproval(false)
                .subTasks(new ArrayList<>())
                .comments(new ArrayList<>())
                .labels(new ArrayList<>())
                .build();

        KanbanList list1 = KanbanList.builder()
                .id(listId1)
                .title("Open")
                .position(0)
                .workflowStepId(stepId1)
                .cards(new ArrayList<>(List.of(card)))
                .build();

        KanbanList list2 = KanbanList.builder()
                .id(listId2)
                .title("In Progress")
                .position(1)
                .workflowStepId(stepId2)
                .cards(new ArrayList<>())
                .build();

        return KanbanBoard.builder()
                .id(id)
                .title("Helpdesk Board")
                .boardCode(BOARD_CODE)
                .userId(TEST_USER_ID)
                .tenantId(tenantId)
                .boardTypeCode(BOARD_TYPE_CODE)
                .isActive(true)
                .lists(new ArrayList<>(List.of(list1, list2)))
                .labels(new ArrayList<>())
                .build();
    }

    private KanbanBoard createEmptyBoard(UUID id, UUID tenantId) {
        KanbanList list1 = KanbanList.builder()
                .id(listId1)
                .title("Open")
                .position(0)
                .workflowStepId(stepId1)
                .cards(new ArrayList<>())
                .build();

        KanbanList list2 = KanbanList.builder()
                .id(listId2)
                .title("In Progress")
                .position(1)
                .workflowStepId(stepId2)
                .cards(new ArrayList<>())
                .build();

        return KanbanBoard.builder()
                .id(id)
                .title("Helpdesk Board")
                .boardCode(BOARD_CODE)
                .userId(TEST_USER_ID)
                .tenantId(tenantId)
                .boardTypeCode(BOARD_TYPE_CODE)
                .isActive(true)
                .lists(new ArrayList<>(List.of(list1, list2)))
                .labels(new ArrayList<>())
                .build();
    }

    // ==================== Board Type Tests ====================

    @Nested
    @DisplayName("Board Types")
    class BoardTypeTests {

        @Test
        @DisplayName("Should return all board types")
        void shouldReturnAllBoardTypes() {
            // Given
            BoardType bt1 = createBoardType("HELPDESK", "Helpdesk");
            BoardType bt2 = createBoardType("FINANCE", "Finance");
            when(boardTypeRepo.findAll()).thenReturn(List.of(bt1, bt2));

            BoardTypeFeature feature1 = BoardTypeFeature.builder()
                    .id(randomId())
                    .boardTypeCode("HELPDESK")
                    .featureCode("COMMENTS")
                    .enabled(true)
                    .build();
            when(featureRepo.findByBoardTypeCode("HELPDESK")).thenReturn(List.of(feature1));
            when(featureRepo.findByBoardTypeCode("FINANCE")).thenReturn(List.of());

            // When
            List<BoardTypeModel> result = engineService.getBoardTypes();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).code()).isEqualTo("HELPDESK");
            assertThat(result.get(0).enabledFeatures()).contains("COMMENTS");
            assertThat(result.get(1).code()).isEqualTo("FINANCE");
            assertThat(result.get(1).enabledFeatures()).isEmpty();
        }

        @Test
        @DisplayName("Should return specific board type by code")
        void shouldReturnBoardTypeByCode() {
            // Given
            BoardType bt = createBoardType(BOARD_TYPE_CODE, "Helpdesk");
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));
            when(featureRepo.findByBoardTypeCode(BOARD_TYPE_CODE)).thenReturn(List.of());

            // When
            BoardTypeModel result = engineService.getBoardType(BOARD_TYPE_CODE);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.code()).isEqualTo(BOARD_TYPE_CODE);
            assertThat(result.name()).isEqualTo("Helpdesk");
            assertThat(result.numberPrefix()).isEqualTo("HD");
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when board type not found")
        void shouldThrowWhenBoardTypeNotFound() {
            // Given
            when(boardTypeRepo.findById("NONEXISTENT")).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> engineService.getBoardType("NONEXISTENT"))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board type not found");
        }

        @Test
        @DisplayName("Should filter out disabled features")
        void shouldFilterOutDisabledFeatures() {
            // Given
            BoardType bt = createBoardType(BOARD_TYPE_CODE, "Helpdesk");
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));

            BoardTypeFeature enabled = BoardTypeFeature.builder()
                    .id(randomId())
                    .boardTypeCode(BOARD_TYPE_CODE)
                    .featureCode("COMMENTS")
                    .enabled(true)
                    .build();
            BoardTypeFeature disabled = BoardTypeFeature.builder()
                    .id(randomId())
                    .boardTypeCode(BOARD_TYPE_CODE)
                    .featureCode("APPROVALS")
                    .enabled(false)
                    .build();
            when(featureRepo.findByBoardTypeCode(BOARD_TYPE_CODE)).thenReturn(List.of(enabled, disabled));

            // When
            BoardTypeModel result = engineService.getBoardType(BOARD_TYPE_CODE);

            // Then
            assertThat(result.enabledFeatures()).containsExactly("COMMENTS");
            assertThat(result.enabledFeatures()).doesNotContain("APPROVALS");
        }
    }

    // ==================== Workflow Tests ====================

    @Nested
    @DisplayName("Workflow")
    class WorkflowTests {

        @Test
        @DisplayName("Should return workflow with steps and transitions")
        void shouldReturnWorkflow() {
            // Given
            BoardType bt = createBoardType(BOARD_TYPE_CODE, "Helpdesk");
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));

            WorkflowStep step1 = createStep(stepId1, "OPEN", "Open", 0, true, false);
            WorkflowStep step2 = createStep(stepId2, "IN_PROGRESS", "In Progress", 1, false, false);
            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE)).thenReturn(List.of(step1, step2));

            UUID transitionId = randomId();
            WorkflowTransition transition = WorkflowTransition.builder()
                    .id(transitionId)
                    .boardTypeCode(BOARD_TYPE_CODE)
                    .fromStepId(stepId1)
                    .toStepId(stepId2)
                    .requiresComment(false)
                    .requiresApproval(false)
                    .build();
            when(transitionRepo.findByBoardTypeCode(BOARD_TYPE_CODE)).thenReturn(List.of(transition));

            // When
            WorkflowModel result = engineService.getWorkflow(BOARD_TYPE_CODE);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.boardTypeCode()).isEqualTo(BOARD_TYPE_CODE);
            assertThat(result.steps()).hasSize(2);
            assertThat(result.steps().get(0).stepCode()).isEqualTo("OPEN");
            assertThat(result.steps().get(0).isInitial()).isTrue();
            assertThat(result.steps().get(1).stepCode()).isEqualTo("IN_PROGRESS");
            assertThat(result.transitions()).hasSize(1);
            assertThat(result.transitions().get(0).fromStepCode()).isEqualTo("OPEN");
            assertThat(result.transitions().get(0).toStepCode()).isEqualTo("IN_PROGRESS");
        }

        @Test
        @DisplayName("Should throw when getting workflow for non-existent board type")
        void shouldThrowWhenWorkflowBoardTypeNotFound() {
            // Given
            when(boardTypeRepo.findById("NONEXISTENT")).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> engineService.getWorkflow("NONEXISTENT"))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board type not found");
        }
    }

    // ==================== Board CRUD Tests ====================

    @Nested
    @DisplayName("Boards")
    class BoardTests {

        @Test
        @DisplayName("Should return all boards for tenant")
        void shouldReturnAllBoardsForTenant() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE)).thenReturn(List.of());
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.empty());

            // When
            List<EngineBoardModel> result = engineService.getBoards(TEST_TENANT_ID, null);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(boardId);
            assertThat(result.get(0).title()).isEqualTo("Helpdesk Board");
        }

        @Test
        @DisplayName("Should return boards filtered by type")
        void shouldReturnBoardsFilteredByType() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByBoardTypeCodeAndTenantId(BOARD_TYPE_CODE, TEST_TENANT_ID))
                    .thenReturn(List.of(board));
            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE)).thenReturn(List.of());
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.empty());

            // When
            List<EngineBoardModel> result = engineService.getBoards(TEST_TENANT_ID, BOARD_TYPE_CODE);

            // Then
            assertThat(result).hasSize(1);
            verify(boardRepo).findByBoardTypeCodeAndTenantId(BOARD_TYPE_CODE, TEST_TENANT_ID);
            verify(boardRepo, never()).findByTenantId(any());
        }

        @Test
        @DisplayName("Should create board from board type")
        void shouldCreateBoard() {
            // Given
            BoardType bt = createBoardType(BOARD_TYPE_CODE, "Helpdesk");
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));

            WorkflowStep step1 = createStep(stepId1, "OPEN", "Open", 0, true, false);
            WorkflowStep step2 = createStep(stepId2, "IN_PROGRESS", "In Progress", 1, false, false);
            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE))
                    .thenReturn(List.of(step1, step2));

            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            CreateEngineBoardRequest request = new CreateEngineBoardRequest(BOARD_TYPE_CODE, "My Board", "MB");

            // When
            EngineBoardModel result = engineService.createBoard(request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("My Board");
            assertThat(result.boardTypeCode()).isEqualTo(BOARD_TYPE_CODE);
            verify(boardRepo).save(any(KanbanBoard.class));
            verify(kanbanMetrics).recordBoardCreated();
            verify(kanbanMetrics).incrementActiveBoards();
        }

        @Test
        @DisplayName("Should use board type name when title not provided")
        void shouldUseBoardTypeNameWhenTitleNotProvided() {
            // Given
            BoardType bt = createBoardType(BOARD_TYPE_CODE, "Helpdesk");
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));

            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE))
                    .thenReturn(List.of());
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            CreateEngineBoardRequest request = new CreateEngineBoardRequest(BOARD_TYPE_CODE, null, null);

            // When
            EngineBoardModel result = engineService.createBoard(request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result.title()).isEqualTo("Helpdesk");
        }

        @Test
        @DisplayName("Should return existing board for singleton board type")
        void shouldReturnExistingBoardForSingleton() {
            // Given
            BoardType bt = createSingletonBoardType(BOARD_TYPE_CODE, "Helpdesk");
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));

            KanbanBoard existingBoard = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByBoardTypeCodeAndTenantId(BOARD_TYPE_CODE, TEST_TENANT_ID))
                    .thenReturn(List.of(existingBoard));
            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE)).thenReturn(List.of());

            CreateEngineBoardRequest request = new CreateEngineBoardRequest(BOARD_TYPE_CODE, "Another", null);

            // When
            EngineBoardModel result = engineService.createBoard(request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result.id()).isEqualTo(boardId);
            verify(boardRepo, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when creating board with non-existent board type")
        void shouldThrowWhenBoardTypeNotFoundForCreate() {
            // Given
            when(boardTypeRepo.findById("NONEXISTENT")).thenReturn(Optional.empty());
            CreateEngineBoardRequest request = new CreateEngineBoardRequest("NONEXISTENT", "Title", null);

            // When/Then
            assertThatThrownBy(() -> engineService.createBoard(request, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board type not found");
        }

        @Test
        @DisplayName("Should get board by id and tenant")
        void shouldGetBoardById() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE)).thenReturn(List.of());
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.empty());

            // When
            EngineBoardModel result = engineService.getBoard(boardId, TEST_TENANT_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(boardId);
        }

        @Test
        @DisplayName("Should throw when board not found by id")
        void shouldThrowWhenBoardNotFoundById() {
            // Given
            UUID nonExistentId = randomId();
            when(boardRepo.findByIdAndTenantId(nonExistentId, TEST_TENANT_ID)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> engineService.getBoard(nonExistentId, TEST_TENANT_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board not found");
        }

        @Test
        @DisplayName("Should update board title")
        void shouldUpdateBoardTitle() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));
            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE)).thenReturn(List.of());
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.empty());

            EngineBoardModel updateModel = new EngineBoardModel(
                    null, "Updated Title", null, null, null, null,
                    null, null, null, null, null, null, null);

            // When
            EngineBoardModel result = engineService.updateBoard(boardId, updateModel, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result.title()).isEqualTo("Updated Title");
            verify(boardRepo).save(any(KanbanBoard.class));
            verify(kanbanMetrics).recordBoardUpdated();
        }

        @Test
        @DisplayName("Should delete board")
        void shouldDeleteBoard() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));

            // When
            engineService.deleteBoard(boardId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            verify(boardRepo).delete(board);
            verify(kanbanMetrics).recordBoardDeleted();
            verify(kanbanMetrics).decrementActiveBoards();
        }

        @Test
        @DisplayName("Should throw when deleting non-existent board")
        void shouldThrowWhenDeletingNonExistentBoard() {
            // Given
            UUID nonExistentId = randomId();
            when(boardRepo.findByIdAndTenantId(nonExistentId, TEST_TENANT_ID)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> engineService.deleteBoard(nonExistentId, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board not found");
        }

        @Test
        @DisplayName("Should find board by type for tenant scope")
        void shouldFindBoardByTypeForTenantScope() {
            // Given
            BoardType bt = createBoardType(BOARD_TYPE_CODE, "Helpdesk");
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));

            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByBoardTypeCodeAndTenantId(BOARD_TYPE_CODE, TEST_TENANT_ID))
                    .thenReturn(List.of(board));
            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE)).thenReturn(List.of());

            // When
            EngineBoardModel result = engineService.findBoard(BOARD_TYPE_CODE, TEST_TENANT_ID, null);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(boardId);
        }

        @Test
        @DisplayName("Should find board by type for user scope")
        void shouldFindBoardByTypeForUserScope() {
            // Given
            BoardType bt = createUserScopeBoardType(BOARD_TYPE_CODE, "Helpdesk");
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));

            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByBoardTypeCodeAndTenantIdAndUserId(BOARD_TYPE_CODE, TEST_TENANT_ID, TEST_USER_ID))
                    .thenReturn(Optional.of(board));
            when(stepRepo.findByBoardTypeCodeOrderByPositionAsc(BOARD_TYPE_CODE)).thenReturn(List.of());
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));

            // When
            EngineBoardModel result = engineService.findBoard(BOARD_TYPE_CODE, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(boardId);
        }

        @Test
        @DisplayName("Should throw when finding board that does not exist")
        void shouldThrowWhenFindingNonExistentBoard() {
            // Given
            BoardType bt = createBoardType(BOARD_TYPE_CODE, "Helpdesk");
            when(boardTypeRepo.findById(BOARD_TYPE_CODE)).thenReturn(Optional.of(bt));
            when(boardRepo.findByBoardTypeCodeAndTenantId(BOARD_TYPE_CODE, TEST_TENANT_ID))
                    .thenReturn(List.of());

            // When/Then
            assertThatThrownBy(() -> engineService.findBoard(BOARD_TYPE_CODE, TEST_TENANT_ID, null))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board not found for type");
        }
    }

    // ==================== Card CRUD Tests ====================

    @Nested
    @DisplayName("Cards")
    class CardTests {

        @Test
        @DisplayName("Should create card on board")
        void shouldCreateCard() {
            // Given
            KanbanBoard board = createEmptyBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));

            WorkflowStep initialStep = createStep(stepId1, "OPEN", "Open", 0, true, false);
            when(stepRepo.findByBoardTypeCodeAndStepCode(BOARD_TYPE_CODE, "OPEN"))
                    .thenReturn(Optional.of(initialStep));

            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "New Card", "Description", "#FF0000", "HIGH",
                    LocalDate.now(), LocalDate.now().plusDays(7),
                    "OPEN", null, "helpdesk-service");

            // When
            EngineCardModel result = engineService.createCard(boardId, request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("New Card");
            assertThat(result.description()).isEqualTo("Description");
            assertThat(result.completed()).isFalse();
            assertThat(result.progress()).isEqualTo(0);
            verify(boardRepo).save(any(KanbanBoard.class));
            verify(kanbanMetrics).recordCardCreated();
            verify(historyService).trackCardCreated(any(), eq(boardId), eq(TEST_USER_ID), isNull(), isNull());
        }

        @Test
        @DisplayName("Should create card with targetListId")
        void shouldCreateCardWithTargetListId() {
            // Given
            KanbanBoard board = createEmptyBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));
            when(stepRepo.findById(stepId2)).thenReturn(Optional.empty());

            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Card in List 2", null, null, null,
                    null, null, null, listId2, null);

            // When
            EngineCardModel result = engineService.createCard(boardId, request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("Card in List 2");
            assertThat(result.listId()).isEqualTo(listId2);
        }

        @Test
        @DisplayName("Should throw when creating card on non-existent board")
        void shouldThrowWhenBoardNotFoundForCreateCard() {
            // Given
            UUID nonExistentBoardId = randomId();
            when(boardRepo.findByIdAndTenantId(nonExistentBoardId, TEST_TENANT_ID)).thenReturn(Optional.empty());

            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Card", null, null, null, null, null, null, null, null);

            // When/Then
            assertThatThrownBy(() -> engineService.createCard(nonExistentBoardId, request, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board not found");
        }

        @Test
        @DisplayName("Should get card by id")
        void shouldGetCardById() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            // When
            EngineCardModel result = engineService.getCard(cardId, TEST_TENANT_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(cardId);
            assertThat(result.title()).isEqualTo("Test Card");
        }

        @Test
        @DisplayName("Should throw when card not found by id")
        void shouldThrowWhenCardNotFoundById() {
            // Given
            UUID nonExistentCardId = randomId();
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of());

            // When/Then
            assertThatThrownBy(() -> engineService.getCard(nonExistentCardId, TEST_TENANT_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Card not found");
        }

        @Test
        @DisplayName("Should get card by code")
        void shouldGetCardByCode() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByBoardCodeAndTenantId("HD", TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            // When
            EngineCardModel result = engineService.getCardByCode("HD-0001", TEST_TENANT_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.cardCode()).isEqualTo("HD-0001");
            assertThat(result.title()).isEqualTo("Test Card");
        }

        @Test
        @DisplayName("Should throw for invalid card code format")
        void shouldThrowForInvalidCardCodeFormat() {
            // When/Then
            assertThatThrownBy(() -> engineService.getCardByCode("INVALIDCODE", TEST_TENANT_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid card code format");
        }

        @Test
        @DisplayName("Should throw when card code board not found")
        void shouldThrowWhenCardCodeBoardNotFound() {
            // Given
            when(boardRepo.findByBoardCodeAndTenantId("NONEXIST", TEST_TENANT_ID)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> engineService.getCardByCode("NONEXIST-0001", TEST_TENANT_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Card not found with code");
        }

        @Test
        @DisplayName("Should update card")
        void shouldUpdateCard() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            KanbanPriorityModel priority = new KanbanPriorityModel("#FF0000", "HIGH");
            EngineCardModel updateModel = new EngineCardModel(
                    null, null, "Updated Title", "Updated Desc",
                    LocalDate.now(), LocalDate.now().plusDays(14),
                    true, 75, null, null, null, null, null,
                    null, priority, null, null, null, null,
                    null, null, null, null, null, null);

            // When
            EngineCardModel result = engineService.updateCard(cardId, updateModel, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("Updated Title");
            assertThat(result.description()).isEqualTo("Updated Desc");
            assertThat(result.completed()).isTrue();
            assertThat(result.progress()).isEqualTo(75);
            verify(kanbanMetrics).recordCardUpdated();
        }

        @Test
        @DisplayName("Should delete card")
        void shouldDeleteCard() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            engineService.deleteCard(cardId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            verify(boardRepo).save(any(KanbanBoard.class));
            verify(kanbanMetrics).recordCardDeleted();
            verify(historyService).trackCardDeleted(eq(cardId), eq(boardId), eq(TEST_USER_ID), isNull());
        }

        @Test
        @DisplayName("Should throw when deleting non-existent card")
        void shouldThrowWhenDeletingNonExistentCard() {
            // Given
            UUID nonExistentCardId = randomId();
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of());

            // When/Then
            assertThatThrownBy(() -> engineService.deleteCard(nonExistentCardId, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Card not found");
        }

        @Test
        @DisplayName("Should assign sequential card numbers")
        void shouldAssignSequentialCardNumbers() {
            // Given - board with one existing card (cardNumber=1)
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));

            WorkflowStep initialStep = createStep(stepId1, "OPEN", "Open", 0, true, false);
            when(stepRepo.findInitialStep(BOARD_TYPE_CODE)).thenReturn(Optional.of(initialStep));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.of(initialStep));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            CreateEngineCardRequest request = new CreateEngineCardRequest(
                    "Second Card", null, null, null, null, null, null, null, null);

            // When
            EngineCardModel result = engineService.createCard(boardId, request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.cardCode()).isEqualTo("HD-0002");
        }
    }

    // ==================== Move Card Tests ====================

    @Nested
    @DisplayName("Move Card")
    class MoveCardTests {

        @Test
        @DisplayName("Should move card between lists")
        void shouldMoveCardBetweenLists() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            WorkflowTransition transition = WorkflowTransition.builder()
                    .id(randomId())
                    .boardTypeCode(BOARD_TYPE_CODE)
                    .fromStepId(stepId1)
                    .toStepId(stepId2)
                    .requiresComment(false)
                    .requiresApproval(false)
                    .build();
            when(transitionRepo.findTransition(BOARD_TYPE_CODE, stepId1, stepId2))
                    .thenReturn(Optional.of(transition));
            when(stepRepo.findById(stepId2)).thenReturn(Optional.empty());

            MoveCardRequest request = new MoveCardRequest(listId2, null, null, null);

            // When
            EngineCardModel result = engineService.moveCard(cardId, request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.listId()).isEqualTo(listId2);
            verify(kanbanMetrics).recordCardMoved();
            verify(historyService).trackCardUpdated(eq(cardId), eq(boardId), eq(TEST_USER_ID),
                    isNull(), any(), isNull());
        }

        @Test
        @DisplayName("Should move card within same list (reorder)")
        void shouldMoveCardWithinSameList() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            MoveCardRequest request = new MoveCardRequest(listId1, null, 5, null);

            // When
            EngineCardModel result = engineService.moveCard(cardId, request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.position()).isEqualTo(5);
            verify(kanbanMetrics, never()).recordCardMoved();
        }

        @Test
        @DisplayName("Should throw when transition is not allowed")
        void shouldThrowWhenTransitionNotAllowed() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(transitionRepo.findTransition(BOARD_TYPE_CODE, stepId1, stepId2))
                    .thenReturn(Optional.empty());

            MoveCardRequest request = new MoveCardRequest(listId2, null, null, null);

            // When/Then
            assertThatThrownBy(() -> engineService.moveCard(cardId, request, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("is not allowed");
        }

        @Test
        @DisplayName("Should throw when comment required but not provided")
        void shouldThrowWhenCommentRequiredButNotProvided() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));

            WorkflowTransition transition = WorkflowTransition.builder()
                    .id(randomId())
                    .boardTypeCode(BOARD_TYPE_CODE)
                    .fromStepId(stepId1)
                    .toStepId(stepId2)
                    .requiresComment(true)
                    .requiresApproval(false)
                    .build();
            when(transitionRepo.findTransition(BOARD_TYPE_CODE, stepId1, stepId2))
                    .thenReturn(Optional.of(transition));

            MoveCardRequest request = new MoveCardRequest(listId2, null, null, null);

            // When/Then
            assertThatThrownBy(() -> engineService.moveCard(cardId, request, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Comment is required");
        }

        @Test
        @DisplayName("Should add comment when moving with comment")
        void shouldAddCommentWhenMoving() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            WorkflowTransition transition = WorkflowTransition.builder()
                    .id(randomId())
                    .boardTypeCode(BOARD_TYPE_CODE)
                    .fromStepId(stepId1)
                    .toStepId(stepId2)
                    .requiresComment(true)
                    .requiresApproval(false)
                    .build();
            when(transitionRepo.findTransition(BOARD_TYPE_CODE, stepId1, stepId2))
                    .thenReturn(Optional.of(transition));
            when(stepRepo.findById(stepId2)).thenReturn(Optional.empty());

            MoveCardRequest request = new MoveCardRequest(listId2, null, null, "Moving to progress");

            // When
            EngineCardModel result = engineService.moveCard(cardId, request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.comments()).hasSize(1);
            assertThat(result.comments().get(0).getText()).isEqualTo("Moving to progress");
        }

        @Test
        @DisplayName("Should create pending approval when transition requires approval")
        void shouldCreatePendingApproval() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            UUID transitionId = randomId();
            WorkflowTransition transition = WorkflowTransition.builder()
                    .id(transitionId)
                    .boardTypeCode(BOARD_TYPE_CODE)
                    .fromStepId(stepId1)
                    .toStepId(stepId2)
                    .requiresComment(false)
                    .requiresApproval(true)
                    .build();
            when(transitionRepo.findTransition(BOARD_TYPE_CODE, stepId1, stepId2))
                    .thenReturn(Optional.of(transition));
            when(approvalRuleRepo.findByTransitionId(transitionId)).thenReturn(List.of());
            when(approvalRepo.save(any(Approval.class))).thenAnswer(inv -> inv.getArgument(0));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            MoveCardRequest request = new MoveCardRequest(listId2, null, null, null);

            // When
            EngineCardModel result = engineService.moveCard(cardId, request, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.pendingApproval()).isTrue();
            assertThat(result.pendingTargetListId()).isEqualTo(listId2);
            // Card should still be in original list
            assertThat(result.listId()).isEqualTo(listId1);
            verify(approvalRepo).save(any(Approval.class));
            verify(kanbanMetrics, never()).recordCardMoved();
        }
    }

    // ==================== Search Tests ====================

    @Nested
    @DisplayName("Search Cards")
    class SearchTests {

        @Test
        @DisplayName("Should return all cards when no filters")
        void shouldReturnAllCardsWhenNoFilters() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            // When
            List<EngineCardModel> result = engineService.searchCards(
                    boardId, TEST_TENANT_ID, null, null, null, null, null, null, null, null);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).title()).isEqualTo("Test Card");
        }

        @Test
        @DisplayName("Should filter by completed status")
        void shouldFilterByCompleted() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            // When - filter for completed=true, but card is not completed
            List<EngineCardModel> result = engineService.searchCards(
                    boardId, TEST_TENANT_ID, null, null, null, null, true, null, null, null);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should filter by search text in title")
        void shouldFilterBySearchText() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            // When
            List<EngineCardModel> result = engineService.searchCards(
                    boardId, TEST_TENANT_ID, null, null, null, null, null, "Test", null, null);

            // Then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should filter by search text - no match")
        void shouldFilterBySearchTextNoMatch() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            // When
            List<EngineCardModel> result = engineService.searchCards(
                    boardId, TEST_TENANT_ID, null, null, null, null, null, "NonExistent", null, null);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should filter by source service")
        void shouldFilterBySourceService() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            // Card has no sourceService set in our helper
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(stepRepo.findById(stepId1)).thenReturn(Optional.empty());

            // When
            List<EngineCardModel> result = engineService.searchCards(
                    boardId, TEST_TENANT_ID, null, null, null, null, null, null, null, "helpdesk-service");

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should throw when searching on non-existent board")
        void shouldThrowWhenSearchingNonExistentBoard() {
            // Given
            UUID nonExistentBoardId = randomId();
            when(boardRepo.findByIdAndTenantId(nonExistentBoardId, TEST_TENANT_ID)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> engineService.searchCards(
                    nonExistentBoardId, TEST_TENANT_ID, null, null, null, null, null, null, null, null))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Board not found");
        }
    }

    // ==================== Comment Tests ====================

    @Nested
    @DisplayName("Comments")
    class CommentTests {

        @Test
        @DisplayName("Should add comment to card")
        void shouldAddComment() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            KanbanCommentModel result = engineService.addComment(cardId, "Test comment", TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getText()).isEqualTo("Test comment");
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(result.getCreatedAt()).isNotNull();
            verify(kanbanMetrics).recordCommentCreated();
        }

        @Test
        @DisplayName("Should update comment")
        void shouldUpdateComment() {
            // Given
            UUID commentId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanComment existingComment = KanbanComment.builder()
                    .id(commentId)
                    .text("Original")
                    .userId(TEST_USER_ID)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            board.getLists().get(0).getCards().get(0).getComments().add(existingComment);

            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            KanbanCommentModel result = engineService.updateComment(cardId, commentId, "Updated", TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getText()).isEqualTo("Updated");
            verify(kanbanMetrics).recordCommentUpdated();
        }

        @Test
        @DisplayName("Should throw when updating non-existent comment")
        void shouldThrowWhenUpdatingNonExistentComment() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            UUID nonExistentCommentId = randomId();

            // When/Then
            assertThatThrownBy(() ->
                    engineService.updateComment(cardId, nonExistentCommentId, "Updated", TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Comment not found");
        }

        @Test
        @DisplayName("Should delete comment")
        void shouldDeleteComment() {
            // Given
            UUID commentId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanComment comment = KanbanComment.builder()
                    .id(commentId)
                    .text("To delete")
                    .userId(TEST_USER_ID)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            board.getLists().get(0).getCards().get(0).getComments().add(comment);

            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            engineService.deleteComment(cardId, commentId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            verify(boardRepo).save(any(KanbanBoard.class));
            verify(kanbanMetrics).recordCommentDeleted();
        }

        @Test
        @DisplayName("Should throw when deleting non-existent comment")
        void shouldThrowWhenDeletingNonExistentComment() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            UUID nonExistentCommentId = randomId();

            // When/Then
            assertThatThrownBy(() ->
                    engineService.deleteComment(cardId, nonExistentCommentId, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Comment not found");
        }
    }

    // ==================== SubTask Tests ====================

    @Nested
    @DisplayName("SubTasks")
    class SubTaskTests {

        @Test
        @DisplayName("Should add subtask to card")
        void shouldAddSubTask() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            KanbanSubTaskModel subTaskModel = KanbanSubTaskModel.builder()
                    .text("New subtask")
                    .completed(false)
                    .dueDate(LocalDate.now().plusDays(3))
                    .build();

            // When
            KanbanSubTaskModel result = engineService.addSubTask(cardId, subTaskModel, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getText()).isEqualTo("New subtask");
            assertThat(result.getCompleted()).isFalse();
            assertThat(result.getPosition()).isEqualTo(0);
            assertThat(result.getDueDate()).isEqualTo(LocalDate.now().plusDays(3));
            verify(kanbanMetrics).recordSubtaskCreated();
        }

        @Test
        @DisplayName("Should add subtask with assignee")
        void shouldAddSubTaskWithAssignee() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            KanbanAssigneeModel assignee = KanbanAssigneeModel.builder().userId(TEST_USER_ID).build();
            KanbanSubTaskModel subTaskModel = KanbanSubTaskModel.builder()
                    .text("Subtask with assignee")
                    .completed(false)
                    .assignee(assignee)
                    .build();

            // When
            KanbanSubTaskModel result = engineService.addSubTask(cardId, subTaskModel, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result.getAssignee()).isNotNull();
            assertThat(result.getAssignee().getUserId()).isEqualTo(TEST_USER_ID);
        }

        @Test
        @DisplayName("Should update subtask")
        void shouldUpdateSubTask() {
            // Given
            UUID subtaskId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanSubTask existingSubTask = KanbanSubTask.builder()
                    .id(subtaskId)
                    .text("Original")
                    .completed(false)
                    .position(0)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            board.getLists().get(0).getCards().get(0).getSubTasks().add(existingSubTask);

            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            KanbanSubTaskModel updateModel = KanbanSubTaskModel.builder()
                    .text("Updated subtask")
                    .completed(true)
                    .build();

            // When
            KanbanSubTaskModel result = engineService.updateSubTask(cardId, subtaskId, updateModel, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result.getText()).isEqualTo("Updated subtask");
            assertThat(result.getCompleted()).isTrue();
            verify(kanbanMetrics).recordSubtaskUpdated();
        }

        @Test
        @DisplayName("Should delete subtask and reindex positions")
        void shouldDeleteSubTask() {
            // Given
            UUID subtaskId1 = randomId();
            UUID subtaskId2 = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanSubTask st1 = KanbanSubTask.builder().id(subtaskId1).text("ST1").position(0).completed(false)
                    .createdAt(Instant.now()).updatedAt(Instant.now()).build();
            KanbanSubTask st2 = KanbanSubTask.builder().id(subtaskId2).text("ST2").position(1).completed(false)
                    .createdAt(Instant.now()).updatedAt(Instant.now()).build();
            board.getLists().get(0).getCards().get(0).getSubTasks().addAll(List.of(st1, st2));

            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            engineService.deleteSubTask(cardId, subtaskId1, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            KanbanCard card = board.getLists().get(0).getCards().get(0);
            assertThat(card.getSubTasks()).hasSize(1);
            assertThat(card.getSubTasks().get(0).getPosition()).isEqualTo(0);
            verify(kanbanMetrics).recordSubtaskDeleted();
        }

        @Test
        @DisplayName("Should throw when deleting non-existent subtask")
        void shouldThrowWhenDeletingNonExistentSubTask() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            UUID nonExistentSubtaskId = randomId();

            // When/Then
            assertThatThrownBy(() ->
                    engineService.deleteSubTask(cardId, nonExistentSubtaskId, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("SubTask not found");
        }
    }

    // ==================== Label Tests ====================

    @Nested
    @DisplayName("Labels")
    class LabelTests {

        @Test
        @DisplayName("Should create label on board")
        void shouldCreateLabel() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            KanbanLabelModel result = engineService.createLabel(boardId, "Bug", "#FF0000", TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getName()).isEqualTo("Bug");
            assertThat(result.getColor()).isEqualTo("#FF0000");
            verify(kanbanMetrics).recordLabelCreated();
        }

        @Test
        @DisplayName("Should get board labels")
        void shouldGetBoardLabels() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanLabel label = KanbanLabel.builder()
                    .id(randomId())
                    .name("Bug")
                    .color("#FF0000")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            board.getLabels().add(label);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));

            // When
            List<KanbanLabelModel> result = engineService.getBoardLabels(boardId, TEST_TENANT_ID);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Bug");
        }

        @Test
        @DisplayName("Should update label")
        void shouldUpdateLabel() {
            // Given
            UUID labelId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanLabel label = KanbanLabel.builder()
                    .id(labelId)
                    .name("Bug")
                    .color("#FF0000")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            board.getLabels().add(label);
            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            KanbanLabelModel result = engineService.updateLabel(boardId, labelId, "Critical", "#990000",
                    TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result.getName()).isEqualTo("Critical");
            assertThat(result.getColor()).isEqualTo("#990000");
            verify(kanbanMetrics).recordLabelUpdated();
        }

        @Test
        @DisplayName("Should delete label and remove from cards")
        void shouldDeleteLabelAndRemoveFromCards() {
            // Given
            UUID labelId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanLabel label = KanbanLabel.builder()
                    .id(labelId).name("Bug").color("#FF0000")
                    .createdAt(Instant.now()).updatedAt(Instant.now()).build();
            board.getLabels().add(label);
            // Add label to card
            board.getLists().get(0).getCards().get(0).getLabels().add(
                    KanbanCardLabel.builder().id(randomId()).labelId(labelId).build());

            when(boardRepo.findByIdAndTenantId(boardId, TEST_TENANT_ID)).thenReturn(Optional.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            engineService.deleteLabel(boardId, labelId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(board.getLabels()).isEmpty();
            assertThat(board.getLists().get(0).getCards().get(0).getLabels()).isEmpty();
            verify(kanbanMetrics).recordLabelDeleted();
        }

        @Test
        @DisplayName("Should add label to card")
        void shouldAddLabelToCard() {
            // Given
            UUID labelId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanLabel label = KanbanLabel.builder()
                    .id(labelId).name("Bug").color("#FF0000")
                    .createdAt(Instant.now()).updatedAt(Instant.now()).build();
            board.getLabels().add(label);

            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            engineService.addLabelToCard(cardId, labelId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            KanbanCard card = board.getLists().get(0).getCards().get(0);
            assertThat(card.getLabels()).hasSize(1);
            assertThat(card.getLabels().get(0).getLabelId()).isEqualTo(labelId);
            verify(kanbanMetrics).recordLabelAssigned();
        }

        @Test
        @DisplayName("Should throw when adding non-existent label to card")
        void shouldThrowWhenAddingNonExistentLabel() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            UUID nonExistentLabelId = randomId();

            // When/Then
            assertThatThrownBy(() ->
                    engineService.addLabelToCard(cardId, nonExistentLabelId, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Label not found in board");
        }

        @Test
        @DisplayName("Should throw when adding duplicate label to card")
        void shouldThrowWhenAddingDuplicateLabel() {
            // Given
            UUID labelId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanLabel label = KanbanLabel.builder()
                    .id(labelId).name("Bug").color("#FF0000")
                    .createdAt(Instant.now()).updatedAt(Instant.now()).build();
            board.getLabels().add(label);
            board.getLists().get(0).getCards().get(0).getLabels().add(
                    KanbanCardLabel.builder().id(randomId()).labelId(labelId).build());

            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));

            // When/Then
            assertThatThrownBy(() ->
                    engineService.addLabelToCard(cardId, labelId, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Label already assigned");
        }

        @Test
        @DisplayName("Should remove label from card")
        void shouldRemoveLabelFromCard() {
            // Given
            UUID labelId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            board.getLists().get(0).getCards().get(0).getLabels().add(
                    KanbanCardLabel.builder().id(randomId()).labelId(labelId).build());

            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            engineService.removeLabelFromCard(cardId, labelId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            KanbanCard card = board.getLists().get(0).getCards().get(0);
            assertThat(card.getLabels()).isEmpty();
            verify(kanbanMetrics).recordLabelUnassigned();
        }

        @Test
        @DisplayName("Should throw when removing non-assigned label from card")
        void shouldThrowWhenRemovingNonAssignedLabel() {
            // Given
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            UUID nonAssignedLabelId = randomId();

            // When/Then
            assertThatThrownBy(() ->
                    engineService.removeLabelFromCard(cardId, nonAssignedLabelId, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Label not assigned to card");
        }
    }

    // ==================== Assignee Tests ====================

    @Nested
    @DisplayName("Assignees")
    class AssigneeTests {

        @Test
        @DisplayName("Should add assignee to card")
        void shouldAddAssignee() {
            // Given
            UUID assigneeUserId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            engineService.addAssignee(cardId, assigneeUserId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            KanbanCard card = board.getLists().get(0).getCards().get(0);
            assertThat(card.getAssigneeUserId()).isEqualTo(assigneeUserId);
            verify(boardRepo).save(any(KanbanBoard.class));
        }

        @Test
        @DisplayName("Should remove assignee from card")
        void shouldRemoveAssignee() {
            // Given
            UUID assigneeUserId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            board.getLists().get(0).getCards().get(0).setAssigneeUserId(assigneeUserId);

            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            engineService.removeAssignee(cardId, assigneeUserId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            KanbanCard card = board.getLists().get(0).getCards().get(0);
            assertThat(card.getAssigneeUserId()).isNull();
        }

        @Test
        @DisplayName("Should not remove assignee when user id does not match")
        void shouldNotRemoveWhenUserIdDoesNotMatch() {
            // Given
            UUID actualAssigneeId = randomId();
            UUID differentUserId = randomId();
            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            board.getLists().get(0).getCards().get(0).setAssigneeUserId(actualAssigneeId);

            when(boardRepo.findByTenantId(TEST_TENANT_ID)).thenReturn(List.of(board));

            // When
            engineService.removeAssignee(cardId, differentUserId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            KanbanCard card = board.getLists().get(0).getCards().get(0);
            assertThat(card.getAssigneeUserId()).isEqualTo(actualAssigneeId);
            verify(boardRepo, never()).save(any());
        }
    }

    // ==================== Attachments Tests ====================

    @Nested
    @DisplayName("Attachments")
    class AttachmentTests {

        @Test
        @DisplayName("Should delegate getAttachments to attachment service")
        void shouldDelegateGetAttachments() {
            // Given
            List<KanbanAttachmentModel> expected = List.of();
            when(attachmentService.findByCardId(cardId)).thenReturn(expected);

            // When
            List<KanbanAttachmentModel> result = engineService.getAttachments(cardId, TEST_TENANT_ID);

            // Then
            assertThat(result).isSameAs(expected);
            verify(attachmentService).findByCardId(cardId);
        }

        @Test
        @DisplayName("Should delegate deleteAttachment to attachment service")
        void shouldDelegateDeleteAttachment() {
            // Given
            UUID attachmentId = randomId();

            // When
            engineService.deleteAttachment(cardId, attachmentId, TEST_TENANT_ID, TEST_USER_ID);

            // Then
            verify(attachmentService).delete(cardId, attachmentId);
        }
    }

    // ==================== History Tests ====================

    @Nested
    @DisplayName("History")
    class HistoryTests {

        @Test
        @DisplayName("Should delegate getCardHistory to history service")
        void shouldDelegateGetCardHistory() {
            // Given
            List<CardHistoryModel> expected = List.of();
            when(historyService.getCardHistory(cardId)).thenReturn(expected);

            // When
            List<CardHistoryModel> result = engineService.getCardHistory(cardId, TEST_TENANT_ID);

            // Then
            assertThat(result).isSameAs(expected);
            verify(historyService).getCardHistory(cardId);
        }
    }

    // ==================== Approval Tests ====================

    @Nested
    @DisplayName("Approvals")
    class ApprovalTests {

        @Test
        @DisplayName("Should get approvals by tenant and status")
        void shouldGetApprovalsByTenantAndStatus() {
            // Given
            Approval approval = Approval.builder()
                    .id(randomId())
                    .tenantId(TEST_TENANT_ID)
                    .cardId(cardId)
                    .boardId(boardId)
                    .fromListId(listId1)
                    .toListId(listId2)
                    .requestedBy(TEST_USER_ID)
                    .requestedAt(Instant.now())
                    .status("PENDING")
                    .build();
            when(approvalRepo.findByTenantIdAndStatus(TEST_TENANT_ID, "PENDING"))
                    .thenReturn(List.of(approval));

            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findById(boardId)).thenReturn(Optional.of(board));
            when(stepRepo.findById(any())).thenReturn(Optional.empty());

            // When
            List<ApprovalModel> result = engineService.getApprovals(TEST_TENANT_ID, "PENDING", null, null);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("Should get approvals by card id")
        void shouldGetApprovalsByCardId() {
            // Given
            Approval approval = Approval.builder()
                    .id(randomId())
                    .tenantId(TEST_TENANT_ID)
                    .cardId(cardId)
                    .boardId(boardId)
                    .fromListId(listId1)
                    .toListId(listId2)
                    .requestedBy(TEST_USER_ID)
                    .requestedAt(Instant.now())
                    .status("PENDING")
                    .build();
            when(approvalRepo.findByCardIdAndStatus(cardId, "PENDING"))
                    .thenReturn(List.of(approval));

            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            when(boardRepo.findById(boardId)).thenReturn(Optional.of(board));
            when(stepRepo.findById(any())).thenReturn(Optional.empty());

            // When
            List<ApprovalModel> result = engineService.getApprovals(TEST_TENANT_ID, "PENDING", null, cardId);

            // Then
            assertThat(result).hasSize(1);
            verify(approvalRepo).findByCardIdAndStatus(cardId, "PENDING");
        }

        @Test
        @DisplayName("Should count approvals")
        void shouldCountApprovals() {
            // Given
            when(approvalRepo.countByTenantIdAndStatus(TEST_TENANT_ID, "PENDING")).thenReturn(5L);

            // When
            CountModel result = engineService.countApprovals(TEST_TENANT_ID, "PENDING");

            // Then
            assertThat(result.count()).isEqualTo(5L);
        }

        @Test
        @DisplayName("Should approve card and move it")
        void shouldApproveCardAndMoveIt() {
            // Given
            UUID approvalId = randomId();
            Approval approval = Approval.builder()
                    .id(approvalId)
                    .tenantId(TEST_TENANT_ID)
                    .cardId(cardId)
                    .boardId(boardId)
                    .fromListId(listId1)
                    .toListId(listId2)
                    .requestedBy(TEST_USER_ID)
                    .requestedAt(Instant.now())
                    .status("PENDING")
                    .build();
            when(approvalRepo.findById(approvalId)).thenReturn(Optional.of(approval));
            when(approvalRepo.save(any(Approval.class))).thenAnswer(inv -> inv.getArgument(0));

            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            // Set pending approval on card
            KanbanCard card = board.getLists().get(0).getCards().get(0);
            card.setPendingApproval(true);
            card.setPendingApprovalId(approvalId);
            card.setPendingTargetList(listId2);

            when(boardRepo.findById(boardId)).thenReturn(Optional.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));
            when(stepRepo.findById(any())).thenReturn(Optional.empty());

            // When
            ApprovalModel result = engineService.approveCard(approvalId, "Approved!", TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo("APPROVED");
            assertThat(result.comment()).isEqualTo("Approved!");

            // Verify card was moved to target list and approval flags cleared
            assertThat(card.getPendingApproval()).isFalse();
            assertThat(card.getPendingApprovalId()).isNull();
            assertThat(card.getPendingTargetList()).isNull();

            verify(kanbanMetrics).recordCardMoved();
            verify(approvalRepo).save(any(Approval.class));
            verify(boardRepo).save(any(KanbanBoard.class));
        }

        @Test
        @DisplayName("Should throw when approving non-PENDING approval")
        void shouldThrowWhenApprovingNonPending() {
            // Given
            UUID approvalId = randomId();
            Approval approval = Approval.builder()
                    .id(approvalId)
                    .status("APPROVED")
                    .build();
            when(approvalRepo.findById(approvalId)).thenReturn(Optional.of(approval));

            // When/Then
            assertThatThrownBy(() ->
                    engineService.approveCard(approvalId, null, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not in PENDING status");
        }

        @Test
        @DisplayName("Should throw when approving non-existent approval")
        void shouldThrowWhenApprovingNonExistent() {
            // Given
            UUID nonExistentId = randomId();
            when(approvalRepo.findById(nonExistentId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() ->
                    engineService.approveCard(nonExistentId, null, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Approval not found");
        }

        @Test
        @DisplayName("Should reject card and clear pending flags")
        void shouldRejectCard() {
            // Given
            UUID approvalId = randomId();
            Approval approval = Approval.builder()
                    .id(approvalId)
                    .tenantId(TEST_TENANT_ID)
                    .cardId(cardId)
                    .boardId(boardId)
                    .fromListId(listId1)
                    .toListId(listId2)
                    .requestedBy(TEST_USER_ID)
                    .requestedAt(Instant.now())
                    .status("PENDING")
                    .build();
            when(approvalRepo.findById(approvalId)).thenReturn(Optional.of(approval));
            when(approvalRepo.save(any(Approval.class))).thenAnswer(inv -> inv.getArgument(0));

            KanbanBoard board = createBoard(boardId, TEST_TENANT_ID);
            KanbanCard card = board.getLists().get(0).getCards().get(0);
            card.setPendingApproval(true);
            card.setPendingApprovalId(approvalId);
            card.setPendingTargetList(listId2);

            when(boardRepo.findById(boardId)).thenReturn(Optional.of(board));
            when(boardRepo.save(any(KanbanBoard.class))).thenAnswer(inv -> inv.getArgument(0));
            when(stepRepo.findById(any())).thenReturn(Optional.empty());

            // When
            ApprovalModel result = engineService.rejectCard(approvalId, "Rejected reason", TEST_TENANT_ID, TEST_USER_ID);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo("REJECTED");
            assertThat(result.comment()).isEqualTo("Rejected reason");

            // Card should remain in original list, pending flags cleared
            assertThat(card.getPendingApproval()).isFalse();
            assertThat(card.getPendingApprovalId()).isNull();
            assertThat(card.getPendingTargetList()).isNull();
            assertThat(board.getLists().get(0).getCards()).contains(card);
        }

        @Test
        @DisplayName("Should throw when rejecting without comment")
        void shouldThrowWhenRejectingWithoutComment() {
            // Given
            UUID approvalId = randomId();
            Approval approval = Approval.builder()
                    .id(approvalId)
                    .status("PENDING")
                    .build();
            when(approvalRepo.findById(approvalId)).thenReturn(Optional.of(approval));

            // When/Then
            assertThatThrownBy(() ->
                    engineService.rejectCard(approvalId, null, TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Comment is required for rejection");
        }

        @Test
        @DisplayName("Should throw when rejecting with blank comment")
        void shouldThrowWhenRejectingWithBlankComment() {
            // Given
            UUID approvalId = randomId();
            Approval approval = Approval.builder()
                    .id(approvalId)
                    .status("PENDING")
                    .build();
            when(approvalRepo.findById(approvalId)).thenReturn(Optional.of(approval));

            // When/Then
            assertThatThrownBy(() ->
                    engineService.rejectCard(approvalId, "   ", TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Comment is required for rejection");
        }

        @Test
        @DisplayName("Should throw when rejecting non-PENDING approval")
        void shouldThrowWhenRejectingNonPending() {
            // Given
            UUID approvalId = randomId();
            Approval approval = Approval.builder()
                    .id(approvalId)
                    .status("APPROVED")
                    .build();
            when(approvalRepo.findById(approvalId)).thenReturn(Optional.of(approval));

            // When/Then
            assertThatThrownBy(() ->
                    engineService.rejectCard(approvalId, "Reason", TEST_TENANT_ID, TEST_USER_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not in PENDING status");
        }
    }
}
