package com.poc.kanban.model;

import java.util.List;

public record WorkflowModel(
        String boardTypeCode,
        List<WorkflowStepModel> steps,
        List<WorkflowTransitionModel> transitions
) {}
