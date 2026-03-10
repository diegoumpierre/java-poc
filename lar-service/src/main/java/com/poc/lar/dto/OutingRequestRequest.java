package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record OutingRequestRequest(
    @NotBlank(message = "Event name is required")
    String eventName,
    @NotNull(message = "Event date is required")
    LocalDate eventDate,
    LocalTime eventTime,
    String address,
    BigDecimal addressLat,
    BigDecimal addressLng,
    String locationContactName,
    String locationContactPhone,
    @NotNull(message = "Departure time is required")
    LocalTime departureTime,
    @NotBlank(message = "Return method is required")
    String returnMethod,
    String returnMethodDetail,
    @NotNull(message = "Estimated return time is required")
    LocalTime estimatedReturnTime,
    List<CompanionDTO> companions,
    String teenNotes
) {}
