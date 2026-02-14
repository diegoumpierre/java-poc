package com.poc.kanban.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom metrics for Kanban Service
 * Tracks business-specific operations and events
 */
@Component
@Slf4j
public class KanbanMetrics {

    private final Counter boardsCreated;
    private final Counter boardsUpdated;
    private final Counter boardsDeleted;
    private final Counter listsCreated;
    private final Counter listsUpdated;
    private final Counter listsDeleted;
    private final Counter cardsCreated;
    private final Counter cardsUpdated;
    private final Counter cardsMoved;
    private final Counter cardsDeleted;
    private final Counter commentsAdded;
    private final Counter commentsUpdated;
    private final Counter commentsDeleted;
    private final Counter attachmentsUploaded;
    private final Counter subtasksCreated;
    private final Counter subtasksUpdated;
    private final Counter subtasksDeleted;
    private final Counter labelsCreated;
    private final Counter labelsUpdated;
    private final Counter labelsDeleted;
    private final Counter labelsAssigned;
    private final Counter labelsUnassigned;
    private final AtomicInteger activeBoards;
    private final Timer cardOperationDuration;

    public KanbanMetrics(MeterRegistry registry) {
        // Board operations
        this.boardsCreated = Counter.builder("kanban.boards.created")
                .description("Total number of boards created")
                .tag("service", "kanban")
                .register(registry);

        this.boardsUpdated = Counter.builder("kanban.boards.updated")
                .description("Total number of boards updated")
                .tag("service", "kanban")
                .register(registry);

        this.boardsDeleted = Counter.builder("kanban.boards.deleted")
                .description("Total number of boards deleted")
                .tag("service", "kanban")
                .register(registry);

        // List operations
        this.listsCreated = Counter.builder("kanban.lists.created")
                .description("Total number of lists created")
                .tag("service", "kanban")
                .register(registry);

        this.listsUpdated = Counter.builder("kanban.lists.updated")
                .description("Total number of lists updated")
                .tag("service", "kanban")
                .register(registry);

        this.listsDeleted = Counter.builder("kanban.lists.deleted")
                .description("Total number of lists deleted")
                .tag("service", "kanban")
                .register(registry);

        // Card operations
        this.cardsCreated = Counter.builder("kanban.cards.created")
                .description("Total number of cards created")
                .tag("service", "kanban")
                .register(registry);

        this.cardsUpdated = Counter.builder("kanban.cards.updated")
                .description("Total number of cards updated")
                .tag("service", "kanban")
                .register(registry);

        this.cardsMoved = Counter.builder("kanban.cards.moved")
                .description("Total number of cards moved between lists")
                .tag("service", "kanban")
                .register(registry);

        this.cardsDeleted = Counter.builder("kanban.cards.deleted")
                .description("Total number of cards deleted")
                .tag("service", "kanban")
                .register(registry);

        // Card details
        this.commentsAdded = Counter.builder("kanban.comments.added")
                .description("Total number of comments added to cards")
                .tag("service", "kanban")
                .register(registry);

        this.commentsUpdated = Counter.builder("kanban.comments.updated")
                .description("Total number of comments updated")
                .tag("service", "kanban")
                .register(registry);

        this.commentsDeleted = Counter.builder("kanban.comments.deleted")
                .description("Total number of comments deleted")
                .tag("service", "kanban")
                .register(registry);

        this.attachmentsUploaded = Counter.builder("kanban.attachments.uploaded")
                .description("Total number of attachments uploaded")
                .tag("service", "kanban")
                .register(registry);

        this.subtasksCreated = Counter.builder("kanban.subtasks.created")
                .description("Total number of subtasks created")
                .tag("service", "kanban")
                .register(registry);

        this.subtasksUpdated = Counter.builder("kanban.subtasks.updated")
                .description("Total number of subtasks updated")
                .tag("service", "kanban")
                .register(registry);

        this.subtasksDeleted = Counter.builder("kanban.subtasks.deleted")
                .description("Total number of subtasks deleted")
                .tag("service", "kanban")
                .register(registry);

        this.labelsCreated = Counter.builder("kanban.labels.created")
                .description("Total number of labels created")
                .tag("service", "kanban")
                .register(registry);

        this.labelsUpdated = Counter.builder("kanban.labels.updated")
                .description("Total number of labels updated")
                .tag("service", "kanban")
                .register(registry);

        this.labelsDeleted = Counter.builder("kanban.labels.deleted")
                .description("Total number of labels deleted")
                .tag("service", "kanban")
                .register(registry);

        this.labelsAssigned = Counter.builder("kanban.labels.assigned")
                .description("Total number of labels assigned to cards")
                .tag("service", "kanban")
                .register(registry);

        this.labelsUnassigned = Counter.builder("kanban.labels.unassigned")
                .description("Total number of labels removed from cards")
                .tag("service", "kanban")
                .register(registry);

        // Active boards (gauge)
        this.activeBoards = new AtomicInteger(0);
        registry.gauge("kanban.boards.active", activeBoards);

        // Card operation duration
        this.cardOperationDuration = Timer.builder("kanban.card.operation.duration")
                .description("Time taken to process card operations")
                .tag("service", "kanban")
                .register(registry);
    }

