package com.poc.kanban.model;

import java.util.UUID;

public record WorkflowTransitionModel(
        UUID id,
        String fromStepCode,
        String toStepCode,
        Boolean requiresComment,
        Boolean requiresApproval
) {}
