package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChecklistTemplateDTO(
    UUID id,
    String name,
    String type,
    Boolean active,
    List<ChecklistItemDTO> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
