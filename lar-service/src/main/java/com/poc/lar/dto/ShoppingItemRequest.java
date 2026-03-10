package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ShoppingItemRequest(
    @NotBlank(message = "Name is required")
    String name,
    BigDecimal quantity,
    String unit,
    String category,
    Integer estimatedPriceCents,
    Boolean recurring,
    String notes
) {}
