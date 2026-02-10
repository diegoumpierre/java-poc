package com.poc.kanban.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EngineBoardModel(
        UUID id,
        String title,
        String boardCode,
        String boardTypeCode,
        UUID tenantId,
        UUID userId,
        String numberPrefix,
        Integer nextCardNumber,
        Boolean allowCustomLists,
        List<EngineBoardListModel> lists,
        List<KanbanLabelModel> labels,
        Instant createdAt,
        Instant updatedAt
) {

    public record EngineBoardListModel(
            UUID id,
            String title,
            Integer position,
            UUID workflowStepId,
            String stepCode,
            String stepName,
            String color,
            Boolean isInitial,
            Boolean isFinal,
            List<EngineCardModel> cards
    ) {}
}
