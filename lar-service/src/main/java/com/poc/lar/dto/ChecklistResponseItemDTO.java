package com.poc.lar.dto;

import java.util.UUID;

public record ChecklistResponseItemDTO(
    UUID id,
    UUID itemId,
    String description,
    Boolean checked,
    String photoUrl,
    String note
) {}
