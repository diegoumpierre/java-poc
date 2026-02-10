package com.poc.kanban.model;

import java.util.UUID;

public record WorkflowStepModel(
        UUID id,
        String stepCode,
        String stepName,
        int position,
        String color,
        Boolean isInitial,
        Boolean isFinal
) {}
