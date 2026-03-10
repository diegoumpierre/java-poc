package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ChoreDTO(
    UUID id,
    String name,
    String description,
    String frequency,
    Integer dayOfWeek,
    Integer points,
    String assignmentType,
    UUID assignedTo,
    List<UUID> rotationMembers,
    Integer rotationIndex,
    String currentAssigneeName,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
