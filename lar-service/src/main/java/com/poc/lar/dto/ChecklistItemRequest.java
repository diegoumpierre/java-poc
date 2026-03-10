package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

public record ChecklistItemRequest(
    @NotBlank(message = "Description is required")
    String description,
    Integer orderIndex,
    Boolean required,
    Boolean requiresPhoto
) {}
