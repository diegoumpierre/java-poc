package com.poc.kanban.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEngineCardRequest(
        @NotBlank(message = "Card title is required")
        @Size(min = 1, max = 255, message = "Card title must be between 1 and 255 characters")
        String title,

        @Size(max = 5000, message = "Description must be at most 5000 characters")
        String description,

        String priorityColor,
        String priorityTitle,
        LocalDate startDate,
        LocalDate dueDate,
        String targetStepCode,
        UUID targetListId,
        String sourceService
) {}
