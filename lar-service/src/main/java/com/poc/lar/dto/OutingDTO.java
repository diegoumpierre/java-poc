package com.poc.lar.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record OutingDTO(
    UUID id,
    UUID memberId,
    String memberNickname,
    String eventName,
    LocalDate eventDate,
    LocalTime eventTime,
    String address,
    BigDecimal addressLat,
    BigDecimal addressLng,
    String locationContactName,
    String locationContactPhone,
    LocalTime departureTime,
    String returnMethod,
    String returnMethodDetail,
    LocalTime estimatedReturnTime,
    List<CompanionDTO> companions,
    String status,
    UUID approvedBy,
    LocalDateTime approvedAt,
    String rejectionReason,
    LocalDateTime actualDeparture,
    LocalDateTime actualReturn,
    String parentNotes,
    String teenNotes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
