package com.poc.lar.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShoppingItemDTO(
    UUID id,
    UUID listId,
    String name,
    BigDecimal quantity,
    String unit,
    String category,
    Integer estimatedPriceCents,
    Integer actualPriceCents,
    Boolean checked,
    UUID addedBy,
    UUID checkedBy,
    Boolean recurring,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
