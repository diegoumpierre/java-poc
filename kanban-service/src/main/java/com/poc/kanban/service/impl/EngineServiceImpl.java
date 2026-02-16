package com.poc.kanban.service.impl;

import com.poc.kanban.domain.*;
import com.poc.kanban.metrics.KanbanMetrics;
import com.poc.kanban.model.*;
import com.poc.kanban.repository.jpa.*;
import com.poc.kanban.service.CardHistoryService;
import com.poc.kanban.service.EngineService;
import com.poc.kanban.service.KanbanAttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EngineServiceImpl implements EngineService {

    private final JpaRepositoryKanbanBoard boardRepo;
    private final JpaRepositoryBoardType boardTypeRepo;
    private final JpaRepositoryBoardTypeFeature featureRepo;
    private final JpaRepositoryWorkflowStep stepRepo;
    private final JpaRepositoryWorkflowTransition transitionRepo;
    private final JpaRepositoryApprovalRule approvalRuleRepo;
    private final JpaRepositoryApproval approvalRepo;
    private final KanbanAttachmentService attachmentService;
    private final CardHistoryService historyService;
    private final KanbanMetrics kanbanMetrics;

    // ==================== Board Types ====================

    @Override
    @Transactional(readOnly = true)
    public List<BoardTypeModel> getBoardTypes() {
        List<BoardTypeModel> result = new ArrayList<>();
        boardTypeRepo.findAll().forEach(bt -> {
            List<String> features = featureRepo.findByBoardTypeCode(bt.getCode()).stream()
                    .filter(f -> Boolean.TRUE.equals(f.getEnabled()))
                    .map(BoardTypeFeature::getFeatureCode)
                    .toList();
            result.add(toBoardTypeModel(bt, features));
        });
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BoardTypeModel getBoardType(String code) {
        BoardType bt = boardTypeRepo.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Board type not found: " + code));
        List<String> features = featureRepo.findByBoardTypeCode(code).stream()
                .filter(f -> Boolean.TRUE.equals(f.getEnabled()))
                .map(BoardTypeFeature::getFeatureCode)
                .toList();
        return toBoardTypeModel(bt, features);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowModel getWorkflow(String boardTypeCode) {
        boardTypeRepo.findById(boardTypeCode)
                .orElseThrow(() -> new NoSuchElementException("Board type not found: " + boardTypeCode));

        List<WorkflowStep> steps = stepRepo.findByBoardTypeCodeOrderByPositionAsc(boardTypeCode);
        Map<UUID, WorkflowStep> stepMap = new HashMap<>();
        steps.forEach(s -> stepMap.put(s.getId(), s));

        List<WorkflowStepModel> stepModels = steps.stream()
                .map(s -> new WorkflowStepModel(s.getId(), s.getStepCode(), s.getStepName(),
                        s.getPosition(), s.getColor(), s.getIsInitial(), s.getIsFinal()))
                .toList();

        List<WorkflowTransitionModel> transitionModels = transitionRepo.findByBoardTypeCode(boardTypeCode).stream()
                .map(t -> {
                    WorkflowStep from = stepMap.get(t.getFromStepId());
                    WorkflowStep to = stepMap.get(t.getToStepId());
                    return new WorkflowTransitionModel(t.getId(),
                            from != null ? from.getStepCode() : null,
                            to != null ? to.getStepCode() : null,
                            t.getRequiresComment(), t.getRequiresApproval());
                })
                .toList();

        return new WorkflowModel(boardTypeCode, stepModels, transitionModels);
    }

    // ==================== Boards ====================

    @Override
    @Transactional(readOnly = true)
    public List<EngineBoardModel> getBoards(UUID tenantId, String type) {
        List<KanbanBoard> boards;
        if (type != null && !type.isBlank()) {
            boards = boardRepo.findByBoardTypeCodeAndTenantId(type, tenantId);
        } else {
            boards = boardRepo.findByTenantId(tenantId);
        }
        return boards.stream().map(this::toEngineBoardModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EngineBoardModel> getBoardsPaged(UUID tenantId, int page, int size) {
        long total = boardRepo.countByTenantId(tenantId);
        if (total == 0) return PageResponse.of(List.of(), page, size, 0);

        List<EngineBoardModel> content = boardRepo.findByTenantIdPaged(tenantId, size, page * size).stream()
                .map(this::toEngineBoardModel)
                .toList();
        return PageResponse.of(content, page, size, total);
    }

    @Override
    @Transactional(readOnly = true)
    public EngineBoardModel findBoard(String type, UUID tenantId, UUID userId) {
        BoardType bt = boardTypeRepo.findById(type)
                .orElseThrow(() -> new NoSuchElementException("Board type not found: " + type));

        KanbanBoard board;
        if ("USER".equals(bt.getScope()) && userId != null) {
            board = boardRepo.findByBoardTypeCodeAndTenantIdAndUserId(type, tenantId, userId)
                    .orElseThrow(() -> new NoSuchElementException("Board not found for type " + type));
        } else {
            List<KanbanBoard> boards = boardRepo.findByBoardTypeCodeAndTenantId(type, tenantId);
            if (boards.isEmpty()) {
                throw new NoSuchElementException("Board not found for type " + type);
            }
            board = boards.get(0);
        }
        return toEngineBoardModel(board);
    }

    @Override
    public EngineBoardModel createBoard(CreateEngineBoardRequest request, UUID tenantId, UUID userId) {
        BoardType bt = boardTypeRepo.findById(request.boardTypeCode())
                .orElseThrow(() -> new NoSuchElementException("Board type not found: " + request.boardTypeCode()));

        if (Boolean.TRUE.equals(bt.getSingleton())) {
            List<KanbanBoard> existing = boardRepo.findByBoardTypeCodeAndTenantId(request.boardTypeCode(), tenantId);
            if (!existing.isEmpty()) {
                return toEngineBoardModel(existing.get(0));
            }
        }

        String title = request.title() != null ? request.title() : bt.getName();
        String prefix = request.prefix() != null ? request.prefix() : bt.getNumberPrefix();

        KanbanBoard board = KanbanBoard.builder()
                .id(UUID.randomUUID())
                .title(title)
                .boardCode(prefix != null ? prefix : generateBoardCode(title))
                .userId(userId)
                .tenantId(tenantId)
                .boardTypeCode(request.boardTypeCode())
                .isActive(true)
                .lists(new ArrayList<>())
                .labels(new ArrayList<>())
                .isNew(true)
                .build();

        List<WorkflowStep> steps = stepRepo.findByBoardTypeCodeOrderByPositionAsc(request.boardTypeCode());
        for (WorkflowStep step : steps) {
            KanbanList list = KanbanList.builder()
                    .id(UUID.randomUUID())
                    .title(step.getStepName())
                    .position(step.getPosition())
                    .workflowStepId(step.getId())
                    .color(step.getColor())
                    .cards(new ArrayList<>())
                    .build();
            board.getLists().add(list);
        }

        KanbanBoard saved = boardRepo.save(board);
        kanbanMetrics.recordBoardCreated();
        kanbanMetrics.incrementActiveBoards();
        log.info("Created engine board '{}' of type '{}' for tenant {}", title, request.boardTypeCode(), tenantId);

        return toEngineBoardModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EngineBoardModel getBoard(UUID boardId, UUID tenantId) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        return toEngineBoardModel(board);
    }

    @Override
    public EngineBoardModel updateBoard(UUID boardId, EngineBoardModel boardModel, UUID tenantId, UUID userId) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        if (boardModel.title() != null) board.setTitle(boardModel.title());
        boardRepo.save(board);
        kanbanMetrics.recordBoardUpdated();
        return toEngineBoardModel(board);
    }

    @Override
    public void deleteBoard(UUID boardId, UUID tenantId, UUID userId) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        boardRepo.delete(board);
        kanbanMetrics.recordBoardDeleted();
        kanbanMetrics.decrementActiveBoards();
    }

    @Override
    public EngineBoardModel saveBoardState(UUID boardId, KanbanBoardModel boardModel, UUID tenantId, UUID userId) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        if (boardModel.getLists() != null) {
            for (KanbanListModel listModel : boardModel.getLists()) {
                UUID listId = listModel.getListId() != null ? UUID.fromString(listModel.getListId()) : null;
                if (listId == null) continue;
                board.getLists().stream()
                        .filter(l -> l.getId().equals(listId))
                        .findFirst()
                        .ifPresent(list -> {
                            list.setPosition(listModel.getPosition());
                            if (listModel.getCards() != null) {
                                int cardPos = 0;
                                for (KanbanCardModel cm : listModel.getCards()) {
                                    int pos = cardPos++;
                                    list.getCards().stream()
                                            .filter(c -> c.getId().equals(cm.getId()))
                                            .findFirst()
                                            .ifPresent(c -> c.setPosition(pos));
                                }
                            }
                        });
            }
        }

        boardRepo.save(board);
        return toEngineBoardModel(board);
    }

    // ==================== Cards ====================

    @Override
    public EngineCardModel createCard(UUID boardId, CreateEngineCardRequest request, UUID tenantId, UUID userId) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        KanbanList targetList = findTargetList(board, request.targetStepCode(), request.targetListId());

        int maxCardNumber = board.getLists().stream()
                .flatMap(l -> l.getCards().stream())
                .map(KanbanCard::getCardNumber)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);

        int maxPosition = targetList.getCards().stream()
                .mapToInt(c -> c.getPosition() != null ? c.getPosition() : 0)
                .max().orElse(-1);

        KanbanCard card = KanbanCard.builder()
                .id(UUID.randomUUID())
                .title(request.title())
                .description(request.description())
                .cardNumber(maxCardNumber + 1)
                .position(maxPosition + 1)
                .priorityColor(request.priorityColor())
                .priorityTitle(request.priorityTitle())
                .startDate(request.startDate())
                .dueDate(request.dueDate())
                .sourceService(request.sourceService())
                .completed(false)
                .progress(0)
                .attachments(0)
                .pendingApproval(false)
                .subTasks(new ArrayList<>())
                .comments(new ArrayList<>())
                .labels(new ArrayList<>())
                .build();

        targetList.getCards().add(card);
        KanbanBoard saved = boardRepo.save(board);
        kanbanMetrics.recordCardCreated();

        KanbanList savedList = saved.getLists().stream()
                .filter(l -> l.getId().equals(targetList.getId()))
                .findFirst().orElseThrow();
        KanbanCard savedCard = savedList.getCards().stream()
                .filter(c -> c.getCardNumber() != null && c.getCardNumber() == maxCardNumber + 1)
                .findFirst()
                .orElse(savedList.getCards().get(savedList.getCards().size() - 1));

        historyService.trackCardCreated(savedCard.getId(), boardId, userId, null, null);
        return toEngineCardModel(savedCard, savedList, saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EngineCardModel getCard(UUID cardId, UUID tenantId) {
        return findCardInBoards(cardId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public EngineCardModel getCardByCode(String code, UUID tenantId) {
        int dashIdx = code.lastIndexOf('-');
        if (dashIdx < 0) {
            throw new IllegalArgumentException("Invalid card code format: " + code);
        }
        String prefix = code.substring(0, dashIdx);
        int number;
        try {
            number = Integer.parseInt(code.substring(dashIdx + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid card code format: " + code);
        }

        Optional<KanbanBoard> boardOpt = boardRepo.findByBoardCodeAndTenantId(prefix, tenantId);
        if (boardOpt.isEmpty()) {
            throw new NoSuchElementException("Card not found with code: " + code);
        }

        KanbanBoard board = boardOpt.get();
        for (KanbanList list : board.getLists()) {
            for (KanbanCard card : list.getCards()) {
                if (card.getCardNumber() != null && card.getCardNumber() == number) {
                    return toEngineCardModel(card, list, board);
                }
            }
        }
        throw new NoSuchElementException("Card not found with code: " + code);
    }

    @Override
    public EngineCardModel updateCard(UUID cardId, EngineCardModel cardModel, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);

        if (cardModel.title() != null) card.setTitle(cardModel.title());
        if (cardModel.description() != null) card.setDescription(cardModel.description());
        if (cardModel.startDate() != null) card.setStartDate(cardModel.startDate());
        if (cardModel.dueDate() != null) card.setDueDate(cardModel.dueDate());
        if (cardModel.completed() != null) card.setCompleted(cardModel.completed());
        if (cardModel.progress() != null) card.setProgress(cardModel.progress());
        if (cardModel.priority() != null) {
            card.setPriorityColor(cardModel.priority().getColor());
            card.setPriorityTitle(cardModel.priority().getTitle());
        }

        boardRepo.save(board);
        kanbanMetrics.recordCardUpdated();

        KanbanList list = findListContainingCard(board, cardId);
        return toEngineCardModel(card, list, board);
    }

    @Override
    public void deleteCard(UUID cardId, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        for (KanbanList list : board.getLists()) {
            list.getCards().removeIf(c -> c.getId().equals(cardId));
        }
        boardRepo.save(board);
        kanbanMetrics.recordCardDeleted();
        historyService.trackCardDeleted(cardId, board.getId(), userId, null);
    }

    @Override
    public EngineCardModel moveCard(UUID cardId, MoveCardRequest request, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanList sourceList = findListContainingCard(board, cardId);
        KanbanCard card = findCardInBoard(board, cardId);

        KanbanList targetList = findTargetList(board, request.targetStepCode(), request.targetListId());

        if (sourceList.getId().equals(targetList.getId())) {
            if (request.position() != null) card.setPosition(request.position());
            boardRepo.save(board);
            return toEngineCardModel(card, targetList, board);
        }

        // Workflow validation
        if (sourceList.getWorkflowStepId() != null && targetList.getWorkflowStepId() != null) {
            String boardTypeCode = board.getBoardTypeCode();
            Optional<WorkflowTransition> transition = transitionRepo.findTransition(
                    boardTypeCode, sourceList.getWorkflowStepId(), targetList.getWorkflowStepId());

            if (transition.isEmpty()) {
                throw new IllegalStateException(
                        "Transition from '" + sourceList.getTitle() + "' to '" + targetList.getTitle() + "' is not allowed");
            }

            WorkflowTransition wt = transition.get();

            if (Boolean.TRUE.equals(wt.getRequiresComment())
                    && (request.comment() == null || request.comment().isBlank())) {
                throw new IllegalArgumentException("Comment is required for this transition");
            }

            if (Boolean.TRUE.equals(wt.getRequiresApproval())) {
                Approval approval = createApprovalRequest(card, board, sourceList, targetList, wt, tenantId, userId);
                card.setPendingApproval(true);
                card.setPendingApprovalId(approval.getId());
                card.setPendingTargetList(targetList.getId());
                boardRepo.save(board);
                log.info("Approval required for card {} moving to '{}'. Approval ID: {}",
                        cardId, targetList.getTitle(), approval.getId());
                return toEngineCardModel(card, sourceList, board);
            }
        }

        doMoveCard(sourceList, targetList, card, request.position());

        if (request.comment() != null && !request.comment().isBlank()) {
            KanbanComment comment = KanbanComment.builder()
                    .id(UUID.randomUUID())
                    .text(request.comment())
                    .userId(userId)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            card.getComments().add(comment);
        }

        boardRepo.save(board);
        kanbanMetrics.recordCardMoved();

        historyService.trackCardUpdated(cardId, board.getId(), userId, null,
                List.of(new CardHistoryModel.FieldChange("list", "List", sourceList.getTitle(), targetList.getTitle())), null);

        return toEngineCardModel(card, targetList, board);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EngineCardModel> searchCards(UUID boardId, UUID tenantId, UUID assigneeUserId,
                                              String priority, LocalDate dueDateFrom, LocalDate dueDateTo,
                                              Boolean completed, String search, UUID labelId, String sourceService) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));

        List<EngineCardModel> allCards = new ArrayList<>();
        for (KanbanList list : board.getLists()) {
            for (KanbanCard card : list.getCards()) {
                allCards.add(toEngineCardModel(card, list, board));
            }
        }

        return allCards.stream()
                .filter(card -> {
                    if (assigneeUserId != null && (card.assignees() == null || card.assignees().stream()
                            .noneMatch(a -> assigneeUserId.equals(a.getUserId())))) return false;
                    if (priority != null && !priority.isBlank() && (card.priority() == null
                            || !priority.equalsIgnoreCase(card.priority().getTitle()))) return false;
                    if (dueDateFrom != null && (card.dueDate() == null || card.dueDate().isBefore(dueDateFrom))) return false;
                    if (dueDateTo != null && (card.dueDate() == null || card.dueDate().isAfter(dueDateTo))) return false;
                    if (completed != null && !completed.equals(card.completed())) return false;
                    if (search != null && !search.isBlank()) {
                        String s = search.toLowerCase();
                        boolean match = (card.title() != null && card.title().toLowerCase().contains(s))
                                || (card.description() != null && card.description().toLowerCase().contains(s));
                        if (!match) return false;
                    }
                    if (sourceService != null && !sourceService.equals(card.sourceService())) return false;
                    if (labelId != null && (card.labels() == null || card.labels().stream()
                            .noneMatch(l -> labelId.equals(l.getId())))) return false;
                    return true;
                })
                .toList();
    }

    // ==================== Comments ====================

    @Override
    public KanbanCommentModel addComment(UUID cardId, String text, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);

        KanbanComment comment = KanbanComment.builder()
                .id(UUID.randomUUID())
                .text(text)
                .userId(userId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        card.getComments().add(comment);
        boardRepo.save(board);
        kanbanMetrics.recordCommentCreated();

        return KanbanCommentModel.builder()
                .id(comment.getId()).text(comment.getText())
                .userId(comment.getUserId()).createdAt(comment.getCreatedAt())
                .build();
    }

    @Override
    public KanbanCommentModel updateComment(UUID cardId, UUID commentId, String text, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);

        KanbanComment comment = card.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        comment.setText(text);
        comment.setUpdatedAt(Instant.now());
        boardRepo.save(board);
        kanbanMetrics.recordCommentUpdated();

        return KanbanCommentModel.builder()
                .id(comment.getId()).text(comment.getText())
                .userId(comment.getUserId()).createdAt(comment.getCreatedAt())
                .build();
    }

    @Override
    public void deleteComment(UUID cardId, UUID commentId, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);
        if (!card.getComments().removeIf(c -> c.getId().equals(commentId))) {
            throw new NoSuchElementException("Comment not found");
        }
        boardRepo.save(board);
        kanbanMetrics.recordCommentDeleted();
    }

    // ==================== SubTasks ====================

    @Override
    public KanbanSubTaskModel addSubTask(UUID cardId, KanbanSubTaskModel subTaskModel, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);

        UUID assigneeUserId = subTaskModel.getAssignee() != null ? subTaskModel.getAssignee().getUserId() : null;

        KanbanSubTask subTask = KanbanSubTask.builder()
                .id(UUID.randomUUID())
                .text(subTaskModel.getText())
                .completed(subTaskModel.getCompleted() != null ? subTaskModel.getCompleted() : false)
                .position(card.getSubTasks().size())
                .assigneeUserId(assigneeUserId)
                .dueDate(subTaskModel.getDueDate())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        card.getSubTasks().add(subTask);
        boardRepo.save(board);
        kanbanMetrics.recordSubtaskCreated();

        return KanbanSubTaskModel.builder()
                .id(subTask.getId()).text(subTask.getText())
                .completed(subTask.getCompleted()).position(subTask.getPosition())
                .assignee(assigneeUserId != null ? KanbanAssigneeModel.builder().userId(assigneeUserId).build() : null)
                .dueDate(subTask.getDueDate())
                .build();
    }

    @Override
    public KanbanSubTaskModel updateSubTask(UUID cardId, UUID subtaskId, KanbanSubTaskModel subTaskModel,
                                             UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);

        KanbanSubTask subTask = card.getSubTasks().stream()
                .filter(st -> st.getId().equals(subtaskId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("SubTask not found"));

        if (subTaskModel.getText() != null) subTask.setText(subTaskModel.getText());
        if (subTaskModel.getCompleted() != null) subTask.setCompleted(subTaskModel.getCompleted());
        if (subTaskModel.getAssignee() != null && subTaskModel.getAssignee().getUserId() != null) {
            subTask.setAssigneeUserId(subTaskModel.getAssignee().getUserId());
        }
        if (subTaskModel.getDueDate() != null) subTask.setDueDate(subTaskModel.getDueDate());
        subTask.setUpdatedAt(Instant.now());

        boardRepo.save(board);
        kanbanMetrics.recordSubtaskUpdated();

        return KanbanSubTaskModel.builder()
                .id(subTask.getId()).text(subTask.getText())
                .completed(subTask.getCompleted()).position(subTask.getPosition())
                .assignee(subTask.getAssigneeUserId() != null ?
                        KanbanAssigneeModel.builder().userId(subTask.getAssigneeUserId()).build() : null)
                .dueDate(subTask.getDueDate())
                .build();
    }

    @Override
    public void deleteSubTask(UUID cardId, UUID subtaskId, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);
        if (!card.getSubTasks().removeIf(st -> st.getId().equals(subtaskId))) {
            throw new NoSuchElementException("SubTask not found");
        }
        int pos = 0;
        for (KanbanSubTask st : card.getSubTasks()) { st.setPosition(pos++); }
        boardRepo.save(board);
        kanbanMetrics.recordSubtaskDeleted();
    }

    // ==================== Labels ====================

    @Override
    public void addLabelToCard(UUID cardId, UUID labelId, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);
        if (board.getLabels().stream().noneMatch(l -> l.getId().equals(labelId)))
            throw new NoSuchElementException("Label not found in board");
        if (card.getLabels().stream().anyMatch(cl -> cl.getLabelId().equals(labelId)))
            throw new IllegalStateException("Label already assigned to card");
        card.getLabels().add(KanbanCardLabel.builder().id(UUID.randomUUID()).labelId(labelId).build());
        boardRepo.save(board);
        kanbanMetrics.recordLabelAssigned();
    }

    @Override
    public void removeLabelFromCard(UUID cardId, UUID labelId, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);
        if (!card.getLabels().removeIf(cl -> cl.getLabelId().equals(labelId)))
            throw new NoSuchElementException("Label not assigned to card");
        boardRepo.save(board);
        kanbanMetrics.recordLabelUnassigned();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KanbanLabelModel> getBoardLabels(UUID boardId, UUID tenantId) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        return board.getLabels().stream()
                .map(l -> KanbanLabelModel.builder()
                        .id(l.getId()).name(l.getName()).color(l.getColor())
                        .createdAt(l.getCreatedAt()).updatedAt(l.getUpdatedAt()).build())
                .toList();
    }

    @Override
    public KanbanLabelModel createLabel(UUID boardId, String name, String color, UUID tenantId, UUID userId) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        KanbanLabel label = KanbanLabel.builder()
                .id(UUID.randomUUID()).name(name).color(color)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();
        board.getLabels().add(label);
        boardRepo.save(board);
        kanbanMetrics.recordLabelCreated();
        return KanbanLabelModel.builder()
                .id(label.getId()).name(label.getName()).color(label.getColor())
                .createdAt(label.getCreatedAt()).updatedAt(label.getUpdatedAt()).build();
    }

    @Override
    public KanbanLabelModel updateLabel(UUID boardId, UUID labelId, String name, String color,
                                         UUID tenantId, UUID userId) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        KanbanLabel label = board.getLabels().stream()
                .filter(l -> l.getId().equals(labelId)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Label not found"));
        if (name != null) label.setName(name);
        if (color != null) label.setColor(color);
        label.setUpdatedAt(Instant.now());
        boardRepo.save(board);
        kanbanMetrics.recordLabelUpdated();
        return KanbanLabelModel.builder()
                .id(label.getId()).name(label.getName()).color(label.getColor())
                .createdAt(label.getCreatedAt()).updatedAt(label.getUpdatedAt()).build();
    }

    @Override
    public void deleteLabel(UUID boardId, UUID labelId, UUID tenantId, UUID userId) {
        KanbanBoard board = boardRepo.findByIdAndTenantId(boardId, tenantId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        if (!board.getLabels().removeIf(l -> l.getId().equals(labelId)))
            throw new NoSuchElementException("Label not found");
        for (KanbanList list : board.getLists()) {
            for (KanbanCard card : list.getCards()) {
                card.getLabels().removeIf(cl -> cl.getLabelId().equals(labelId));
            }
        }
        boardRepo.save(board);
        kanbanMetrics.recordLabelDeleted();
    }

    // ==================== Attachments ====================

    @Override
    @Transactional(readOnly = true)
    public List<KanbanAttachmentModel> getAttachments(UUID cardId, UUID tenantId) {
        return attachmentService.findByCardId(cardId);
    }

    @Override
    public KanbanAttachmentModel uploadAttachment(UUID cardId, MultipartFile file, UUID tenantId, UUID userId) {
        return attachmentService.upload(cardId, file);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadAttachment(UUID cardId, UUID attachmentId, UUID tenantId) {
        return attachmentService.download(cardId, attachmentId);
    }

    @Override
    public void deleteAttachment(UUID cardId, UUID attachmentId, UUID tenantId, UUID userId) {
        attachmentService.delete(cardId, attachmentId);
    }

    // ==================== Assignees ====================

    @Override
    public void addAssignee(UUID cardId, UUID assigneeUserId, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);
        card.setAssigneeUserId(assigneeUserId);
        boardRepo.save(board);
    }

    @Override
    public void removeAssignee(UUID cardId, UUID assigneeUserId, UUID tenantId, UUID userId) {
        KanbanBoard board = findBoardByCardId(cardId, tenantId);
        KanbanCard card = findCardInBoard(board, cardId);
        if (assigneeUserId.equals(card.getAssigneeUserId())) {
            card.setAssigneeUserId(null);
            boardRepo.save(board);
        }
    }

    // ==================== History ====================

    @Override
    @Transactional(readOnly = true)
    public List<CardHistoryModel> getCardHistory(UUID cardId, UUID tenantId) {
        return historyService.getCardHistory(cardId);
    }

    // ==================== Approvals ====================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalModel> getApprovals(UUID tenantId, String status, UUID boardId, UUID cardId) {
        List<Approval> approvals;
        if (cardId != null) {
            approvals = status != null ?
                    approvalRepo.findByCardIdAndStatus(cardId, status) :
                    approvalRepo.findByCardId(cardId);
        } else if (boardId != null) {
            approvals = approvalRepo.findByBoardId(boardId);
        } else if (status != null) {
            approvals = approvalRepo.findByTenantIdAndStatus(tenantId, status);
        } else {
            approvals = approvalRepo.findByTenantIdAndStatus(tenantId, "PENDING");
        }
        return approvals.stream().map(this::toApprovalModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CountModel countApprovals(UUID tenantId, String status) {
        return new CountModel(approvalRepo.countByTenantIdAndStatus(tenantId, status));
    }

    @Override
    public ApprovalModel approveCard(UUID approvalId, String comment, UUID tenantId, UUID userId) {
        Approval approval = approvalRepo.findById(approvalId)
                .orElseThrow(() -> new NoSuchElementException("Approval not found"));
        if (!"PENDING".equals(approval.getStatus()))
            throw new IllegalStateException("Approval is not in PENDING status");

        approval.setStatus("APPROVED");
        approval.setResolvedBy(userId);
        approval.setResolvedAt(Instant.now());
        approval.setComment(comment);
        approvalRepo.save(approval);

        KanbanBoard board = boardRepo.findById(approval.getBoardId())
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        KanbanCard card = findCardInBoard(board, approval.getCardId());
        KanbanList sourceList = findListContainingCard(board, approval.getCardId());
        KanbanList targetList = board.getLists().stream()
                .filter(l -> l.getId().equals(approval.getToListId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Target list not found"));

        doMoveCard(sourceList, targetList, card, null);
        card.setPendingApproval(false);
        card.setPendingApprovalId(null);
        card.setPendingTargetList(null);

        boardRepo.save(board);
        kanbanMetrics.recordCardMoved();
        log.info("Approved card {} moving from '{}' to '{}'",
                approval.getCardId(), sourceList.getTitle(), targetList.getTitle());

        return toApprovalModel(approval);
    }

    @Override
    public ApprovalModel rejectCard(UUID approvalId, String comment, UUID tenantId, UUID userId) {
        Approval approval = approvalRepo.findById(approvalId)
                .orElseThrow(() -> new NoSuchElementException("Approval not found"));
        if (!"PENDING".equals(approval.getStatus()))
            throw new IllegalStateException("Approval is not in PENDING status");
        if (comment == null || comment.isBlank())
            throw new IllegalArgumentException("Comment is required for rejection");

        approval.setStatus("REJECTED");
        approval.setResolvedBy(userId);
        approval.setResolvedAt(Instant.now());
        approval.setComment(comment);
        approvalRepo.save(approval);

        KanbanBoard board = boardRepo.findById(approval.getBoardId())
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        KanbanCard card = findCardInBoard(board, approval.getCardId());
        card.setPendingApproval(false);
        card.setPendingApprovalId(null);
        card.setPendingTargetList(null);
        boardRepo.save(board);
        log.info("Rejected card {} approval", approval.getCardId());

        return toApprovalModel(approval);
    }

    // ==================== Private Helpers ====================

    private KanbanList findTargetList(KanbanBoard board, String targetStepCode, UUID targetListId) {
        if (targetListId != null) {
            return board.getLists().stream()
                    .filter(l -> l.getId().equals(targetListId)).findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Target list not found"));
        }
        if (targetStepCode != null) {
            Optional<WorkflowStep> step = stepRepo.findByBoardTypeCodeAndStepCode(board.getBoardTypeCode(), targetStepCode);
            if (step.isPresent()) {
                return board.getLists().stream()
                        .filter(l -> step.get().getId().equals(l.getWorkflowStepId())).findFirst()
                        .orElseThrow(() -> new NoSuchElementException("List not found for step: " + targetStepCode));
            }
        }
        Optional<WorkflowStep> initialStep = stepRepo.findInitialStep(board.getBoardTypeCode());
        if (initialStep.isPresent()) {
            return board.getLists().stream()
                    .filter(l -> initialStep.get().getId().equals(l.getWorkflowStepId()))
                    .findFirst().orElse(board.getLists().get(0));
        }
        if (board.getLists().isEmpty()) throw new IllegalStateException("Board has no lists");
        return board.getLists().get(0);
    }

    private KanbanBoard findBoardByCardId(UUID cardId, UUID tenantId) {
        List<KanbanBoard> boards = boardRepo.findByTenantId(tenantId);
        for (KanbanBoard board : boards) {
            for (KanbanList list : board.getLists()) {
                for (KanbanCard card : list.getCards()) {
                    if (card.getId().equals(cardId)) return board;
                }
            }
        }
        throw new NoSuchElementException("Card not found");
    }

    private EngineCardModel findCardInBoards(UUID cardId, UUID tenantId) {
        List<KanbanBoard> boards = boardRepo.findByTenantId(tenantId);
        for (KanbanBoard board : boards) {
            for (KanbanList list : board.getLists()) {
                for (KanbanCard card : list.getCards()) {
                    if (card.getId().equals(cardId)) return toEngineCardModel(card, list, board);
                }
            }
        }
        throw new NoSuchElementException("Card not found");
    }

    private KanbanCard findCardInBoard(KanbanBoard board, UUID cardId) {
        for (KanbanList list : board.getLists()) {
            for (KanbanCard card : list.getCards()) {
                if (card.getId().equals(cardId)) return card;
            }
        }
        throw new NoSuchElementException("Card not found in board");
    }

    private KanbanList findListContainingCard(KanbanBoard board, UUID cardId) {
        for (KanbanList list : board.getLists()) {
            for (KanbanCard card : list.getCards()) {
                if (card.getId().equals(cardId)) return list;
            }
        }
        throw new NoSuchElementException("List containing card not found");
    }

    private void doMoveCard(KanbanList sourceList, KanbanList targetList, KanbanCard card, Integer targetPosition) {
        sourceList.getCards().remove(card);
        int newPos = targetPosition != null ? targetPosition : targetList.getCards().size();
        card.setPosition(newPos);
        for (KanbanCard c : targetList.getCards()) {
            if (c.getPosition() != null && c.getPosition() >= newPos) c.setPosition(c.getPosition() + 1);
        }
        targetList.getCards().add(card);
    }

    private Approval createApprovalRequest(KanbanCard card, KanbanBoard board, KanbanList fromList,
                                            KanbanList toList, WorkflowTransition transition,
                                            UUID tenantId, UUID userId) {
        List<ApprovalRule> rules = approvalRuleRepo.findByTransitionId(transition.getId());
        ApprovalRule rule = rules.isEmpty() ? null : rules.get(0);

        Approval approval = Approval.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .cardId(card.getId())
                .boardId(board.getId())
                .fromListId(fromList.getId())
                .toListId(toList.getId())
                .transitionId(transition.getId())
                .approvalRuleId(rule != null ? rule.getId() : null)
                .requestedBy(userId)
                .requestedAt(Instant.now())
                .status("PENDING")
                .isNew(true)
                .build();

        return approvalRepo.save(approval);
    }

    // ==================== Model Conversion ====================

    private BoardTypeModel toBoardTypeModel(BoardType bt, List<String> features) {
        return new BoardTypeModel(bt.getCode(), bt.getName(), bt.getProductCode(),
                bt.getVisibleInKanban(), bt.getSingleton(), bt.getScope(),
                bt.getAutoCreate(), bt.getNumberPrefix(), bt.getAllowCustomLists(), features);
    }

    private EngineBoardModel toEngineBoardModel(KanbanBoard board) {
        Map<UUID, WorkflowStep> stepMap = new HashMap<>();
        if (board.getBoardTypeCode() != null) {
            stepRepo.findByBoardTypeCodeOrderByPositionAsc(board.getBoardTypeCode())
                    .forEach(s -> stepMap.put(s.getId(), s));
        }

        Map<UUID, KanbanLabel> labelMap = new HashMap<>();
        board.getLabels().forEach(l -> labelMap.put(l.getId(), l));

        List<EngineBoardModel.EngineBoardListModel> lists = board.getLists().stream()
                .sorted(Comparator.comparingInt(l -> l.getPosition() != null ? l.getPosition() : 0))
                .map(list -> {
                    WorkflowStep step = list.getWorkflowStepId() != null ? stepMap.get(list.getWorkflowStepId()) : null;
                    List<EngineCardModel> cards = list.getCards().stream()
                            .sorted(Comparator.comparingInt(c -> c.getPosition() != null ? c.getPosition() : 0))
                            .map(card -> toEngineCardModel(card, list, board, step, labelMap))
                            .toList();
                    return new EngineBoardModel.EngineBoardListModel(
                            list.getId(), list.getTitle(), list.getPosition(),
                            list.getWorkflowStepId(),
                            step != null ? step.getStepCode() : null,
                            step != null ? step.getStepName() : null,
                            step != null ? step.getColor() : list.getColor(),
                            step != null ? step.getIsInitial() : null,
                            step != null ? step.getIsFinal() : null,
                            cards);
                })
                .toList();

        List<KanbanLabelModel> labelModels = board.getLabels().stream()
                .map(l -> KanbanLabelModel.builder()
                        .id(l.getId()).name(l.getName()).color(l.getColor())
                        .createdAt(l.getCreatedAt()).updatedAt(l.getUpdatedAt()).build())
                .toList();

        Boolean allowCustomLists = board.getBoardTypeCode() != null ?
                boardTypeRepo.findById(board.getBoardTypeCode()).map(BoardType::getAllowCustomLists).orElse(null) : null;

        int nextCardNumber = board.getLists().stream()
                .flatMap(l -> l.getCards().stream())
                .map(KanbanCard::getCardNumber)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        return new EngineBoardModel(
                board.getId(), board.getTitle(), board.getBoardCode(),
                board.getBoardTypeCode(), board.getTenantId(), board.getUserId(),
                board.getBoardCode(), nextCardNumber, allowCustomLists,
                lists, labelModels, board.getCreatedAt(), board.getUpdatedAt());
    }

    private EngineCardModel toEngineCardModel(KanbanCard card, KanbanList list, KanbanBoard board) {
        WorkflowStep step = list.getWorkflowStepId() != null ?
                stepRepo.findById(list.getWorkflowStepId()).orElse(null) : null;
        Map<UUID, KanbanLabel> labelMap = new HashMap<>();
        board.getLabels().forEach(l -> labelMap.put(l.getId(), l));
        return toEngineCardModel(card, list, board, step, labelMap);
    }

    private EngineCardModel toEngineCardModel(KanbanCard card, KanbanList list, KanbanBoard board,
                                               WorkflowStep step, Map<UUID, KanbanLabel> labelMap) {
        String cardCode = formatCardCode(board.getBoardCode(), card.getCardNumber());

        KanbanPriorityModel priority = (card.getPriorityColor() != null || card.getPriorityTitle() != null) ?
                new KanbanPriorityModel(card.getPriorityColor(), card.getPriorityTitle()) : null;

        List<KanbanAssigneeModel> assignees = new ArrayList<>();
        if (card.getAssigneeUserId() != null) {
            assignees.add(KanbanAssigneeModel.builder().userId(card.getAssigneeUserId()).build());
        }

        List<KanbanCommentModel> comments = card.getComments().stream()
                .map(c -> KanbanCommentModel.builder()
                        .id(c.getId()).text(c.getText()).userId(c.getUserId()).createdAt(c.getCreatedAt()).build())
                .toList();

        List<KanbanSubTaskModel> subtasks = card.getSubTasks().stream()
                .map(st -> KanbanSubTaskModel.builder()
                        .id(st.getId()).text(st.getText()).completed(st.getCompleted())
                        .position(st.getPosition()).dueDate(st.getDueDate())
                        .assignee(st.getAssigneeUserId() != null ?
                                KanbanAssigneeModel.builder().userId(st.getAssigneeUserId()).build() : null)
                        .build())
                .toList();

        List<KanbanLabelModel> labels = card.getLabels().stream()
                .map(cl -> {
                    KanbanLabel label = labelMap.get(cl.getLabelId());
                    if (label != null) {
                        return KanbanLabelModel.builder()
                                .id(label.getId()).name(label.getName()).color(label.getColor())
                                .createdAt(label.getCreatedAt()).updatedAt(label.getUpdatedAt()).build();
                    }
                    return KanbanLabelModel.builder().id(cl.getLabelId()).build();
                })
                .toList();

        return new EngineCardModel(
                card.getId(), cardCode, card.getTitle(), card.getDescription(),
                card.getStartDate(), card.getDueDate(),
                card.getCompleted(), card.getProgress(), card.getPosition(),
                list.getId(), board.getId(),
                step != null ? step.getStepCode() : null,
                step != null ? step.getStepName() : null,
                card.getSourceService(), priority, card.getAttachments(),
                card.getPendingApproval(), card.getPendingApprovalId(), card.getPendingTargetList(),
                assignees, comments, subtasks, labels,
                card.getCreatedAt(), card.getUpdatedAt());
    }

    private ApprovalModel toApprovalModel(Approval a) {
        String cardTitle = null, cardCode = null;
        String fromStepCode = null, fromStepName = null;
        String toStepCode = null, toStepName = null;

        try {
            KanbanBoard board = boardRepo.findById(a.getBoardId()).orElse(null);
            if (board != null) {
                for (KanbanList list : board.getLists()) {
                    for (KanbanCard card : list.getCards()) {
                        if (card.getId().equals(a.getCardId())) {
                            cardTitle = card.getTitle();
                            cardCode = formatCardCode(board.getBoardCode(), card.getCardNumber());
                            break;
                        }
                    }
                    if (cardTitle != null) break;
                }
                for (KanbanList list : board.getLists()) {
                    if (list.getId().equals(a.getFromListId()) && list.getWorkflowStepId() != null) {
                        WorkflowStep s = stepRepo.findById(list.getWorkflowStepId()).orElse(null);
                        fromStepCode = s != null ? s.getStepCode() : list.getTitle();
                        fromStepName = s != null ? s.getStepName() : list.getTitle();
                    }
                    if (list.getId().equals(a.getToListId()) && list.getWorkflowStepId() != null) {
                        WorkflowStep s = stepRepo.findById(list.getWorkflowStepId()).orElse(null);
                        toStepCode = s != null ? s.getStepCode() : list.getTitle();
                        toStepName = s != null ? s.getStepName() : list.getTitle();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error resolving approval details for approval {}", a.getId(), e);
        }

        return new ApprovalModel(a.getId(), a.getCardId(), a.getBoardId(),
                cardTitle, cardCode, fromStepCode, fromStepName, toStepCode, toStepName,
                a.getRequestedBy(), a.getRequestedAt(), a.getResolvedBy(), a.getResolvedAt(),
                a.getStatus(), a.getComment());
    }

    private String formatCardCode(String prefix, Integer cardNumber) {
        if (prefix == null || cardNumber == null) return null;
        return String.format("%s-%04d", prefix, cardNumber);
    }

    private String generateBoardCode(String title) {
        if (title == null || title.isEmpty()) return "BORD";
        String clean = title.replaceAll("[^a-zA-Z0-9\\s]", "");
        String[] words = clean.split("\\s+");
        StringBuilder code = new StringBuilder();
        for (String word : words) {
            if (code.length() >= 4) break;
            if (!word.isEmpty()) code.append(word.substring(0, 1).toUpperCase());
        }
        while (code.length() < 4) code.append("X");
        return code.substring(0, 4);
    }
}
