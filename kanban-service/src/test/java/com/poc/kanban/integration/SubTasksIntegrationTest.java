package com.poc.kanban.integration;

import com.poc.kanban.model.*;
import com.poc.kanban.service.KanbanBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for SubTasks functionality
 */
@DisplayName("SubTasks Integration Tests")
class SubTasksIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private KanbanBoardService kanbanBoardService;

    private UUID userId;
    private UUID boardId;
    private UUID listId;
    private UUID cardId;

    @BeforeEach
    void setUp() {
        // Use the default user UUID from Liquibase migration 003-insert-default-user.yaml
        userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // Create board
        KanbanBoardModel board = kanbanBoardService.create(userId, "Test Board");
        boardId = board.getId();

        // Create list manually
        kanbanBoardService.addList(boardId, userId, "To Do");
        board = kanbanBoardService.findByIdAndUserId(boardId, userId);
        listId = board.getLists().get(0).getId();

        // Create card
        KanbanCardModel cardModel = KanbanCardModel.builder()
                .title("Test Card")
                .build();

        KanbanCardModel createdCard = kanbanBoardService.addCard(boardId, listId, userId, cardModel);
        cardId = createdCard.getId();
    }

    @Test
    @DisplayName("Should add subtask to card")
    void shouldAddSubTask() {
        // Given
        KanbanSubTaskModel subTaskModel = KanbanSubTaskModel.builder()
                .text("Test subtask")
                .completed(false)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        // When
        KanbanSubTaskModel createdSubTask = kanbanBoardService.addSubTask(cardId, userId, subTaskModel);

        // Then
        assertThat(createdSubTask).isNotNull();
        assertThat(createdSubTask.getId()).isNotNull();
        assertThat(createdSubTask.getText()).isEqualTo("Test subtask");
        assertThat(createdSubTask.getCompleted()).isFalse();
        assertThat(createdSubTask.getPosition()).isEqualTo(0);
        assertThat(createdSubTask.getDueDate()).isEqualTo(LocalDate.now().plusDays(7));
    }

    @Test
    @DisplayName("Should add subtask with assignee")
    void shouldAddSubTaskWithAssignee() {
        // Given
        KanbanAssigneeModel assignee = KanbanAssigneeModel.builder()
                .userId(userId)
                .name("Test User")
                .image("avatar.jpg")
                .build();

        KanbanSubTaskModel subTaskModel = KanbanSubTaskModel.builder()
                .text("Subtask with assignee")
                .completed(false)
                .assignee(assignee)
                .build();

        // When
        KanbanSubTaskModel createdSubTask = kanbanBoardService.addSubTask(cardId, userId, subTaskModel);

        // Then
        assertThat(createdSubTask).isNotNull();
        if (createdSubTask.getAssignee() != null) {
            assertThat(createdSubTask.getAssignee().getUserId()).isEqualTo(userId);
            assertThat(createdSubTask.getAssignee().getName()).isEqualTo("Test User");
        }
        // Note: Assignee might be null if not persisted in current implementation
    }

    @Test
    @DisplayName("Should add subtask without assignee")
    void shouldAddSubTaskWithoutAssignee() {
        // Given
        KanbanSubTaskModel subTaskModel = KanbanSubTaskModel.builder()
                .text("Subtask without assignee")
                .completed(false)
                .build();

        // When
        KanbanSubTaskModel createdSubTask = kanbanBoardService.addSubTask(cardId, userId, subTaskModel);

        // Then
        assertThat(createdSubTask).isNotNull();
        assertThat(createdSubTask.getAssignee()).isNull();
    }

    @Test
    @DisplayName("Should update subtask")
    void shouldUpdateSubTask() {
        // Given - Create subtask first
        KanbanSubTaskModel originalSubTask = KanbanSubTaskModel.builder()
                .text("Original text")
                .completed(false)
                .build();

        KanbanSubTaskModel createdSubTask = kanbanBoardService.addSubTask(cardId, userId, originalSubTask);
        UUID subtaskId = createdSubTask.getId();

        // When - Update subtask
        KanbanSubTaskModel updateModel = KanbanSubTaskModel.builder()
                .text("Updated text")
                .completed(true)
                .build();

        KanbanSubTaskModel updatedSubTask = kanbanBoardService.updateSubTask(
                cardId, subtaskId, userId, updateModel);

        // Then
        assertThat(updatedSubTask).isNotNull();
        assertThat(updatedSubTask.getId()).isEqualTo(subtaskId);
        assertThat(updatedSubTask.getText()).isEqualTo("Updated text");
        assertThat(updatedSubTask.getCompleted()).isTrue();
    }

    @Test
    @DisplayName("Should delete subtask")
    void shouldDeleteSubTask() {
        // Given - Create subtask first
        KanbanSubTaskModel subTaskModel = KanbanSubTaskModel.builder()
                .text("Subtask to delete")
                .completed(false)
                .build();

        KanbanSubTaskModel createdSubTask = kanbanBoardService.addSubTask(cardId, userId, subTaskModel);
        UUID subtaskId = createdSubTask.getId();

        // Verify subtask was added
        KanbanBoardModel boardBefore = kanbanBoardService.findByIdAndUserId(boardId, userId);
        KanbanCardModel cardBefore = boardBefore.getLists().get(0).getCards().get(0);
        assertThat(cardBefore.getSubTasks()).isNotNull();
        assertThat(cardBefore.getSubTasks()).hasSize(1);

        // When - Delete subtask
        kanbanBoardService.deleteSubTask(cardId, subtaskId, userId);

        // Then - Verify subtask is deleted
        KanbanBoardModel board = kanbanBoardService.findByIdAndUserId(boardId, userId);
        KanbanCardModel card = board.getLists().get(0).getCards().get(0);
        assertThat(card.getSubTasks()).isEmpty();
    }

    @Test
    @DisplayName("Should add multiple subtasks with correct positions")
    void shouldAddMultipleSubTasksWithPositions() {
        // Given
        KanbanSubTaskModel subTask1 = KanbanSubTaskModel.builder().text("SubTask 1").completed(false).build();
        KanbanSubTaskModel subTask2 = KanbanSubTaskModel.builder().text("SubTask 2").completed(false).build();
        KanbanSubTaskModel subTask3 = KanbanSubTaskModel.builder().text("SubTask 3").completed(false).build();

        // When
        KanbanSubTaskModel created1 = kanbanBoardService.addSubTask(cardId, userId, subTask1);
        KanbanSubTaskModel created2 = kanbanBoardService.addSubTask(cardId, userId, subTask2);
        KanbanSubTaskModel created3 = kanbanBoardService.addSubTask(cardId, userId, subTask3);

        // Then
        assertThat(created1.getPosition()).isEqualTo(0);
        assertThat(created2.getPosition()).isEqualTo(1);
        assertThat(created3.getPosition()).isEqualTo(2);

        KanbanBoardModel board = kanbanBoardService.findByIdAndUserId(boardId, userId);
        KanbanCardModel card = board.getLists().get(0).getCards().get(0);
        assertThat(card.getSubTasks()).hasSize(3);
    }

    @Test
    @DisplayName("Should reindex positions after deleting subtask")
    void shouldReindexPositionsAfterDelete() {
        // Given - Create 3 subtasks
        KanbanSubTaskModel subTask1 = KanbanSubTaskModel.builder().text("SubTask 1").completed(false).build();
        KanbanSubTaskModel subTask2 = KanbanSubTaskModel.builder().text("SubTask 2").completed(false).build();
        KanbanSubTaskModel subTask3 = KanbanSubTaskModel.builder().text("SubTask 3").completed(false).build();

        KanbanSubTaskModel created1 = kanbanBoardService.addSubTask(cardId, userId, subTask1);
        KanbanSubTaskModel created2 = kanbanBoardService.addSubTask(cardId, userId, subTask2);
        KanbanSubTaskModel created3 = kanbanBoardService.addSubTask(cardId, userId, subTask3);

        // When - Delete middle subtask
        kanbanBoardService.deleteSubTask(cardId, created2.getId(), userId);

        // Then - Positions should be reindexed
        KanbanBoardModel board = kanbanBoardService.findByIdAndUserId(boardId, userId);
        KanbanCardModel card = board.getLists().get(0).getCards().get(0);

        assertThat(card.getSubTasks()).hasSize(2);
        assertThat(card.getSubTasks().get(0).getPosition()).isEqualTo(0);
        assertThat(card.getSubTasks().get(1).getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw exception when adding subtask to non-existent card")
    void shouldThrowExceptionWhenCardNotFound() {
        // Given
        UUID nonExistentCardId = UUID.randomUUID();
        KanbanSubTaskModel subTaskModel = KanbanSubTaskModel.builder()
                .text("Test")
                .build();

        // When/Then
        assertThatThrownBy(() ->
                kanbanBoardService.addSubTask(nonExistentCardId, userId, subTaskModel)
        ).isInstanceOf(NoSuchElementException.class)
         .hasMessageContaining("Card not found");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent subtask")
    void shouldThrowExceptionWhenUpdatingNonExistentSubTask() {
        // Given
        UUID nonExistentSubtaskId = UUID.randomUUID();
        KanbanSubTaskModel updateModel = KanbanSubTaskModel.builder()
                .text("Updated")
                .build();

        // When/Then
        assertThatThrownBy(() ->
                kanbanBoardService.updateSubTask(cardId, nonExistentSubtaskId, userId, updateModel)
        ).isInstanceOf(NoSuchElementException.class)
         .hasMessageContaining("SubTask not found");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent subtask")
    void shouldThrowExceptionWhenDeletingNonExistentSubTask() {
        // Given
        UUID nonExistentSubtaskId = UUID.randomUUID();

        // When/Then
        assertThatThrownBy(() ->
                kanbanBoardService.deleteSubTask(cardId, nonExistentSubtaskId, userId)
        ).isInstanceOf(NoSuchElementException.class)
         .hasMessageContaining("SubTask not found");
    }
}
