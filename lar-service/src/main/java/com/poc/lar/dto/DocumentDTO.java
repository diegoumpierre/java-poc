package com.poc.lar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentDTO(
    UUID id,
    UUID memberId,
    String memberNickname,
    String title,
    String description,
    String category,
    String fileUrl,
    LocalDate expiryDate,
    Integer reminderDaysBefore,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
