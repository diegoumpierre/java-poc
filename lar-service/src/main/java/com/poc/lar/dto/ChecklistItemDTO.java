package com.poc.lar.dto;

import java.util.UUID;

public record ChecklistItemDTO(
    UUID id,
    String description,
    Integer orderIndex,
    Boolean required,
    Boolean requiresPhoto
) {}
