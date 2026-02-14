package com.poc.kanban.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kanban.service.FinancialBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Consumes billing events from Kafka and creates/moves financial kanban cards.
 *
 * Events handled:
 * - INVOICE_CREATED -> creates a card in the "Faturado" list
 * - INVOICE_PAID -> moves the card to "Pago" list
 * - INVOICE_OVERDUE -> moves the card to "Vencido" list
 * - COMMISSION_CALCULATED -> creates a card in the "Comissoes" board
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class BillingEventConsumer {

    private final ObjectMapper objectMapper;
    private final FinancialBoardService financialBoardService;

    @KafkaListener(topics = "${app.kafka.topics.billing-events:billing-events}", groupId = "kanban-service")
    public void consumeBillingEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            Map<String, Object> event = objectMapper.readValue(record.value(), new TypeReference<>() {});
            String eventType = (String) event.get("eventType");

            log.info("[BillingEventConsumer] Received event: {} for tenant: {}", eventType, event.get("tenantId"));

            switch (eventType) {
                case "INVOICE_CREATED" -> handleInvoiceCreated(event);
                case "INVOICE_PAID" -> handleInvoicePaid(event);
                case "INVOICE_OVERDUE" -> handleInvoiceOverdue(event);
                case "COMMISSION_CALCULATED" -> handleCommissionCalculated(event);
                default -> log.debug("[BillingEventConsumer] Ignoring event type: {}", eventType);
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("[BillingEventConsumer] Error processing event: {} | payload: {}",
                    e.getMessage(), record.value(), e);
            // Acknowledge anyway to prevent infinite retry loops
            ack.acknowledge();
        }
    }

    private void handleInvoiceCreated(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        UUID invoiceId = parseUUID(event.get("invoiceId"));
        if (tenantId == null || invoiceId == null) {
            log.warn("[BillingEventConsumer] Missing tenantId or invoiceId in INVOICE_CREATED event");
            return;
        }

        BigDecimal amount = parseAmount(event.get("amount"));
        String currency = (String) event.getOrDefault("currency", "BRL");
        String description = (String) event.get("description");
        String billingModel = (String) event.get("billingModel");
        String invoiceNumber = (String) event.get("invoiceNumber");
        Instant dueDate = parseInstant(event.get("dueDate"));

        if (invoiceNumber == null) {
            invoiceNumber = invoiceId.toString().substring(0, 8);
        }

        financialBoardService.createInvoiceCard(
                tenantId, invoiceId, invoiceNumber, amount, currency, dueDate, description, billingModel
        );
    }

    private void handleInvoicePaid(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        UUID invoiceId = parseUUID(event.get("invoiceId"));
        if (tenantId == null || invoiceId == null) {
            log.warn("[BillingEventConsumer] Missing tenantId or invoiceId in INVOICE_PAID event");
            return;
        }

        financialBoardService.moveInvoiceToPago(tenantId, invoiceId);
    }

    private void handleInvoiceOverdue(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        UUID invoiceId = parseUUID(event.get("invoiceId"));
        if (tenantId == null || invoiceId == null) {
            log.warn("[BillingEventConsumer] Missing tenantId or invoiceId in INVOICE_OVERDUE event");
            return;
        }

        financialBoardService.moveInvoiceToVencido(tenantId, invoiceId);
    }

    private void handleCommissionCalculated(Map<String, Object> event) {
        UUID clientTenantId = parseUUID(event.get("tenantId"));
        UUID resellerTenantId = parseUUID(event.get("parentTenantId"));
        if (resellerTenantId == null) {
            log.warn("[BillingEventConsumer] Missing parentTenantId in COMMISSION_CALCULATED event");
            return;
        }

        BigDecimal grossAmount = parseAmount(event.get("amount"));
        BigDecimal commissionRate = parseAmount(event.get("commissionRate"));
        BigDecimal commissionAmount = parseAmount(event.get("commissionAmount"));
        String monthYear = (String) event.get("monthYear");

        if (monthYear == null) {
            monthYear = java.time.YearMonth.now().toString();
        }

        financialBoardService.createCommissionCard(
                resellerTenantId, clientTenantId, monthYear, grossAmount, commissionRate, commissionAmount
        );
    }

    // --- Parsing helpers ---

    private UUID parseUUID(Object value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            log.warn("[BillingEventConsumer] Invalid UUID: {}", value);
            return null;
        }
    }

    private BigDecimal parseAmount(Object amountObj) {
        if (amountObj == null) return BigDecimal.ZERO;
        if (amountObj instanceof Number num) return BigDecimal.valueOf(num.doubleValue());
        try {
            return new BigDecimal(amountObj.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private Instant parseInstant(Object value) {
        if (value == null) return null;
        try {
            if (value instanceof Number num) {
                return Instant.ofEpochSecond(num.longValue());
            }
            return Instant.parse(value.toString());
        } catch (Exception e) {
            log.warn("[BillingEventConsumer] Failed to parse instant: {}", value);
            return null;
        }
    }
}
