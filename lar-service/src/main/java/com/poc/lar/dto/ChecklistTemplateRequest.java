package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ChecklistTemplateRequest(
    @NotBlank(message = "Name is required")
    String name,
    @NotBlank(message = "Type is required")
    String type,
    List<ChecklistItemRequest> items
) {}
