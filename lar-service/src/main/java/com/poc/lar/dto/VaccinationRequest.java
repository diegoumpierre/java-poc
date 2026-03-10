package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record VaccinationRequest(
    UUID memberId,
    @NotBlank(message = "Vaccine name is required")
    String vaccineName,
    Integer doseNumber,
    @NotNull(message = "Date administered is required")
    LocalDate dateAdministered,
    String location,
    LocalDate nextDoseDate,
    String certificateUrl,
    String notes
) {}
