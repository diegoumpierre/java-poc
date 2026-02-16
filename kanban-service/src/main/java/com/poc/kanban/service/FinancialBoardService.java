package com.poc.kanban.service;

import com.poc.kanban.domain.*;
import com.poc.kanban.repository.jpa.JpaRepositoryKanbanBoard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages financial boards and cards in the kanban service.
 * This service is called by the BillingEventConsumer when billing events arrive via Kafka.
 *
 * Board conventions:
 * - Financial board: boardCode = "FINX", title = "Financeiro"
 *   Lists: "A Gerar", "Faturado", "Vencendo", "Vencido", "Pago", "Cancelado"
 * - Commission board: boardCode = "COMX", title = "Comissoes"
 *   Lists: "Pendente", "Aprovada", "Paga"
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialBoardService {

    private final JpaRepositoryKanbanBoard jpaRepositoryKanbanBoard;

    private static final String FINANCIAL_BOARD_CODE = "FINX";
    private static final String COMMISSION_BOARD_CODE = "COMX";

    private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    // Financial board list titles (order matters - position = index)
    private static final String[] FINANCIAL_LISTS = {
        "A Gerar", "Faturado", "Vencendo", "Vencido", "Pago", "Cancelado"
    };
    private static final int LIST_FATURADO = 1;
    private static final int LIST_VENCIDO = 3;
    private static final int LIST_PAGO = 4;

    // Commission board list titles
    private static final String[] COMMISSION_LISTS = {
        "Pendente", "Aprovada", "Paga"
    };
    private static final int COMM_PENDENTE = 0;

    /**
     * Gets or creates the financial board for a tenant.
     */
    @Transactional
    public KanbanBoard getOrCreateFinancialBoard(UUID tenantId) {
        Optional<KanbanBoard> existing = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(FINANCIAL_BOARD_CODE, tenantId);
        if (existing.isPresent()) {
            return existing.get();
        }

        log.info("[FinancialBoard] Creating financial board for tenant {}", tenantId);


        KanbanBoard board = KanbanBoard.builder()
                .id(UUID.randomUUID())
                .title("Financeiro")
                .boardCode(FINANCIAL_BOARD_CODE)
                .userId(SYSTEM_USER_ID)
                .tenantId(tenantId)
                .lists(new ArrayList<>())
                .labels(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .isNew(true)
                .build();

        for (int i = 0; i < FINANCIAL_LISTS.length; i++) {
            KanbanList list = KanbanList.builder()
                    .title(FINANCIAL_LISTS[i])
                    .position(i)
                    .cards(new ArrayList<>())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            board.getLists().add(list);
        }

        KanbanBoard saved = jpaRepositoryKanbanBoard.save(board);
        log.info("[FinancialBoard] Created financial board {} for tenant {}", saved.getId(), tenantId);
        return saved;
    }

    /**
     * Gets or creates the commission board for a reseller tenant.
     */
    @Transactional
    public KanbanBoard getOrCreateCommissionBoard(UUID tenantId) {
        Optional<KanbanBoard> existing = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(COMMISSION_BOARD_CODE, tenantId);
        if (existing.isPresent()) {
            return existing.get();
        }

        log.info("[FinancialBoard] Creating commission board for tenant {}", tenantId);


        KanbanBoard board = KanbanBoard.builder()
                .id(UUID.randomUUID())
                .title("Comissoes")
                .boardCode(COMMISSION_BOARD_CODE)
                .userId(SYSTEM_USER_ID)
                .tenantId(tenantId)
                .lists(new ArrayList<>())
                .labels(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .isNew(true)
                .build();

        for (int i = 0; i < COMMISSION_LISTS.length; i++) {
            KanbanList list = KanbanList.builder()
                    .title(COMMISSION_LISTS[i])
                    .position(i)
                    .cards(new ArrayList<>())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            board.getLists().add(list);
        }

        KanbanBoard saved = jpaRepositoryKanbanBoard.save(board);
        log.info("[FinancialBoard] Created commission board {} for tenant {}", saved.getId(), tenantId);
        return saved;
    }

    /**
     * Creates an invoice card in the "Faturado" list.
     * Idempotent: skips if a card with the same invoiceId already exists (checked by title prefix).
     */
    @Transactional
    public void createInvoiceCard(UUID tenantId, UUID invoiceId, String invoiceNumber,
                                   BigDecimal amount, String currency, Instant dueDate,
                                   String description, String billingModel) {
        try {
            KanbanBoard board = getOrCreateFinancialBoard(tenantId);

            // Check idempotency: look for existing card with this invoice reference
            String cardTitlePrefix = "Fatura " + invoiceNumber;
            boolean alreadyExists = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .anyMatch(c -> c.getTitle() != null && c.getTitle().startsWith(cardTitlePrefix));

            if (alreadyExists) {
                log.debug("[FinancialBoard] Invoice card already exists for {}, skipping", invoiceNumber);
                return;
            }

            if (board.getLists().size() <= LIST_FATURADO) {
                log.warn("[FinancialBoard] Financial board {} has insufficient lists", board.getId());
                return;
            }

            KanbanList faturadoList = board.getLists().get(LIST_FATURADO);

            String formattedAmount = amount != null
                    ? String.format("%.2f", amount)
                    : "0.00";

            String title = String.format("Fatura %s - %s",
                    invoiceNumber,
                    description != null ? description : "Assinatura");

            String cardDescription = String.format(
                    "Valor: R$ %s\nVencimento: %s\nModelo: %s",
                    formattedAmount,
                    dueDate != null ? LocalDate.ofInstant(dueDate, ZoneId.systemDefault()).toString() : "N/A",
                    billingModel != null ? billingModel : "DIRECT"
            );

            LocalDate cardDueDate = dueDate != null
                    ? LocalDate.ofInstant(dueDate, ZoneId.systemDefault())
                    : null;

            int maxCardNumber = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .map(KanbanCard::getCardNumber)
                    .filter(num -> num != null)
                    .max(Integer::compareTo)
                    .orElse(0);

            int maxPosition = faturadoList.getCards() != null
                    ? faturadoList.getCards().stream().mapToInt(KanbanCard::getPosition).max().orElse(-1)
                    : -1;

            KanbanCard card = KanbanCard.builder()
                    .title(title)
                    .description(cardDescription)
                    .dueDate(cardDueDate)
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

            faturadoList.getCards().add(card);
            board.markNotNew();
            jpaRepositoryKanbanBoard.save(board);

            log.info("[FinancialBoard] Created invoice card for invoice {} in tenant {}", invoiceNumber, tenantId);
        } catch (Exception e) {
            log.error("[FinancialBoard] Failed to create invoice card for {}: {}", invoiceNumber, e.getMessage(), e);
        }
    }

    /**
     * Moves an invoice card to the "Pago" list.
     */
    @Transactional
    public void moveInvoiceToPago(UUID tenantId, UUID invoiceId) {
        try {
            Optional<KanbanBoard> boardOpt = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(FINANCIAL_BOARD_CODE, tenantId);
            if (boardOpt.isEmpty()) {
                log.debug("[FinancialBoard] No financial board found for tenant {}, skipping move", tenantId);
                return;
            }

            KanbanBoard board = boardOpt.get();
            moveCardByInvoiceId(board, invoiceId, LIST_PAGO);
        } catch (Exception e) {
            log.error("[FinancialBoard] Failed to move invoice {} to Pago: {}", invoiceId, e.getMessage(), e);
        }
    }

    /**
     * Moves an invoice card to the "Vencido" list.
     */
    @Transactional
    public void moveInvoiceToVencido(UUID tenantId, UUID invoiceId) {
        try {
            Optional<KanbanBoard> boardOpt = jpaRepositoryKanbanBoard.findByBoardCodeAndTenantId(FINANCIAL_BOARD_CODE, tenantId);
            if (boardOpt.isEmpty()) {
                log.debug("[FinancialBoard] No financial board found for tenant {}, skipping move", tenantId);
                return;
            }

            KanbanBoard board = boardOpt.get();
            moveCardByInvoiceId(board, invoiceId, LIST_VENCIDO);
        } catch (Exception e) {
            log.error("[FinancialBoard] Failed to move invoice {} to Vencido: {}", invoiceId, e.getMessage(), e);
        }
    }

    /**
     * Creates a commission card in the reseller's commission board.
     * Idempotent: skips if a card for the same monthYear/tenantId already exists.
     */
    @Transactional
    public void createCommissionCard(UUID resellerTenantId, UUID clientTenantId,
                                      String monthYear, BigDecimal grossAmount,
                                      BigDecimal commissionRate, BigDecimal commissionAmount) {
        try {
            KanbanBoard board = getOrCreateCommissionBoard(resellerTenantId);

            // Idempotency: check if card for this month/client already exists
            String cardTitlePrefix = "Comissao " + monthYear;
            boolean alreadyExists = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .anyMatch(c -> c.getTitle() != null
                            && c.getTitle().startsWith(cardTitlePrefix)
                            && c.getDescription() != null
                            && c.getDescription().contains(clientTenantId.toString()));

            if (alreadyExists) {
                log.debug("[FinancialBoard] Commission card already exists for {} / {}, skipping",
                        monthYear, clientTenantId);
                return;
            }

            if (board.getLists().size() <= COMM_PENDENTE) {
                log.warn("[FinancialBoard] Commission board has insufficient lists");
                return;
            }

            KanbanList pendenteList = board.getLists().get(COMM_PENDENTE);

            String title = String.format("Comissao %s - Cliente %s",
                    monthYear, clientTenantId.toString().substring(0, 8));

            String cardDescription = String.format(
                    "Valor bruto: R$ %s\nTaxa: %s%%\nComissao: R$ %s\nCliente: %s",
                    grossAmount != null ? grossAmount.toPlainString() : "0.00",
                    commissionRate != null ? commissionRate.toPlainString() : "0",
                    commissionAmount != null ? commissionAmount.toPlainString() : "0.00",
                    clientTenantId
            );

            int maxCardNumber = board.getLists().stream()
                    .flatMap(l -> l.getCards().stream())
                    .map(KanbanCard::getCardNumber)
                    .filter(num -> num != null)
                    .max(Integer::compareTo)
                    .orElse(0);

            int maxPosition = pendenteList.getCards() != null
                    ? pendenteList.getCards().stream().mapToInt(KanbanCard::getPosition).max().orElse(-1)
                    : -1;

            KanbanCard card = KanbanCard.builder()
                    .title(title)
                    .description(cardDescription)
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

            pendenteList.getCards().add(card);
            board.markNotNew();
            jpaRepositoryKanbanBoard.save(board);

            log.info("[FinancialBoard] Created commission card for reseller {} month {}", resellerTenantId, monthYear);
        } catch (Exception e) {
            log.error("[FinancialBoard] Failed to create commission card: {}", e.getMessage(), e);
        }
    }

    // --- Private helpers ---

    /**
     * Finds a card related to an invoice (by invoiceId in the title) and moves it to the target list.
     */
    private void moveCardByInvoiceId(KanbanBoard board, UUID invoiceId, int targetListIndex) {
        if (board.getLists().size() <= targetListIndex) {
            log.warn("[FinancialBoard] Board {} has insufficient lists for target index {}", board.getId(), targetListIndex);
            return;
        }

        String invoiceRef = invoiceId.toString().substring(0, 8);

        // Find the card across all lists
        KanbanCard targetCard = null;
        KanbanList sourceList = null;

        for (KanbanList list : board.getLists()) {
            for (KanbanCard card : list.getCards()) {
                if (card.getTitle() != null && card.getTitle().contains(invoiceRef)) {
                    targetCard = card;
                    sourceList = list;
                    break;
                }
            }
            if (targetCard != null) break;
        }

        if (targetCard == null) {
            log.debug("[FinancialBoard] No card found for invoice {} in board {}", invoiceId, board.getId());
            return;
        }

        KanbanList targetList = board.getLists().get(targetListIndex);

        // Remove from source, add to target
        sourceList.getCards().remove(targetCard);
        int newPosition = targetList.getCards() != null ? targetList.getCards().size() : 0;
        targetCard.setPosition(newPosition);
        targetCard.setUpdatedAt(Instant.now());
        targetList.getCards().add(targetCard);

        board.markNotNew();
        jpaRepositoryKanbanBoard.save(board);
        log.info("[FinancialBoard] Moved card for invoice {} to list index {}", invoiceId, targetListIndex);
    }

}
