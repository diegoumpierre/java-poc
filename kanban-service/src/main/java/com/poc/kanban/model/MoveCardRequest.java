package com.poc.kanban.model;

import java.util.UUID;

public record MoveCardRequest(
        UUID targetListId,
        String targetStepCode,
        Integer position,
        String comment
) {}
