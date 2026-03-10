package com.poc.lar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VaccinationDTO(
    UUID id,
    UUID memberId,
    String vaccineName,
    Integer doseNumber,
    LocalDate dateAdministered,
    String location,
    LocalDate nextDoseDate,
    String certificateUrl,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
