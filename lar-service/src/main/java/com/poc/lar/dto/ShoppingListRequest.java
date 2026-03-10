package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ShoppingListRequest(
    @NotBlank(message = "Name is required")
    String name,
    UUID assignedTo,
    Integer budgetCents
) {}
