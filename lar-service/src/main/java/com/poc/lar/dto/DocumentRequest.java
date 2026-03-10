package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

public record DocumentRequest(
    UUID memberId,
    @NotBlank(message = "Title is required")
    String title,
    String description,
    @NotBlank(message = "Category is required")
    String category,
    String fileUrl,
    LocalDate expiryDate,
    Integer reminderDaysBefore
) {}
