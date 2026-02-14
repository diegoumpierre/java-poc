package com.poc.kanban.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kanban.service.PericiaWorkflowService;
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
 * Consumes pericia events from Kafka and creates/moves/updates kanban cards.
 *
 * Events handled:
 * - PROCESSO_CREATED -> creates board (if needed) and card in "Triagem" list
 * - PROCESSO_FASE_CHANGED -> moves card to the target fase list
 * - TAREFA_CREATED -> creates a card for a tarefa
 * - TAREFA_UPDATED -> updates a card
 * - TAREFA_DELETED -> removes a card
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class PericiaEventConsumer {

    private final ObjectMapper objectMapper;
    private final PericiaWorkflowService periciaWorkflowService;

    @KafkaListener(topics = "${app.kafka.topics.pericia-events:pericia-events}", groupId = "kanban-service")
    public void consumePericiaEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            Map<String, Object> event = objectMapper.readValue(record.value(), new TypeReference<>() {});
            String eventType = (String) event.get("eventType");

            log.info("[PericiaEventConsumer] Received event: {} for tenant: {}", eventType, event.get("tenantId"));

            switch (eventType) {
                case "PROCESSO_CREATED" -> handleProcessoCreated(event);
                case "PROCESSO_FASE_CHANGED" -> handleProcessoFaseChanged(event);
                case "TAREFA_CREATED" -> handleTarefaCreated(event);
                case "TAREFA_UPDATED" -> handleTarefaUpdated(event);
                case "TAREFA_DELETED" -> handleTarefaDeleted(event);
                default -> log.debug("[PericiaEventConsumer] Ignoring event type: {}", eventType);
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("[PericiaEventConsumer] Error processing event: {} | payload: {}",
                    e.getMessage(), record.value(), e);
            // Acknowledge anyway to prevent infinite retry loops
            ack.acknowledge();
        }
    }

    private void handleProcessoCreated(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        UUID processoId = parseUUID(event.get("processoId"));
        if (tenantId == null || processoId == null) {
            log.warn("[PericiaEventConsumer] Missing tenantId or processoId in PROCESSO_CREATED event");
            return;
        }

        String processoNumber = (String) event.get("processoNumber");
        String title = (String) event.get("title");
        String description = (String) event.get("description");
        String tipo = (String) event.get("tipo");
        LocalDate dueDate = parseLocalDate(event.get("dueDate"));

        @SuppressWarnings("unchecked")
        List<String> fases = (List<String>) event.get("fases");

        periciaWorkflowService.createProcessoCard(
                tenantId, processoId, processoNumber, title, description, tipo, dueDate, fases
        );
    }

    private void handleProcessoFaseChanged(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        UUID processoId = parseUUID(event.get("processoId"));
        if (tenantId == null) {
            log.warn("[PericiaEventConsumer] Missing tenantId in PROCESSO_FASE_CHANGED event");
            return;
        }

        String processoNumber = (String) event.get("processoNumber");
        String faseFrom = (String) event.get("faseFrom");
        String faseTo = (String) event.get("faseTo");

        if (faseFrom == null || faseTo == null) {
            log.warn("[PericiaEventConsumer] Missing faseFrom or faseTo in PROCESSO_FASE_CHANGED event");
            return;
        }

        periciaWorkflowService.moveProcessoCard(tenantId, processoId, processoNumber, faseFrom, faseTo);
    }

    private void handleTarefaCreated(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        UUID processoId = parseUUID(event.get("processoId"));
        UUID tarefaId = parseUUID(event.get("tarefaId"));
        if (tenantId == null) {
            log.warn("[PericiaEventConsumer] Missing tenantId in TAREFA_CREATED event");
            return;
        }

        String title = (String) event.get("title");
        String description = (String) event.get("description");
        String priority = (String) event.get("priority");
        LocalDate dueDate = parseLocalDate(event.get("dueDate"));

        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) event.get("labels");

        // Tarefa cards are added to the board as new cards
        periciaWorkflowService.createProcessoCard(
                tenantId, processoId,
                tarefaId != null ? tarefaId.toString().substring(0, 8) : "tarefa",
                title, description, null, dueDate, null
        );
    }

    private void handleTarefaUpdated(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        UUID cardId = parseUUID(event.get("cardId"));
        if (tenantId == null || cardId == null) {
            log.debug("[PericiaEventConsumer] Missing tenantId or cardId in TAREFA_UPDATED event, skipping");
            return;
        }

        String title = (String) event.get("title");
        String description = (String) event.get("description");
        LocalDate dueDate = parseLocalDate(event.get("dueDate"));

        periciaWorkflowService.updateCard(tenantId, cardId, title, description, dueDate);
    }

    private void handleTarefaDeleted(Map<String, Object> event) {
        UUID tenantId = parseUUID(event.get("tenantId"));
        UUID cardId = parseUUID(event.get("cardId"));
        if (tenantId == null || cardId == null) {
            log.debug("[PericiaEventConsumer] Missing tenantId or cardId in TAREFA_DELETED event, skipping");
            return;
        }

        periciaWorkflowService.deleteCard(tenantId, cardId);
    }

    // --- Parsing helpers ---

    private UUID parseUUID(Object value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            log.warn("[PericiaEventConsumer] Invalid UUID: {}", value);
            return null;
        }
    }

    private LocalDate parseLocalDate(Object value) {
        if (value == null) return null;
        try {
            return LocalDate.parse(value.toString());
        } catch (Exception e) {
            log.warn("[PericiaEventConsumer] Failed to parse LocalDate: {}", value);
            return null;
        }
    }
}
