package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChecklistResponseDTO(
    UUID id,
    UUID templateId,
    UUID outingId,
    UUID memberId,
    Boolean allPassed,
    LocalDateTime completedAt,
    List<ChecklistResponseItemDTO> items
) {}
