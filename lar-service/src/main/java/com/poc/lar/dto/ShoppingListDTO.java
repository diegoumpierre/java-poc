package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShoppingListDTO(
    UUID id,
    String name,
    String status,
    UUID createdBy,
    UUID assignedTo,
    Integer budgetCents,
    Integer actualTotalCents,
    Integer itemCount,
    Integer checkedCount,
    LocalDateTime completedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
