package com.poc.lar.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PantryItemDTO(
    UUID id,
    String name,
    BigDecimal quantity,
    String unit,
    String category,
    LocalDate expiryDate,
    String status,
    Boolean autoAddToList,
    String preferredBrand,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
