package com.poc.kanban.service.impl;

import com.poc.shared.tenant.TenantContext;
import com.poc.kanban.converter.*;
import com.poc.kanban.domain.*;
import com.poc.kanban.model.*;
import com.poc.kanban.metrics.KanbanMetrics;
import com.poc.kanban.repository.KanbanBoardRepository;
import com.poc.kanban.repository.jpa.*;
import com.poc.kanban.service.KanbanBoardService;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KanbanBoardServiceImpl implements KanbanBoardService {

    private final KanbanBoardRepository kanbanBoardRepository;
    private final JpaRepositoryKanbanBoard jpaRepositoryKanbanBoard;
    private final KanbanMetrics kanbanMetrics;

    // Converters for complex operations
    private final KanbanBoardToKanbanBoardModelConverter boardToModelConverter;
    private final KanbanBoardModelToKanbanBoardConverter modelToBoardConverter;
    private final KanbanListToKanbanListModelConverter listToModelConverter;
    private final KanbanCardToKanbanCardModelConverter cardToModelConverter;
    private final KanbanCardModelToKanbanCardConverter modelToCardConverter;

    @Override
    public List<KanbanBoardModel> findAllByUserId(UUID userId) {
        log.info("Finding boards for user: {}", userId);
        UUID tenantId = TenantContext.getCurrentTenant();

        List<KanbanBoardModel> boards;
        if (tenantId != null) {
            log.debug("Filtering boards by tenant: {}", tenantId);
            boards = kanbanBoardRepository.findByUserIdAndTenantId(userId, tenantId);
        } else {
            boards = kanbanBoardRepository.findByUserId(userId);
        }
        log.info("Found {} boards for user: {}", boards.size(), userId);

        if (boards.isEmpty()) {
            log.info("No boards found, creating sample board for user: {}", userId);
            KanbanBoardModel sampleBoard = createSampleBoard(userId);
            log.info("Created sample board with id: {}", sampleBoard.getId());
            boards = List.of(sampleBoard);
        }

        return boards;
    }

    @Override
    @Transactional(readOnly = true)
    public KanbanBoardModel findByIdAndUserId(UUID id, UUID userId) {
        return kanbanBoardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
    }

    @Override
    public KanbanBoardModel create(UUID userId, String title) {
        // Validate unique title
        String trimmedTitle = title != null ? title.trim() : "";
        if (trimmedTitle.isEmpty()) {
            throw new IllegalArgumentException("Board title cannot be empty");
        }

        java.util.Optional<KanbanBoard> existing = jpaRepositoryKanbanBoard.findByTitleAndUserId(trimmedTitle, userId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A board with this name already exists");
        }

        KanbanBoardModel boardModel = KanbanBoardModel.builder()
                .title(trimmedTitle)
                .boardCode(generateBoardCode(trimmedTitle))
                .userId(userId)
                .tenantId(TenantContext.getCurrentTenant())
                .lists(new ArrayList<>())
                .build();

        KanbanBoardModel saved = kanbanBoardRepository.save(boardModel);
        kanbanMetrics.recordBoardCreated();
        kanbanMetrics.incrementActiveBoards();

        return saved;
    }

    @Override
    public KanbanBoardModel update(UUID id, UUID userId, String title) {
        KanbanBoardModel board = kanbanBoardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        // Validate unique title (exclude current board)
        String trimmedTitle = title != null ? title.trim() : "";
        if (trimmedTitle.isEmpty()) {
            throw new IllegalArgumentException("Board title cannot be empty");
        }

        java.util.Optional<KanbanBoard> existing = jpaRepositoryKanbanBoard.findByTitleAndUserIdExcludingBoard(trimmedTitle, userId, id);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A board with this name already exists");
        }

        board.setTitle(trimmedTitle);
        KanbanBoardModel updated = kanbanBoardRepository.save(board);
        kanbanMetrics.recordBoardUpdated();

        return updated;
    }

    @Override
    public void delete(UUID id, UUID userId) {
        KanbanBoardModel board = kanbanBoardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        kanbanBoardRepository.delete(board);
        kanbanMetrics.recordBoardDeleted();
        kanbanMetrics.decrementActiveBoards();
    }

    @Override
    public KanbanListModel addList(UUID boardId, UUID userId, String title) {
        // Verify access
        jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        Integer maxPosition = kanbanBoardRepository.findMaxListPositionByBoardId(boardId);

        // Get board domain for aggregate manipulation
        KanbanBoard board = jpaRepositoryKanbanBoard.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        KanbanList list = KanbanList.builder()
                .title(title)
                .position(maxPosition + 1)
                .cards(new ArrayList<>())
                .build();

        board.getLists().add(list);
        KanbanBoard savedBoard = jpaRepositoryKanbanBoard.save(board);

        kanbanMetrics.recordListCreated();

        // Get the saved list (last one added)
        KanbanList savedList = savedBoard.getLists().get(savedBoard.getLists().size() - 1);
        return listToModelConverter.convert(savedList);
    }

    @Override
    public KanbanListModel updateListTitle(UUID boardId, UUID listId, UUID userId, String title) {
        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        KanbanList list = board.getLists().stream()
                .filter(l -> l.getId().equals(listId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("List not found"));

        list.setTitle(title);
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordListUpdated();
        return listToModelConverter.convert(list);
    }

    @Override
    public void deleteList(UUID boardId, UUID listId, UUID userId) {
        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        board.getLists().removeIf(l -> l.getId().equals(listId));
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordListDeleted();
    }

    @Override
    public KanbanCardModel addCard(UUID boardId, UUID listId, UUID userId, KanbanCardModel cardModel) {
        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        KanbanList list = board.getLists().stream()
                .filter(l -> l.getId().equals(listId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("List not found"));

        Integer maxPosition = list.getCards() != null ?
                list.getCards().stream().mapToInt(KanbanCard::getPosition).max().orElse(-1) : -1;

        // Get next card number for the board
        Integer maxCardNumber = board.getLists().stream()
                .flatMap(l -> l.getCards().stream())
                .map(KanbanCard::getCardNumber)
                .filter(num -> num != null)
                .max(Integer::compareTo)
                .orElse(0);

        // Convert model to domain and set position
        KanbanCard card = modelToCardConverter.convert(cardModel);
        if (card != null) {
            // UUID is auto-generated by @Builder.Default if null
            card.setPosition(maxPosition + 1);
            card.setCardNumber(maxCardNumber + 1);

            list.getCards().add(card);
            KanbanBoard savedBoard = jpaRepositoryKanbanBoard.save(board);

            kanbanMetrics.recordCardCreated();

            // Find the saved card
            KanbanList savedList = savedBoard.getLists().stream()
                    .filter(l -> l.getId().equals(listId))
                    .findFirst()
                    .orElseThrow();
            KanbanCard savedCard = savedList.getCards().get(savedList.getCards().size() - 1);

            return cardToModelConverter.convert(savedCard);
        }
        return null;
    }

    @Override
    public KanbanCardModel updateCard(UUID boardId, UUID listId, UUID cardId, UUID userId, KanbanCardModel cardModel) {
        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        KanbanCard card = board.getLists().stream()
                .flatMap(l -> l.getCards().stream())
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        // Update card fields from model
        card.setTitle(cardModel.getTitle());
        card.setDescription(cardModel.getDescription());
        card.setStartDate(cardModel.getStartDate());
        card.setDueDate(cardModel.getDueDate());
        card.setCompleted(cardModel.getCompleted() != null ? cardModel.getCompleted() : card.getCompleted());
        card.setProgress(cardModel.getProgress() != null ? cardModel.getProgress() : card.getProgress());

        if (cardModel.getPriority() != null) {
            card.setPriorityColor(cardModel.getPriority().getColor());
            card.setPriorityTitle(cardModel.getPriority().getTitle());
        }

        if (cardModel.getAttachments() != null) {
            card.setAttachments(cardModel.getAttachments());
        }

        // Update subtasks if provided
        int oldSubtaskCount = card.getSubTasks().size();
        if (cardModel.getSubTasks() != null && !cardModel.getSubTasks().isEmpty()) {
            card.getSubTasks().clear();
            int pos = 0;
            for (KanbanSubTaskModel subTaskModel : cardModel.getSubTasks()) {
                KanbanSubTask subTask = KanbanSubTask.builder()
                        .text(subTaskModel.getText())
                        .completed(subTaskModel.getCompleted() != null ? subTaskModel.getCompleted() : false)
                        .position(pos++)
                        .build();
                card.getSubTasks().add(subTask);
            }
            // Record new subtasks
            int newSubtaskCount = card.getSubTasks().size();
            if (newSubtaskCount > oldSubtaskCount) {
                for (int i = 0; i < (newSubtaskCount - oldSubtaskCount); i++) {
                    kanbanMetrics.recordSubtaskCreated();
                }
            }
        }

        // Update acceptance criteria if provided
        if (cardModel.getAcceptanceCriteria() != null && !cardModel.getAcceptanceCriteria().isEmpty()) {
            card.getAcceptanceCriteria().clear();
            int acPos = 0;
            for (KanbanAcceptanceCriteriaModel acModel : cardModel.getAcceptanceCriteria()) {
                KanbanAcceptanceCriteria ac = KanbanAcceptanceCriteria.builder()
                        .text(acModel.getText())
                        .completed(acModel.getCompleted() != null ? acModel.getCompleted() : false)
                        .position(acPos++)
                        .build();
                card.getAcceptanceCriteria().add(ac);
            }
        }

        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordCardUpdated();

        return cardToModelConverter.convert(card);
    }

    @Override
    public void deleteCard(UUID boardId, UUID listId, UUID cardId, UUID userId) {
        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        for (KanbanList list : board.getLists()) {
            list.getCards().removeIf(c -> c.getId().equals(cardId));
        }
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordCardDeleted();
    }

    @Override
    public void moveCard(UUID boardId, UUID sourceListId, UUID targetListId, UUID cardId, UUID userId, Integer targetIndex) {
        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        // Find source list and card
        KanbanList sourceList = board.getLists().stream()
                .filter(l -> l.getId().equals(sourceListId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Source list not found"));

        KanbanCard card = sourceList.getCards().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        // Remove from source list
        sourceList.getCards().remove(card);

        // Find target list
        KanbanList targetList = board.getLists().stream()
                .filter(l -> l.getId().equals(targetListId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Target list not found"));

        // Set new position
        int newPosition = targetIndex != null ? targetIndex : targetList.getCards().size();
        card.setPosition(newPosition);

        // Update positions of other cards
        for (int i = 0; i < targetList.getCards().size(); i++) {
            if (targetList.getCards().get(i).getPosition() >= newPosition) {
                targetList.getCards().get(i).setPosition(targetList.getCards().get(i).getPosition() + 1);
            }
        }

        // Add to target list
        targetList.getCards().add(card);

        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordCardMoved();
    }

    @Override
    public KanbanBoardModel saveBoardState(UUID userId, KanbanBoardModel boardModel) {
        KanbanBoard board;

        if (boardModel.getId() != null) {
            board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardModel.getId(), userId)
                    .orElseThrow(() -> new NoSuchElementException("Board not found"));
            board.setTitle(boardModel.getTitle());
            board.getLists().clear();
        } else {
            board = KanbanBoard.builder()
                    .id(UUID.randomUUID())
                    .title(boardModel.getTitle())
                    .userId(userId)
                    .lists(new ArrayList<>())
                    .isNew(true)
                    .build();
        }

        // Convert and add lists from model using converter
        KanbanBoard convertedBoard = modelToBoardConverter.convert(boardModel);
        if (convertedBoard != null && convertedBoard.getLists() != null) {
            // Update positions (UUIDs are auto-generated by @Builder.Default if null)
            int listPos = 0;
            for (KanbanList list : convertedBoard.getLists()) {
                list.setPosition(listPos++);

                int cardPos = 0;
                for (KanbanCard card : list.getCards()) {
                    card.setPosition(cardPos++);

                    int subPos = 0;
                    for (KanbanSubTask subTask : card.getSubTasks()) {
                        subTask.setPosition(subPos++);
                    }
                }
                board.getLists().add(list);
            }
        }

        KanbanBoard savedBoard = jpaRepositoryKanbanBoard.save(board);
        return boardToModelConverter.convert(savedBoard);
    }

    private KanbanBoardModel createSampleBoard(UUID userId) {
        // Create the board
        KanbanBoard board = KanbanBoard.builder()
                .id(UUID.randomUUID())
                .title("My Kanban Board")
                .boardCode(generateBoardCode("My Kanban Board"))
                .userId(userId)
                .tenantId(TenantContext.getCurrentTenant())
                .lists(new ArrayList<>())
                .isNew(true)
                .build();

        // Create "To Do" list with sample cards
        KanbanList todoList = KanbanList.builder()
                .title("To Do")
                .position(0)
                .cards(new ArrayList<>())
                .build();

        KanbanCard card1 = KanbanCard.builder()
                .title("Research competitors")
                .description("Analyze main competitors and their features")
                .position(0)
                .cardNumber(1)
                .priorityColor("#22C55E")
                .priorityTitle("Low")
                .dueDate(LocalDate.now().plusDays(7))
                .completed(false)
                .progress(0)
                .attachments(0)
                .assigneeUserId(userId)
                .subTasks(new ArrayList<>())
                .comments(new ArrayList<>())
                                .build();

        // Add subtasks to card1
        KanbanSubTask card1SubTask1 = KanbanSubTask.builder()
                .text("Identify top 5 competitors")
                .completed(false)
                .position(0)
                .dueDate(LocalDate.now().plusDays(3))
                .build();
        KanbanSubTask card1SubTask2 = KanbanSubTask.builder()
                .text("List their main features")
                .completed(false)
                .position(1)
                .build();
        KanbanSubTask card1SubTask3 = KanbanSubTask.builder()
                .text("Create comparison matrix")
                .completed(false)
                .position(2)
                .build();
        card1.getSubTasks().add(card1SubTask1);
        card1.getSubTasks().add(card1SubTask2);
        card1.getSubTasks().add(card1SubTask3);

        // Add comments to card1
        KanbanComment card1Comment1 = KanbanComment.builder()
                .text("Focus on features related to our target market")
                .userId(userId)
                .createdAt(Instant.now().minusSeconds(3600))
                .build();
        card1.getComments().add(card1Comment1);

        KanbanCard card2 = KanbanCard.builder()
                .title("Create wireframes")
                .description("Design initial wireframes for the new feature")
                .position(1)
                .cardNumber(2)
                .priorityColor("#F59E0B")
                .priorityTitle("Medium")
                .dueDate(LocalDate.now().plusDays(5))
                .completed(false)
                .progress(0)
                .attachments(0)
                .assigneeUserId(userId)
                .subTasks(new ArrayList<>())
                .comments(new ArrayList<>())
                                .build();

        // Add subtasks to card2
        KanbanSubTask card2SubTask1 = KanbanSubTask.builder()
                .text("Sketch main user flows")
                .completed(false)
                .position(0)
                .build();
        KanbanSubTask card2SubTask2 = KanbanSubTask.builder()
                .text("Create low-fidelity wireframes")
                .completed(false)
                .position(1)
                .dueDate(LocalDate.now().plusDays(2))
                .build();
        card2.getSubTasks().add(card2SubTask1);
        card2.getSubTasks().add(card2SubTask2);

        // Add comments to card2
        KanbanComment card2Comment1 = KanbanComment.builder()
                .text("Let's use Figma for this")
                .userId(userId)
                .createdAt(Instant.now().minusSeconds(7200))
                .build();
        KanbanComment card2Comment2 = KanbanComment.builder()
                .text("Don't forget mobile responsive views")
                .userId(userId)
                .createdAt(Instant.now().minusSeconds(1800))
                .build();
        card2.getComments().add(card2Comment1);
        card2.getComments().add(card2Comment2);

        todoList.getCards().add(card1);
        todoList.getCards().add(card2);

        // Create "In Progress" list with sample cards
        KanbanList inProgressList = KanbanList.builder()
                .title("In Progress")
                .position(1)
                .cards(new ArrayList<>())
                .build();

        KanbanCard card3 = KanbanCard.builder()
                .title("Implement authentication")
                .description("Add JWT authentication to the API")
                .position(0)
                .cardNumber(3)
                .priorityColor("#EF4444")
                .priorityTitle("High")
                .startDate(LocalDate.now().minusDays(2))
                .dueDate(LocalDate.now().plusDays(3))
                .completed(false)
                .progress(50)
                .attachments(2)
                .assigneeUserId(userId)
                .subTasks(new ArrayList<>())
                .comments(new ArrayList<>())
                                .build();

        // Add subtasks to card3
        KanbanSubTask subTask1 = KanbanSubTask.builder()
                .text("Configure JWT library")
                .completed(true)
                .position(0)
                .build();
        KanbanSubTask subTask2 = KanbanSubTask.builder()
                .text("Create login endpoint")
                .completed(true)
                .position(1)
                .build();
        KanbanSubTask subTask3 = KanbanSubTask.builder()
                .text("Add token validation")
                .completed(false)
                .position(2)
                .dueDate(LocalDate.now().plusDays(1))
                .build();
        KanbanSubTask subTask4 = KanbanSubTask.builder()
                .text("Write unit tests")
                .completed(false)
                .position(3)
                .build();
        card3.getSubTasks().add(subTask1);
        card3.getSubTasks().add(subTask2);
        card3.getSubTasks().add(subTask3);
        card3.getSubTasks().add(subTask4);

        // Add comments to card3
        KanbanComment card3Comment1 = KanbanComment.builder()
                .text("Using JJWT library version 0.12.5")
                .userId(userId)
                .createdAt(Instant.now().minusSeconds(172800))
                .build();
        KanbanComment card3Comment2 = KanbanComment.builder()
                .text("Login endpoint is working, now moving to validation")
                .userId(userId)
                .createdAt(Instant.now().minusSeconds(86400))
                .build();
        KanbanComment card3Comment3 = KanbanComment.builder()
                .text("Should we add refresh token support?")
                .userId(userId)
                .createdAt(Instant.now().minusSeconds(3600))
                .build();
        card3.getComments().add(card3Comment1);
        card3.getComments().add(card3Comment2);
        card3.getComments().add(card3Comment3);

        inProgressList.getCards().add(card3);

        // Create "Done" list with sample cards
        KanbanList doneList = KanbanList.builder()
                .title("Done")
                .position(2)
                .cards(new ArrayList<>())
                .build();

        KanbanCard card4 = KanbanCard.builder()
                .title("Setup project structure")
                .description("Initialize the project with required dependencies")
                .position(0)
                .cardNumber(4)
                .priorityColor("#22C55E")
                .priorityTitle("Low")
                .startDate(LocalDate.now().minusDays(7))
                .dueDate(LocalDate.now().minusDays(5))
                .completed(true)
                .progress(100)
                .attachments(1)
                .assigneeUserId(userId)
                .subTasks(new ArrayList<>())
                .comments(new ArrayList<>())
                                .build();

        // Add subtasks to card4 (all completed)
        KanbanSubTask card4SubTask1 = KanbanSubTask.builder()
                .text("Create Maven multi-module project")
                .completed(true)
                .position(0)
                .build();
        KanbanSubTask card4SubTask2 = KanbanSubTask.builder()
                .text("Add Spring Boot dependencies")
                .completed(true)
                .position(1)
                .build();
        KanbanSubTask card4SubTask3 = KanbanSubTask.builder()
                .text("Configure database connection")
                .completed(true)
                .position(2)
                .build();
        KanbanSubTask card4SubTask4 = KanbanSubTask.builder()
                .text("Setup Liquibase migrations")
                .completed(true)
                .position(3)
                .build();
        card4.getSubTasks().add(card4SubTask1);
        card4.getSubTasks().add(card4SubTask2);
        card4.getSubTasks().add(card4SubTask3);
        card4.getSubTasks().add(card4SubTask4);

        // Add comments to card4
        KanbanComment card4Comment1 = KanbanComment.builder()
                .text("Project structure is ready!")
                .userId(userId)
                .createdAt(Instant.now().minusSeconds(432000))
                .build();
        KanbanComment card4Comment2 = KanbanComment.builder()
                .text("All dependencies installed successfully")
                .userId(userId)
                .createdAt(Instant.now().minusSeconds(345600))
                .build();
        card4.getComments().add(card4Comment1);
        card4.getComments().add(card4Comment2);

        doneList.getCards().add(card4);

        // Add lists to board
        board.getLists().add(todoList);
        board.getLists().add(inProgressList);
        board.getLists().add(doneList);

        // Save and return
        KanbanBoard savedBoard = jpaRepositoryKanbanBoard.save(board);
        return boardToModelConverter.convert(savedBoard);
    }

    private String generateBoardCode(String title) {
        if (title == null || title.isEmpty()) {
            return "BORD";
        }

        // Remove special characters and split by spaces
        String cleanTitle = title.replaceAll("[^a-zA-Z0-9\\s]", "");
        String[] words = cleanTitle.split("\\s+");

        StringBuilder code = new StringBuilder();

        // Try to get first letter of each word (up to 4 letters)
        for (String word : words) {
            if (code.length() >= 4) {
                break;
            }
            if (!word.isEmpty()) {
                code.append(word.substring(0, 1).toUpperCase());
            }
        }

        // If we don't have 4 letters, get more from the first word
        if (code.length() < 4 && words.length > 0 && !words[0].isEmpty()) {
            int remaining = 4 - code.length();
            int available = Math.min(words[0].length() - 1, remaining);
            if (available > 0) {
                code.append(words[0].substring(1, 1 + available).toUpperCase());
            }
        }

        // Pad with X if still less than 4
        while (code.length() < 4) {
            code.append("X");
        }

        return code.substring(0, 4);
    }

    @Override
    public KanbanCardModel findCardById(UUID cardId, UUID userId) {
        log.info("Finding card with id: {} for user: {}", cardId, userId);

        // Get all boards for the user
        List<KanbanBoardModel> boards = kanbanBoardRepository.findByUserId(userId);

        // Search through all boards and lists to find the card
        for (KanbanBoardModel board : boards) {
            if (board.getLists() != null) {
                for (KanbanListModel list : board.getLists()) {
                    if (list.getCards() != null) {
                        for (KanbanCardModel card : list.getCards()) {
                            if (card.getId().equals(cardId)) {
                                log.info("Found card with id: {}", cardId);
                                return card;
                            }
                        }
                    }
                }
            }
        }

        log.error("Card not found with id: {}", cardId);
        throw new NoSuchElementException("Card not found with id: " + cardId);
    }

    @Override
    public CardDetailResponse findCardDetailById(UUID cardId, UUID userId) {
        log.info("Finding card detail with id: {} for user: {}", cardId, userId);

        List<KanbanBoardModel> boards = kanbanBoardRepository.findByUserId(userId);

        for (KanbanBoardModel board : boards) {
            if (board.getLists() != null) {
                for (KanbanListModel list : board.getLists()) {
                    if (list.getCards() != null) {
                        for (KanbanCardModel card : list.getCards()) {
                            if (card.getId().equals(cardId)) {
                                log.info("Found card with id: {} in board: {} list: {}", cardId, board.getId(), list.getListId());
                                return new CardDetailResponse(card, board.getId(), UUID.fromString(list.getListId()));
                            }
                        }
                    }
                }
            }
        }

        log.error("Card not found with id: {}", cardId);
        throw new NoSuchElementException("Card not found with id: " + cardId);
    }

    @Override
    public CardDetailResponse findCardDetailByCode(String cardCode, UUID userId) {
        log.info("Finding card detail by code: {} for user: {}", cardCode, userId);

        List<KanbanBoardModel> boards = kanbanBoardRepository.findByUserId(userId);

        for (KanbanBoardModel board : boards) {
            if (board.getLists() != null) {
                for (KanbanListModel list : board.getLists()) {
                    if (list.getCards() != null) {
                        for (KanbanCardModel card : list.getCards()) {
                            if (cardCode.equalsIgnoreCase(card.getCardCode())) {
                                log.info("Found card by code: {} in board: {} list: {}", cardCode, board.getId(), list.getListId());
                                return new CardDetailResponse(card, board.getId(), UUID.fromString(list.getListId()));
                            }
                        }
                    }
                }
            }
        }

        log.error("Card not found with code: {}", cardCode);
        throw new NoSuchElementException("Card not found with code: " + cardCode);
    }

    @Override
    public List<KanbanCardModel> searchCards(
            UUID boardId,
            UUID userId,
            UUID assigneeUserId,
            String priority,
            LocalDate dueDateFrom,
            LocalDate dueDateTo,
            Boolean completed,
            String search,
            UUID labelId) {

        log.info("Searching cards in board {} for user {} with filters: assignee={}, priority={}, dueDateFrom={}, dueDateTo={}, completed={}, search={}, labelId={}",
                boardId, userId, assigneeUserId, priority, dueDateFrom, dueDateTo, completed, search, labelId);

        // Get the board
        KanbanBoardModel board = kanbanBoardRepository.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        List<KanbanCardModel> allCards = new ArrayList<>();

        // Collect all cards from all lists
        if (board.getLists() != null) {
            for (KanbanListModel list : board.getLists()) {
                if (list.getCards() != null) {
                    allCards.addAll(list.getCards());
                }
            }
        }

        // Apply filters
        return allCards.stream()
                .filter(card -> {
                    // Filter by assignee
                    if (assigneeUserId != null) {
                        if (card.getAssignee() == null || !assigneeUserId.equals(card.getAssignee().getUserId())) {
                            return false;
                        }
                    }

                    // Filter by priority
                    if (priority != null && !priority.isBlank()) {
                        if (card.getPriority() == null || !priority.equalsIgnoreCase(card.getPriority().getTitle())) {
                            return false;
                        }
                    }

                    // Filter by due date from
                    if (dueDateFrom != null) {
                        if (card.getDueDate() == null || card.getDueDate().isBefore(dueDateFrom)) {
                            return false;
                        }
                    }

                    // Filter by due date to
                    if (dueDateTo != null) {
                        if (card.getDueDate() == null || card.getDueDate().isAfter(dueDateTo)) {
                            return false;
                        }
                    }

                    // Filter by completed status
                    if (completed != null) {
                        if (card.getCompleted() == null || !completed.equals(card.getCompleted())) {
                            return false;
                        }
                    }

                    // Filter by search text (title or description)
                    if (search != null && !search.isBlank()) {
                        String searchLower = search.toLowerCase();
                        boolean titleMatch = card.getTitle() != null && card.getTitle().toLowerCase().contains(searchLower);
                        boolean descMatch = card.getDescription() != null && card.getDescription().toLowerCase().contains(searchLower);
                        if (!titleMatch && !descMatch) {
                            return false;
                        }
                    }

                    // Filter by label
                    if (labelId != null) {
                        if (card.getLabels() == null || card.getLabels().stream().noneMatch(label -> labelId.equals(label.getId()))) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
    }

    // ==================== Comment Operations ====================

    @Override
    public KanbanCommentModel addComment(UUID cardId, UUID userId, String text) {
        log.info("Adding comment to card: {} by user: {}", cardId, userId);

        // Find the board that contains this card
        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        board = b;
                        card = c;
                        break;
                    }
                }
                if (card != null) break;
            }
            if (card != null) break;
        }

        if (card == null) {
            throw new NoSuchElementException("Card not found");
        }

        // Create new comment
        KanbanComment comment = KanbanComment.builder()
                .id(UUID.randomUUID())
                .text(text)
                .userId(userId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        card.getComments().add(comment);

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordCommentCreated();

        log.info("Comment added successfully to card: {}", cardId);
        return KanbanCommentModel.builder()
                .id(comment.getId())
                .text(comment.getText())
                .userId(comment.getUserId())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Override
    public KanbanCommentModel updateComment(UUID cardId, UUID commentId, UUID userId, String text) {
        log.info("Updating comment: {} on card: {} by user: {}", commentId, cardId, userId);

        // Find the board that contains this card
        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;
        KanbanComment comment = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        card = c;
                        // Find the comment
                        for (KanbanComment com : c.getComments()) {
                            if (com.getId().equals(commentId)) {
                                comment = com;
                                board = b;
                                break;
                            }
                        }
                    }
                    if (comment != null) break;
                }
                if (comment != null) break;
            }
            if (comment != null) break;
        }

        if (comment == null) {
            throw new NoSuchElementException("Comment not found");
        }

        // Update comment
        comment.setText(text);
        comment.setUpdatedAt(Instant.now());

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordCommentUpdated();

        log.info("Comment updated successfully: {}", commentId);
        return KanbanCommentModel.builder()
                .id(comment.getId())
                .text(comment.getText())
                .userId(comment.getUserId())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Override
    public void deleteComment(UUID cardId, UUID commentId, UUID userId) {
        log.info("Deleting comment: {} from card: {} by user: {}", commentId, cardId, userId);

        // Find the board that contains this card
        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        card = c;
                        board = b;
                        break;
                    }
                }
                if (card != null) break;
            }
            if (card != null) break;
        }

        if (card == null) {
            throw new NoSuchElementException("Card not found");
        }

        // Remove comment
        boolean removed = card.getComments().removeIf(c -> c.getId().equals(commentId));
        if (!removed) {
            throw new NoSuchElementException("Comment not found");
        }

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordCommentDeleted();

        log.info("Comment deleted successfully: {}", commentId);
    }

    // ==================== SubTask Operations ====================

    @Override
    public KanbanSubTaskModel addSubTask(UUID cardId, UUID userId, KanbanSubTaskModel subTaskModel) {
        log.info("Adding subtask to card: {} by user: {}", cardId, userId);

        // Find the board that contains this card
        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        board = b;
                        card = c;
                        break;
                    }
                }
                if (card != null) break;
            }
            if (card != null) break;
        }

        if (card == null) {
            throw new NoSuchElementException("Card not found");
        }

        // Create new subtask
        int position = card.getSubTasks().size();
        UUID assigneeUserId = subTaskModel.getAssignee() != null ? subTaskModel.getAssignee().getUserId() : null;

        KanbanSubTask subTask = KanbanSubTask.builder()
                .id(UUID.randomUUID())
                .text(subTaskModel.getText())
                .completed(subTaskModel.getCompleted() != null ? subTaskModel.getCompleted() : false)
                .position(position)
                .assigneeUserId(assigneeUserId)
                .dueDate(subTaskModel.getDueDate())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        card.getSubTasks().add(subTask);

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordSubtaskCreated();

        log.info("SubTask added successfully to card: {}", cardId);

        // Build assignee model if assigneeUserId exists, preserving name and image from input
        KanbanAssigneeModel assignee = null;
        if (subTask.getAssigneeUserId() != null) {
            KanbanAssigneeModel.KanbanAssigneeModelBuilder assigneeBuilder = KanbanAssigneeModel.builder()
                    .userId(subTask.getAssigneeUserId());
            // Preserve name and image from input model if provided
            if (subTaskModel.getAssignee() != null) {
                assigneeBuilder.name(subTaskModel.getAssignee().getName());
                assigneeBuilder.image(subTaskModel.getAssignee().getImage());
            }
            assignee = assigneeBuilder.build();
        }

        return KanbanSubTaskModel.builder()
                .id(subTask.getId())
                .text(subTask.getText())
                .completed(subTask.getCompleted())
                .position(subTask.getPosition())
                .assignee(assignee)
                .dueDate(subTask.getDueDate())
                .build();
    }

    @Override
    public KanbanSubTaskModel updateSubTask(UUID cardId, UUID subtaskId, UUID userId, KanbanSubTaskModel subTaskModel) {
        log.info("Updating subtask: {} on card: {} by user: {}", subtaskId, cardId, userId);

        // Find the board that contains this card
        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;
        KanbanSubTask subTask = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        card = c;
                        // Find the subtask
                        for (KanbanSubTask st : c.getSubTasks()) {
                            if (st.getId().equals(subtaskId)) {
                                subTask = st;
                                board = b;
                                break;
                            }
                        }
                    }
                    if (subTask != null) break;
                }
                if (subTask != null) break;
            }
            if (subTask != null) break;
        }

        if (subTask == null) {
            throw new NoSuchElementException("SubTask not found");
        }

        // Update subtask
        if (subTaskModel.getText() != null) {
            subTask.setText(subTaskModel.getText());
        }
        if (subTaskModel.getCompleted() != null) {
            subTask.setCompleted(subTaskModel.getCompleted());
        }
        if (subTaskModel.getAssignee() != null && subTaskModel.getAssignee().getUserId() != null) {
            subTask.setAssigneeUserId(subTaskModel.getAssignee().getUserId());
        }
        if (subTaskModel.getDueDate() != null) {
            subTask.setDueDate(subTaskModel.getDueDate());
        }
        subTask.setUpdatedAt(Instant.now());

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordSubtaskUpdated();

        log.info("SubTask updated successfully: {}", subtaskId);

        // Build assignee model if assigneeUserId exists
        KanbanAssigneeModel assignee = null;
        if (subTask.getAssigneeUserId() != null) {
            assignee = KanbanAssigneeModel.builder()
                    .userId(subTask.getAssigneeUserId())
                    .build();
        }

        return KanbanSubTaskModel.builder()
                .id(subTask.getId())
                .text(subTask.getText())
                .completed(subTask.getCompleted())
                .position(subTask.getPosition())
                .assignee(assignee)
                .dueDate(subTask.getDueDate())
                .build();
    }

    @Override
    public void deleteSubTask(UUID cardId, UUID subtaskId, UUID userId) {
        log.info("Deleting subtask: {} from card: {} by user: {}", subtaskId, cardId, userId);

        // Find the board that contains this card
        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        card = c;
                        board = b;
                        break;
                    }
                }
                if (card != null) break;
            }
            if (card != null) break;
        }

        if (card == null) {
            throw new NoSuchElementException("Card not found");
        }

        // Remove subtask
        boolean removed = card.getSubTasks().removeIf(st -> st.getId().equals(subtaskId));
        if (!removed) {
            throw new NoSuchElementException("SubTask not found");
        }

        // Reindex positions
        int pos = 0;
        for (KanbanSubTask st : card.getSubTasks()) {
            st.setPosition(pos++);
        }

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordSubtaskDeleted();

        log.info("SubTask deleted successfully: {}", subtaskId);
    }

    // ==================== Acceptance Criteria Operations ====================

    @Override
    public KanbanAcceptanceCriteriaModel addAcceptanceCriteria(UUID cardId, UUID userId, KanbanAcceptanceCriteriaModel model) {
        log.info("Adding acceptance criteria to card: {} by user: {}", cardId, userId);

        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        board = b;
                        card = c;
                        break;
                    }
                }
                if (card != null) break;
            }
            if (card != null) break;
        }

        if (card == null) {
            throw new NoSuchElementException("Card not found");
        }

        int position = card.getAcceptanceCriteria().size();

        KanbanAcceptanceCriteria criteria = KanbanAcceptanceCriteria.builder()
                .id(UUID.randomUUID())
                .text(model.getText())
                .completed(model.getCompleted() != null ? model.getCompleted() : false)
                .position(position)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        card.getAcceptanceCriteria().add(criteria);

        jpaRepositoryKanbanBoard.save(board);

        log.info("Acceptance criteria added successfully to card: {}", cardId);

        return KanbanAcceptanceCriteriaModel.builder()
                .id(criteria.getId())
                .text(criteria.getText())
                .completed(criteria.getCompleted())
                .position(criteria.getPosition())
                .build();
    }

    @Override
    public KanbanAcceptanceCriteriaModel updateAcceptanceCriteria(UUID cardId, UUID criteriaId, UUID userId, KanbanAcceptanceCriteriaModel model) {
        log.info("Updating acceptance criteria: {} on card: {} by user: {}", criteriaId, cardId, userId);

        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;
        KanbanAcceptanceCriteria criteria = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        card = c;
                        for (KanbanAcceptanceCriteria ac : c.getAcceptanceCriteria()) {
                            if (ac.getId().equals(criteriaId)) {
                                criteria = ac;
                                board = b;
                                break;
                            }
                        }
                    }
                    if (criteria != null) break;
                }
                if (criteria != null) break;
            }
            if (criteria != null) break;
        }

        if (criteria == null) {
            throw new NoSuchElementException("Acceptance criteria not found");
        }

        if (model.getText() != null) {
            criteria.setText(model.getText());
        }
        if (model.getCompleted() != null) {
            criteria.setCompleted(model.getCompleted());
        }
        criteria.setUpdatedAt(Instant.now());

        jpaRepositoryKanbanBoard.save(board);

        log.info("Acceptance criteria updated successfully: {}", criteriaId);

        return KanbanAcceptanceCriteriaModel.builder()
                .id(criteria.getId())
                .text(criteria.getText())
                .completed(criteria.getCompleted())
                .position(criteria.getPosition())
                .build();
    }

    @Override
    public void deleteAcceptanceCriteria(UUID cardId, UUID criteriaId, UUID userId) {
        log.info("Deleting acceptance criteria: {} from card: {} by user: {}", criteriaId, cardId, userId);

        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        card = c;
                        board = b;
                        break;
                    }
                }
                if (card != null) break;
            }
            if (card != null) break;
        }

        if (card == null) {
            throw new NoSuchElementException("Card not found");
        }

        boolean removed = card.getAcceptanceCriteria().removeIf(ac -> ac.getId().equals(criteriaId));
        if (!removed) {
            throw new NoSuchElementException("Acceptance criteria not found");
        }

        int pos = 0;
        for (KanbanAcceptanceCriteria ac : card.getAcceptanceCriteria()) {
            ac.setPosition(pos++);
        }

        jpaRepositoryKanbanBoard.save(board);

        log.info("Acceptance criteria deleted successfully: {}", criteriaId);
    }

    // ==================== Label Operations ====================

    @Override
    public KanbanLabelModel createLabel(UUID boardId, UUID userId, String name, String color) {
        log.info("Creating label for board: {} by user: {}", boardId, userId);

        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        // Create new label
        KanbanLabel label = KanbanLabel.builder()
                .id(UUID.randomUUID())
                .name(name)
                .color(color)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        board.getLabels().add(label);

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordLabelCreated();

        log.info("Label created successfully: {}", label.getId());
        return KanbanLabelModel.builder()
                .id(label.getId())
                .name(label.getName())
                .color(label.getColor())
                .createdAt(label.getCreatedAt())
                .updatedAt(label.getUpdatedAt())
                .build();
    }

    @Override
    public KanbanLabelModel updateLabel(UUID boardId, UUID labelId, UUID userId, String name, String color) {
        log.info("Updating label: {} on board: {} by user: {}", labelId, boardId, userId);

        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        // Find the label
        KanbanLabel label = board.getLabels().stream()
                .filter(l -> l.getId().equals(labelId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Label not found"));

        // Update label
        if (name != null) {
            label.setName(name);
        }
        if (color != null) {
            label.setColor(color);
        }
        label.setUpdatedAt(Instant.now());

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordLabelUpdated();

        log.info("Label updated successfully: {}", labelId);
        return KanbanLabelModel.builder()
                .id(label.getId())
                .name(label.getName())
                .color(label.getColor())
                .createdAt(label.getCreatedAt())
                .updatedAt(label.getUpdatedAt())
                .build();
    }

    @Override
    public void deleteLabel(UUID boardId, UUID labelId, UUID userId) {
        log.info("Deleting label: {} from board: {} by user: {}", labelId, boardId, userId);

        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        // Remove label
        boolean removed = board.getLabels().removeIf(l -> l.getId().equals(labelId));
        if (!removed) {
            throw new NoSuchElementException("Label not found");
        }

        // Remove label from all cards
        for (KanbanList list : board.getLists()) {
            for (KanbanCard card : list.getCards()) {
                card.getLabels().removeIf(cl -> cl.getLabelId().equals(labelId));
            }
        }

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordLabelDeleted();

        log.info("Label deleted successfully: {}", labelId);
    }

    @Override
    public List<KanbanLabelModel> getBoardLabels(UUID boardId, UUID userId) {
        log.info("Getting labels for board: {}", boardId);

        KanbanBoard board = jpaRepositoryKanbanBoard.findByIdAndUserId(boardId, userId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        return board.getLabels().stream()
                .map(label -> KanbanLabelModel.builder()
                        .id(label.getId())
                        .name(label.getName())
                        .color(label.getColor())
                        .createdAt(label.getCreatedAt())
                        .updatedAt(label.getUpdatedAt())
                        .build())
                .toList();
    }

    // ==================== Card-Label Assignment Operations ====================

    @Override
    public void addLabelToCard(UUID cardId, UUID labelId, UUID userId) {
        log.info("Adding label: {} to card: {} by user: {}", labelId, cardId, userId);

        // Find the board that contains this card
        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        board = b;
                        card = c;
                        break;
                    }
                }
                if (card != null) break;
            }
            if (card != null) break;
        }

        if (card == null) {
            throw new NoSuchElementException("Card not found");
        }

        // Verify label exists in board
        boolean labelExists = board.getLabels().stream()
                .anyMatch(l -> l.getId().equals(labelId));
        if (!labelExists) {
            throw new NoSuchElementException("Label not found in board");
        }

        // Check if label is already assigned
        boolean alreadyAssigned = card.getLabels().stream()
                .anyMatch(cl -> cl.getLabelId().equals(labelId));
        if (alreadyAssigned) {
            throw new IllegalStateException("Label already assigned to card");
        }

        // Create card-label association
        KanbanCardLabel cardLabel = KanbanCardLabel.builder()
                .id(UUID.randomUUID())
                .labelId(labelId)
                .build();

        card.getLabels().add(cardLabel);

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordLabelAssigned();

        log.info("Label added to card successfully");
    }

    @Override
    public void removeLabelFromCard(UUID cardId, UUID labelId, UUID userId) {
        log.info("Removing label: {} from card: {} by user: {}", labelId, cardId, userId);

        // Find the board that contains this card
        List<KanbanBoard> boards = (List<KanbanBoard>) jpaRepositoryKanbanBoard.findAll();
        KanbanBoard board = null;
        KanbanCard card = null;

        for (KanbanBoard b : boards) {
            if (!b.getUserId().equals(userId)) continue;
            for (KanbanList list : b.getLists()) {
                for (KanbanCard c : list.getCards()) {
                    if (c.getId().equals(cardId)) {
                        board = b;
                        card = c;
                        break;
                    }
                }
                if (card != null) break;
            }
            if (card != null) break;
        }

        if (card == null) {
            throw new NoSuchElementException("Card not found");
        }

        // Remove label from card
        boolean removed = card.getLabels().removeIf(cl -> cl.getLabelId().equals(labelId));
        if (!removed) {
            throw new NoSuchElementException("Label not assigned to card");
        }

        // Save board (aggregate root)
        jpaRepositoryKanbanBoard.save(board);
        kanbanMetrics.recordLabelUnassigned();

        log.info("Label removed from card successfully");
    }
}
