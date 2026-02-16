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
 * Manages BPF workflow boards and cards in the kanban service.
 * This service is called by the BpfEventConsumer when BPF events arrive via Kafka.
 *
 * Board conventions:
 * - BPF client board: boardCode = "BPFX", title = "Documentos - {clienteName}"
 *   Lists: "Pendente", "Em Elaboracao", "Revisao Interna", "Aguardando Cliente", "Aprovado", "Vigente"
 *
 * Each BPF tenant+client gets its own board. The boardCode is used as "BPFX" and
 * client-specific identification is done via the board title.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BpfWorkflowService {

    private final JpaRepositoryKanbanBoard jpaRepositoryKanbanBoard;

    private static final String BPF_BOARD_CODE = "BPFX";
    private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    // Default BPF document workflow lists
    private static final String[] DEFAULT_LISTS = {
        "Pendente", "Em Elaboracao", "Revisao Interna",
        "Aguardando Cliente", "Aprovado", "Vigente"
    };

    // Status to list index mapping
    private static final java.util.Map<String, Integer> STATUS_TO_INDEX = java.util.Map.ofEntries(
        java.util.Map.entry("RASCUNHO", 0),
        java.util.Map.entry("PENDENTE", 0),
        java.util.Map.entry("IDENTIFICADA", 0),
        java.util.Map.entry("EM_ELABORACAO", 1),
        java.util.Map.entry("EM_ANDAMENTO", 1),
        java.util.Map.entry("REVISAO", 2),
        java.util.Map.entry("REVISAO_INTERNA", 2),
        java.util.Map.entry("AGUARDANDO_CLIENTE", 3),
        java.util.Map.entry("APROVADO", 4),
        java.util.Map.entry("VIGENTE", 5),
        java.util.Map.entry("RESOLVIDA", 5),
        java.util.Map.entry("CONCLUIDA", 5)
    );

    /**
     * Gets or creates a BPF board for a tenant.
     * BPF uses a single board per tenant (not per client) to simplify management.
     */
    @Transactional
    public KanbanBoard getOrCreateBpfBoard(UUID tenantId, String clienteName, List<String> customLists) {
        Optional<KanbanBoard> existing = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(BPF_BOARD_CODE, tenantId);
        if (existing.isPresent()) {
            return existing.get();
        }

        log.info("[BpfWorkflow] Creating BPF board for tenant {}", tenantId);


        String boardTitle = clienteName != null
                ? "Documentos - " + clienteName
                : "BPF Documentos";

        String[] listNames = (customLists != null && !customLists.isEmpty())
                ? customLists.toArray(new String[0])
                : DEFAULT_LISTS;

        KanbanBoard board = KanbanBoard.builder()
                .id(UUID.randomUUID())
                .title(boardTitle)
                .boardCode(BPF_BOARD_CODE)
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
        log.info("[BpfWorkflow] Created BPF board {} for tenant {}", saved.getId(), tenantId);
        return saved;
    }

    /**
     * Creates a card for a BPF document in the "Pendente" list (first list).
     * Idempotent: skips if a card with the same document name already exists.
     */
    @Transactional
    public void createDocumentoCard(UUID tenantId, UUID clienteId, UUID documentoId,
                                      String documentoNome, String documentoTipo,
                                      UUID elaboradorId, LocalDate dueDate) {
        try {
            KanbanBoard board = getOrCreateBpfBoard(tenantId, null, null);

            // Idempotency: check by document name
            String idempotencyKey = documentoId != null ? documentoId.toString() : documentoNome;
            boolean alreadyExists = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .anyMatch(c -> c.getDescription() != null && c.getDescription().contains(idempotencyKey));

            if (alreadyExists) {
                log.debug("[BpfWorkflow] Card already exists for documento {}, skipping", documentoNome);
                return;
            }

            if (board.getLists().isEmpty()) {
                log.warn("[BpfWorkflow] Board {} has no lists", board.getId());
                return;
            }

            KanbanList pendingList = board.getLists().get(0);

            String description = String.format("Documento: %s\nTipo: %s\nCliente: %s\nDoc ID: %s",
                    documentoNome,
                    documentoTipo != null ? documentoTipo : "N/A",
                    clienteId != null ? clienteId.toString().substring(0, 8) : "N/A",
                    documentoId != null ? documentoId.toString() : "N/A"
            );

            int maxCardNumber = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .map(KanbanCard::getCardNumber)
                    .filter(num -> num != null)
                    .max(Integer::compareTo)
                    .orElse(0);

            int maxPosition = pendingList.getCards() != null
                    ? pendingList.getCards().stream().mapToInt(KanbanCard::getPosition).max().orElse(-1)
                    : -1;

            KanbanCard card = KanbanCard.builder()
                    .title(documentoNome)
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

            pendingList.getCards().add(card);
            board.markNotNew();
            jpaRepositoryKanbanBoard.save(board);

            log.info("[BpfWorkflow] Created card for documento {} in tenant {}", documentoNome, tenantId);
        } catch (Exception e) {
            log.error("[BpfWorkflow] Failed to create card for documento {}: {}", documentoNome, e.getMessage(), e);
        }
    }

    /**
     * Moves a document card to the list corresponding to the new status.
     */
    @Transactional
    public void moveDocumentoCard(UUID tenantId, UUID documentoId, UUID cardId,
                                    String statusFrom, String statusTo) {
        try {
            Optional<KanbanBoard> boardOpt = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(BPF_BOARD_CODE, tenantId);
            if (boardOpt.isEmpty()) {
                log.debug("[BpfWorkflow] No BPF board found for tenant {}, skipping move", tenantId);
                return;
            }

            KanbanBoard board = boardOpt.get();

            Integer targetIndex = STATUS_TO_INDEX.get(statusTo);
            if (targetIndex == null || targetIndex >= board.getLists().size()) {
                log.warn("[BpfWorkflow] Unknown status '{}' or index out of bounds", statusTo);
                return;
            }

            // Find card by cardId or by documentoId in description
            KanbanCard targetCard = null;
            KanbanList sourceList = null;

            for (KanbanList list : board.getLists()) {
                for (KanbanCard card : list.getCards()) {
                    boolean matchById = cardId != null && card.getId() != null && card.getId().equals(cardId);
                    boolean matchByDoc = documentoId != null && card.getDescription() != null
                            && card.getDescription().contains(documentoId.toString());
                    if (matchById || matchByDoc) {
                        targetCard = card;
                        sourceList = list;
                        break;
                    }
                }
                if (targetCard != null) break;
            }

            if (targetCard == null) {
                log.debug("[BpfWorkflow] No card found for documento {} / cardId {}", documentoId, cardId);
                return;
            }

            KanbanList destList = board.getLists().get(targetIndex);

            sourceList.getCards().remove(targetCard);
            int newPosition = destList.getCards() != null ? destList.getCards().size() : 0;
            targetCard.setPosition(newPosition);
            targetCard.setUpdatedAt(Instant.now());
            destList.getCards().add(targetCard);

            board.markNotNew();
            jpaRepositoryKanbanBoard.save(board);
            log.info("[BpfWorkflow] Moved card for documento {} from {} to {}", documentoId, statusFrom, statusTo);
        } catch (Exception e) {
            log.error("[BpfWorkflow] Failed to move card: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates a card for a non-conformity (NC) in the "Pendente" list.
     */
    @Transactional
    public void createNcCard(UUID tenantId, UUID clienteId, UUID ncId,
                               String ncCodigo, String ncDescricao, String ncCriticidade,
                               String ncCategoria, String ncAcaoSugerida, LocalDate prazo) {
        try {
            KanbanBoard board = getOrCreateBpfBoard(tenantId, null, null);

            // Idempotency
            boolean alreadyExists = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .anyMatch(c -> c.getTitle() != null && c.getTitle().contains(ncCodigo));

            if (alreadyExists) {
                log.debug("[BpfWorkflow] NC card already exists for {}, skipping", ncCodigo);
                return;
            }

            if (board.getLists().isEmpty()) {
                log.warn("[BpfWorkflow] Board has no lists");
                return;
            }

            KanbanList pendingList = board.getLists().get(0);

            String criticidadeLabel = switch (ncCriticidade != null ? ncCriticidade : "") {
                case "CRITICA" -> "CRITICA";
                case "MAIOR" -> "MAIOR";
                case "MENOR" -> "MENOR";
                default -> "OBSERVACAO";
            };

            String title = ncCodigo + " - " + ncDescricao;
            String description = String.format(
                    "Categoria: %s\nCriticidade: %s\nAcao sugerida: %s\nCliente: %s",
                    ncCategoria != null ? ncCategoria : "N/A",
                    ncCriticidade != null ? ncCriticidade : "N/A",
                    ncAcaoSugerida != null ? ncAcaoSugerida : "N/A",
                    clienteId != null ? clienteId.toString().substring(0, 8) : "N/A"
            );

            int maxCardNumber = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .map(KanbanCard::getCardNumber)
                    .filter(num -> num != null)
                    .max(Integer::compareTo)
                    .orElse(0);

            int maxPosition = pendingList.getCards() != null
                    ? pendingList.getCards().stream().mapToInt(KanbanCard::getPosition).max().orElse(-1)
                    : -1;

            KanbanCard card = KanbanCard.builder()
                    .title(title)
                    .description(description)
                    .dueDate(prazo)
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

            pendingList.getCards().add(card);
            board.markNotNew();
            jpaRepositoryKanbanBoard.save(board);

            log.info("[BpfWorkflow] Created NC card for {} in tenant {}", ncCodigo, tenantId);
        } catch (Exception e) {
            log.error("[BpfWorkflow] Failed to create NC card for {}: {}", ncCodigo, e.getMessage(), e);
        }
    }

}
