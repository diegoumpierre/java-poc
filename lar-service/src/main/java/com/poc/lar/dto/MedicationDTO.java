package com.poc.lar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MedicationDTO(
    UUID id,
    UUID memberId,
    String name,
    String dosage,
    String frequency,
    List<String> scheduleTimes,
    LocalDate startDate,
    LocalDate endDate,
    String prescribingDoctor,
    String notes,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
