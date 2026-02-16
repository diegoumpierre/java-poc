package com.poc.kanban.model;

import java.util.UUID;

public record CardDetailResponse(
        KanbanCardModel card,
        UUID boardId,
        UUID listId
) {}
