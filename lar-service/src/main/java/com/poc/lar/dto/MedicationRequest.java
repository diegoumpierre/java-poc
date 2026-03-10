package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record MedicationRequest(
    UUID memberId,
    @NotBlank(message = "Medication name is required")
    String name,
    String dosage,
    String frequency,
    List<String> scheduleTimes,
    LocalDate startDate,
    LocalDate endDate,
    String prescribingDoctor,
    String notes
) {}