    // Board metrics
    public void recordBoardCreated() {
        boardsCreated.increment();
        log.debug("Board created recorded");
    }

    public void recordBoardUpdated() {
        boardsUpdated.increment();
        log.debug("Board updated recorded");
    }

    public void recordBoardDeleted() {
        boardsDeleted.increment();
        log.debug("Board deleted recorded");
    }

    // List metrics
    public void recordListCreated() {
        listsCreated.increment();
        log.debug("List created recorded");
    }

    public void recordListUpdated() {
        listsUpdated.increment();
        log.debug("List updated recorded");
    }

    public void recordListDeleted() {
        listsDeleted.increment();
        log.debug("List deleted recorded");
    }

    // Card metrics
    public void recordCardCreated() {
        cardsCreated.increment();
        log.debug("Card created recorded");
    }

    public void recordCardUpdated() {
        cardsUpdated.increment();
        log.debug("Card updated recorded");
    }

    public void recordCardMoved() {
        cardsMoved.increment();
        log.debug("Card moved recorded");
    }

    public void recordCardDeleted() {
        cardsDeleted.increment();
        log.debug("Card deleted recorded");
    }

    // Card details metrics
    public void recordCommentAdded() {
        commentsAdded.increment();
        log.debug("Comment added recorded");
    }

    public void recordCommentCreated() {
        commentsAdded.increment();
        log.debug("Comment created recorded");
    }

    public void recordCommentUpdated() {
        commentsUpdated.increment();
        log.debug("Comment updated recorded");
    }

    public void recordCommentDeleted() {
        commentsDeleted.increment();
        log.debug("Comment deleted recorded");
    }

    public void recordAttachmentUploaded() {
        attachmentsUploaded.increment();
        log.debug("Attachment uploaded recorded");
    }

    public void recordSubtaskCreated() {
        subtasksCreated.increment();
        log.debug("Subtask created recorded");
    }

    public void recordSubtaskUpdated() {
        subtasksUpdated.increment();
        log.debug("Subtask updated recorded");
    }

    public void recordSubtaskDeleted() {
        subtasksDeleted.increment();
        log.debug("Subtask deleted recorded");
    }

    // Label metrics
    public void recordLabelCreated() {
        labelsCreated.increment();
        log.debug("Label created recorded");
    }

    public void recordLabelUpdated() {
        labelsUpdated.increment();
        log.debug("Label updated recorded");
    }

    public void recordLabelDeleted() {
        labelsDeleted.increment();
        log.debug("Label deleted recorded");
    }

    public void recordLabelAssigned() {
        labelsAssigned.increment();
        log.debug("Label assigned recorded");
    }

    public void recordLabelUnassigned() {
        labelsUnassigned.increment();
        log.debug("Label unassigned recorded");
    }

    // Active boards metrics
    public void incrementActiveBoards() {
        activeBoards.incrementAndGet();
        log.debug("Active boards: {}", activeBoards.get());
    }

    public void decrementActiveBoards() {
        int current = activeBoards.get();
        if (current > 0) {
            activeBoards.decrementAndGet();
            log.debug("Active boards: {}", activeBoards.get());
        }
    }

    public int getActiveBoards() {
        return activeBoards.get();
    }

    // Timer for card operations
    public Timer.Sample startCardOperationTimer() {
        return Timer.start();
    }

    public void recordCardOperationDuration(Timer.Sample sample) {
        sample.stop(cardOperationDuration);
    }
}
