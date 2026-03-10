package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PantryItemRequest(
    @NotBlank(message = "Name is required")
    String name,
    BigDecimal quantity,
    String unit,
    String category,
    LocalDate expiryDate,
    String status,
    Boolean autoAddToList,
    String preferredBrand
) {}
