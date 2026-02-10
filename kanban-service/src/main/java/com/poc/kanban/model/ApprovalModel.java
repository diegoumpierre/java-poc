package com.poc.kanban.model;

import java.time.Instant;
import java.util.UUID;

public record ApprovalModel(
        UUID id,
        UUID cardId,
        UUID boardId,
        String cardTitle,
        String cardCode,
        String fromStepCode,
        String fromStepName,
        String toStepCode,
        String toStepName,
        UUID requestedBy,
        Instant requestedAt,
        UUID resolvedBy,
        Instant resolvedAt,
        String status,
        String comment
) {}
