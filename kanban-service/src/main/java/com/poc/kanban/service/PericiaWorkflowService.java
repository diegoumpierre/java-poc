package com.poc.kanban.service;

import com.poc.kanban.domain.*;
import com.poc.kanban.repository.jpa.JpaRepositoryKanbanBoard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages pericia workflow boards and cards in the kanban service.
 * This service is called by the PericiaEventConsumer when pericia events arrive via Kafka.
 *
 * Board conventions:
 * - Pericia board: boardCode = "PERI", title = "Processos Pericia"
 *   Lists match processo etapas: "Triagem", "Distribuicao", "Elaboracao", "Revisao", "Entrega", "Entregue"
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PericiaWorkflowService {

    private final JpaRepositoryKanbanBoard jpaRepositoryKanbanBoard;

    private static final String PERICIA_BOARD_CODE = "PERI";
    private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    // Default pericia lists matching processo etapas
    private static final String[] DEFAULT_LISTS = {
        "Triagem", "Distribuicao", "Elaboracao", "Revisao", "Entrega", "Entregue"
    };

    // Etapa name to list index mapping
    private static final java.util.Map<String, Integer> ETAPA_TO_INDEX = java.util.Map.of(
        "TRIAGEM", 0,
        "DISTRIBUICAO", 1,
        "ELABORACAO", 2,
        "REVISAO", 3,
        "ENTREGA", 4,
        "ENTREGUE", 5
    );

    /**
     * Gets or creates the pericia board for a tenant.
     * Creates lists matching processo etapas.
     */
    @Transactional
    public KanbanBoard getOrCreatePericiaBoard(UUID tenantId, List<String> customFases) {
        Optional<KanbanBoard> existing = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(PERICIA_BOARD_CODE, tenantId);
        if (existing.isPresent()) {
            return existing.get();
        }

        log.info("[PericiaWorkflow] Creating pericia board for tenant {}", tenantId);


        String[] listNames = (customFases != null && !customFases.isEmpty())
                ? customFases.toArray(new String[0])
                : DEFAULT_LISTS;

        KanbanBoard board = KanbanBoard.builder()
                .id(UUID.randomUUID())
                .title("Processos Pericia")
                .boardCode(PERICIA_BOARD_CODE)
                .userId(SYSTEM_USER_ID)
                .tenantId(tenantId)
                .lists(new ArrayList<>())
                .labels(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .isNew(true)
                .build();

        for (int i = 0; i < listNames.length; i++) {
            KanbanList list = KanbanList.builder()
                    .title(listNames[i])
                    .position(i)
                    .cards(new ArrayList<>())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            board.getLists().add(list);
        }

        KanbanBoard saved = jpaRepositoryKanbanBoard.save(board);
        log.info("[PericiaWorkflow] Created pericia board {} for tenant {}", saved.getId(), tenantId);
        return saved;
    }

    /**
     * Creates a card for a processo in the appropriate etapa list.
     * Idempotent: skips if a card with the same processo number already exists.
     */
    @Transactional
    public void createProcessoCard(UUID tenantId, UUID processoId, String processoNumber,
                                    String title, String description, String tipo,
                                    LocalDate dueDate, List<String> fases) {
        try {
            KanbanBoard board = getOrCreatePericiaBoard(tenantId, fases);

            // Check idempotency: look for existing card with this processo number
            boolean alreadyExists = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .anyMatch(c -> c.getTitle() != null && c.getDescription() != null
                            && c.getDescription().contains(processoNumber));

            if (alreadyExists) {
                log.debug("[PericiaWorkflow] Card already exists for processo {}, skipping", processoNumber);
                return;
            }

            // New processos start in the first list (Triagem)
            if (board.getLists().isEmpty()) {
                log.warn("[PericiaWorkflow] Board {} has no lists", board.getId());
                return;
            }

            KanbanList targetList = board.getLists().get(0);

            int maxCardNumber = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .map(KanbanCard::getCardNumber)
                    .filter(num -> num != null)
                    .max(Integer::compareTo)
                    .orElse(0);

            int maxPosition = targetList.getCards() != null
                    ? targetList.getCards().stream().mapToInt(KanbanCard::getPosition).max().orElse(-1)
                    : -1;

            KanbanCard card = KanbanCard.builder()
                    .title(title)
                    .description(description)
                    .dueDate(dueDate)
                    .completed(false)
                    .progress(0)
                    .position(maxPosition + 1)
                    .cardNumber(maxCardNumber + 1)
                    .attachments(0)
                    .subTasks(new ArrayList<>())
                    .comments(new ArrayList<>())
                    .labels(new ArrayList<>())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            targetList.getCards().add(card);
            board.markNotNew();
            jpaRepositoryKanbanBoard.save(board);

            log.info("[PericiaWorkflow] Created card for processo {} in tenant {}", processoNumber, tenantId);
        } catch (Exception e) {
            log.error("[PericiaWorkflow] Failed to create card for processo {}: {}", processoNumber, e.getMessage(), e);
        }
    }

    /**
     * Moves a processo card between lists based on fase/etapa change.
     */
    @Transactional
    public void moveProcessoCard(UUID tenantId, UUID processoId, String processoNumber,
                                   String faseFrom, String faseTo) {
        try {
            Optional<KanbanBoard> boardOpt = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(PERICIA_BOARD_CODE, tenantId);
            if (boardOpt.isEmpty()) {
                log.debug("[PericiaWorkflow] No pericia board found for tenant {}, skipping move", tenantId);
                return;
            }

            KanbanBoard board = boardOpt.get();

            Integer targetIndex = ETAPA_TO_INDEX.get(faseTo.toUpperCase());
            if (targetIndex == null || targetIndex >= board.getLists().size()) {
                log.warn("[PericiaWorkflow] Unknown etapa '{}' or index out of bounds", faseTo);
                return;
            }

            // Find card by processo number in description
            KanbanCard targetCard = null;
            KanbanList sourceList = null;

            for (KanbanList list : board.getLists()) {
                for (KanbanCard card : list.getCards()) {
                    if (card.getDescription() != null && card.getDescription().contains(processoNumber)) {
                        targetCard = card;
                        sourceList = list;
                        break;
                    }
                }
                if (targetCard != null) break;
            }

            if (targetCard == null) {
                log.debug("[PericiaWorkflow] No card found for processo {} in board", processoNumber);
                return;
            }

            KanbanList destList = board.getLists().get(targetIndex);

            // Remove from source, add to target
            sourceList.getCards().remove(targetCard);
            int newPosition = destList.getCards() != null ? destList.getCards().size() : 0;
            targetCard.setPosition(newPosition);
            targetCard.setUpdatedAt(Instant.now());
            destList.getCards().add(targetCard);

            board.markNotNew();
            jpaRepositoryKanbanBoard.save(board);
            log.info("[PericiaWorkflow] Moved card for processo {} from {} to {}", processoNumber, faseFrom, faseTo);
        } catch (Exception e) {
            log.error("[PericiaWorkflow] Failed to move card for processo {}: {}", processoNumber, e.getMessage(), e);
        }
    }

    /**
     * Updates a card's title/description based on processo/tarefa update.
     */
    @Transactional
    public void updateCard(UUID tenantId, UUID cardId, String title, String description, LocalDate dueDate) {
        try {
            Optional<KanbanBoard> boardOpt = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(PERICIA_BOARD_CODE, tenantId);
            if (boardOpt.isEmpty()) {
                log.debug("[PericiaWorkflow] No pericia board for tenant {}, skipping update", tenantId);
                return;
            }

            KanbanBoard board = boardOpt.get();

            // Find the card
            for (KanbanList list : board.getLists()) {
                for (KanbanCard card : list.getCards()) {
                    if (card.getId() != null && card.getId().equals(cardId)) {
                        if (title != null) card.setTitle(title);
                        if (description != null) card.setDescription(description);
                        if (dueDate != null) card.setDueDate(dueDate);
                        card.setUpdatedAt(Instant.now());

                        board.markNotNew();
                        jpaRepositoryKanbanBoard.save(board);
                        log.info("[PericiaWorkflow] Updated card {}", cardId);
                        return;
                    }
                }
            }

            log.debug("[PericiaWorkflow] Card {} not found in board", cardId);
        } catch (Exception e) {
            log.error("[PericiaWorkflow] Failed to update card {}: {}", cardId, e.getMessage(), e);
        }
    }

    /**
     * Deletes/archives a card.
     */
    @Transactional
    public void deleteCard(UUID tenantId, UUID cardId) {
        try {
            Optional<KanbanBoard> boardOpt = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(PERICIA_BOARD_CODE, tenantId);
            if (boardOpt.isEmpty()) {
                return;
            }

            KanbanBoard board = boardOpt.get();

            for (KanbanList list : board.getLists()) {
                boolean removed = list.getCards().removeIf(c -> c.getId() != null && c.getId().equals(cardId));
                if (removed) {
                    board.markNotNew();
                    jpaRepositoryKanbanBoard.save(board);
                    log.info("[PericiaWorkflow] Deleted card {} from board", cardId);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("[PericiaWorkflow] Failed to delete card {}: {}", cardId, e.getMessage(), e);
        }
    }

}
