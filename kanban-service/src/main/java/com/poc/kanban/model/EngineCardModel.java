package com.poc.kanban.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EngineCardModel(
        UUID id,
        String cardCode,
        String title,
        String description,
        LocalDate startDate,
        LocalDate dueDate,
        Boolean completed,
        Integer progress,
        Integer position,
        UUID listId,
        UUID boardId,
        String stepCode,
        String stepName,
        String sourceService,
        KanbanPriorityModel priority,
        Integer attachmentCount,
        Boolean pendingApproval,
        UUID pendingApprovalId,
        UUID pendingTargetListId,
        List<KanbanAssigneeModel> assignees,
        List<KanbanCommentModel> comments,
        List<KanbanSubTaskModel> subtasks,
        List<KanbanLabelModel> labels,
        Instant createdAt,
        Instant updatedAt
) {}
