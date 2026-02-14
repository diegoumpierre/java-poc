package com.poc.kanban.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kanban.service.BpfWorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Consumes BPF events from Kafka and creates/moves kanban cards.
 *
 * Events handled:
 * - CLIENTE_BOARD_CREATED -> creates board with document workflow lists
 * - DOCUMENTO_CREATED -> creates a card in the "Pendente" list
 * - DOCUMENTO_STATUS_CHANGED -> moves the card to the corresponding list
 * - NAO_CONFORMIDADE_CREATED -> creates a card for a non-conformity
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class BpfEventConsumer {

    private final ObjectMapper objectMapper;
    private final BpfWorkflowService bpfWorkflowService;

    @KafkaListener(topics = "${app.kafka.topics.bpf-events:bpf-events}", groupId = "kanban-service")
    public void consumeBpfEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            Map<String, Object> event = objectMapper.readValue(record.value(), new TypeReference<>() {});
            String eventType = (String) event.get("eventType");

            log.info("[BpfEventConsumer] Received event: {} for tenant: {}", eventType, event.get("tenantId"));

            switch (eventType) {
                case "CLIENTE_BOARD_CREATED" -> handleClienteBoardCreated(event);
                case "DOCUMENTO_CREATED" -> handleDocumentoCreated(event);
                case "DOCUMENTO_STATUS_CHANGED" -> handleDocumentoStatusChanged(event);
                case "NAO_CONFORMIDADE_CREATED" -> handleNaoConformidadeCreated(event);
                default -> log.debug("[BpfEventConsumer] Ignoring event type: {}", eventType);
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("[BpfEventConsumer] Error processing event: {} | payload: {}",
                    e.getMessage(), record.value(), e);
            // Acknowledge anyway to prevent infinite retry loops
            ack.acknowledge();
        }
    }

    private void handleClienteBoardCreated(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        if (tenantId == null) {
            log.warn("[BpfEventConsumer] Missing tenantId in CLIENTE_BOARD_CREATED event");
            return;
        }

        String clienteName = (String) event.get("clienteName");

        @SuppressWarnings("unchecked")
        List<String> lists = (List<String>) event.get("lists");

        bpfWorkflowService.getOrCreateBpfBoard(tenantId, clienteName, lists);
    }

    private void handleDocumentoCreated(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        if (tenantId == null) {
            log.warn("[BpfEventConsumer] Missing tenantId in DOCUMENTO_CREATED event");
            return;
        }

        UUID clienteId = parseUUID(event.get("clienteId"));
        UUID documentoId = parseUUID(event.get("documentoId"));
        String documentoNome = (String) event.get("documentoNome");
        String documentoTipo = (String) event.get("documentoTipo");
        UUID elaboradorId = parseUUID(event.get("elaboradorId"));
        LocalDate dueDate = parseLocalDate(event.get("dueDate"));

        bpfWorkflowService.createDocumentoCard(
                tenantId, clienteId, documentoId, documentoNome, documentoTipo, elaboradorId, dueDate
        );
    }

    private void handleDocumentoStatusChanged(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        if (tenantId == null) {
            log.warn("[BpfEventConsumer] Missing tenantId in DOCUMENTO_STATUS_CHANGED event");
            return;
        }

        UUID documentoId = parseUUID(event.get("documentoId"));
        UUID cardId = parseUUID(event.get("cardId"));
        String statusFrom = (String) event.get("statusFrom");
        String statusTo = (String) event.get("statusTo");

        if (statusTo == null) {
            log.warn("[BpfEventConsumer] Missing statusTo in DOCUMENTO_STATUS_CHANGED event");
            return;
        }

        bpfWorkflowService.moveDocumentoCard(tenantId, documentoId, cardId, statusFrom, statusTo);
    }

    private void handleNaoConformidadeCreated(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        if (tenantId == null) {
            log.warn("[BpfEventConsumer] Missing tenantId in NAO_CONFORMIDADE_CREATED event");
            return;
        }

        UUID clienteId = parseUUID(event.get("clienteId"));
        UUID ncId = parseUUID(event.get("ncId"));
        String ncCodigo = (String) event.get("ncCodigo");
        String ncDescricao = (String) event.get("ncDescricao");
        String ncCriticidade = (String) event.get("ncCriticidade");
        String ncCategoria = (String) event.get("ncCategoria");
        String ncAcaoSugerida = (String) event.get("ncAcaoSugerida");
        LocalDate prazo = parseLocalDate(event.get("dueDate"));

        bpfWorkflowService.createNcCard(
                tenantId, clienteId, ncId, ncCodigo, ncDescricao,
                ncCriticidade, ncCategoria, ncAcaoSugerida, prazo
        );
    }

    // --- Parsing helpers ---

    private UUID parseUUID(Object value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            log.warn("[BpfEventConsumer] Invalid UUID: {}", value);
            return null;
        }
    }

    private LocalDate parseLocalDate(Object value) {
        if (value == null) return null;
        try {
            return LocalDate.parse(value.toString());
        } catch (Exception e) {
            log.warn("[BpfEventConsumer] Failed to parse LocalDate: {}", value);
            return null;
        }
    }
}
