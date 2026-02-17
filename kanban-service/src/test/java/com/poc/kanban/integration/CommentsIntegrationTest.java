package com.poc.kanban.integration;

import com.poc.kanban.model.KanbanBoardModel;
import com.poc.kanban.model.KanbanCardModel;
import com.poc.kanban.model.KanbanCommentModel;
import com.poc.kanban.service.KanbanBoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for Comments functionality
 */
@DisplayName("Comments Integration Tests")
class CommentsIntegrationTest extends BaseIntegrationTest {

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
                .description("Test Description")
                .build();

        KanbanCardModel createdCard = kanbanBoardService.addCard(boardId, listId, userId, cardModel);
        cardId = createdCard.getId();
    }

    @Test
    @DisplayName("Should add comment to card")
    void shouldAddComment() {
        // Given
        String commentText = "This is a test comment";

        // When
        KanbanCommentModel comment = kanbanBoardService.addComment(cardId, userId, commentText);

        // Then
        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isNotNull();
        assertThat(comment.getText()).isEqualTo(commentText);
        assertThat(comment.getUserId()).isEqualTo(userId);
        assertThat(comment.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update comment")
    void shouldUpdateComment() {
        // Given - Create comment first
        KanbanCommentModel originalComment = kanbanBoardService.addComment(
                cardId, userId, "Original comment");
        UUID commentId = originalComment.getId();

        // When - Update comment
        String updatedText = "Updated comment text";
        KanbanCommentModel updatedComment = kanbanBoardService.updateComment(
                cardId, commentId, userId, updatedText);

        // Then
        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getId()).isEqualTo(commentId);
        assertThat(updatedComment.getText()).isEqualTo(updatedText);
    }

    @Test
    @DisplayName("Should delete comment")
    void shouldDeleteComment() {
        // Given - Create comment first
        KanbanCommentModel comment = kanbanBoardService.addComment(
                cardId, userId, "Comment to delete");
        UUID commentId = comment.getId();

        // Verify comment was added
        KanbanBoardModel boardBefore = kanbanBoardService.findByIdAndUserId(boardId, userId);
        KanbanCardModel cardBefore = boardBefore.getLists().get(0).getCards().get(0);
        assertThat(cardBefore.getComments()).hasSize(1);

        // When - Delete comment
        kanbanBoardService.deleteComment(cardId, commentId, userId);

        // Then - Verify comment is deleted by checking board state
        KanbanBoardModel board = kanbanBoardService.findByIdAndUserId(boardId, userId);
        KanbanCardModel card = board.getLists().get(0).getCards().get(0);
        assertThat(card.getComments()).isEmpty();
    }

    @Test
    @DisplayName("Should add multiple comments to same card")
    void shouldAddMultipleComments() {
        // Given
        String comment1Text = "First comment";
        String comment2Text = "Second comment";

        // When
        KanbanCommentModel comment1 = kanbanBoardService.addComment(cardId, userId, comment1Text);
        KanbanCommentModel comment2 = kanbanBoardService.addComment(cardId, userId, comment2Text);

        // Then
        KanbanBoardModel board = kanbanBoardService.findByIdAndUserId(boardId, userId);
        KanbanCardModel card = board.getLists().get(0).getCards().get(0);

        assertThat(card.getComments()).hasSize(2);
        assertThat(card.getComments())
                .extracting("text")
                .containsExactlyInAnyOrder(comment1Text, comment2Text);
    }

    @Test
    @DisplayName("Should preserve other comments when deleting one")
    void shouldPreserveOtherCommentsWhenDeletingOne() {
        // Given - Create multiple comments
        KanbanCommentModel comment1 = kanbanBoardService.addComment(cardId, userId, "Comment 1");
        KanbanCommentModel comment2 = kanbanBoardService.addComment(cardId, userId, "Comment 2");
        KanbanCommentModel comment3 = kanbanBoardService.addComment(cardId, userId, "Comment 3");

        // When - Delete middle comment
        kanbanBoardService.deleteComment(cardId, comment2.getId(), userId);

        // Then - Other comments should still exist
        KanbanBoardModel board = kanbanBoardService.findByIdAndUserId(boardId, userId);
        KanbanCardModel card = board.getLists().get(0).getCards().get(0);

        assertThat(card.getComments()).hasSize(2);
        assertThat(card.getComments())
                .extracting("text")
                .containsExactlyInAnyOrder("Comment 1", "Comment 3");
    }

    @Test
    @DisplayName("Should throw exception when adding comment to non-existent card")
    void shouldThrowExceptionWhenCardNotFound() {
        // Given
        UUID nonExistentCardId = UUID.randomUUID();

        // When/Then
        assertThatThrownBy(() ->
                kanbanBoardService.addComment(nonExistentCardId, userId, "Test comment")
        ).isInstanceOf(NoSuchElementException.class)
         .hasMessageContaining("Card not found");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent comment")
    void shouldThrowExceptionWhenUpdatingNonExistentComment() {
        // Given
        UUID nonExistentCommentId = UUID.randomUUID();

        // When/Then
        assertThatThrownBy(() ->
                kanbanBoardService.updateComment(cardId, nonExistentCommentId, userId, "Updated text")
        ).isInstanceOf(NoSuchElementException.class)
         .hasMessageContaining("Comment not found");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent comment")
    void shouldThrowExceptionWhenDeletingNonExistentComment() {
        // Given
        UUID nonExistentCommentId = UUID.randomUUID();

        // When/Then
        assertThatThrownBy(() ->
                kanbanBoardService.deleteComment(cardId, nonExistentCommentId, userId)
        ).isInstanceOf(NoSuchElementException.class)
         .hasMessageContaining("Comment not found");
    }
}
