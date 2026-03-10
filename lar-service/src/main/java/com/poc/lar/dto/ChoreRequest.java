package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record ChoreRequest(
    @NotBlank(message = "Name is required")
    String name,
    String description,
    @NotBlank(message = "Frequency is required")
    String frequency,
    Integer dayOfWeek,
    Integer points,
    String assignmentType,
    UUID assignedTo,
    List<UUID> rotationMembers
) {}
