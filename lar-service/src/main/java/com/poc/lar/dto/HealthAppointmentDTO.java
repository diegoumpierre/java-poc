package com.poc.lar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record HealthAppointmentDTO(
    UUID id,
    UUID memberId,
    String memberNickname,
    String doctorName,
    String specialty,
    String clinicName,
    String clinicPhone,
    String clinicAddress,
    LocalDate appointmentDate,
    LocalTime appointmentTime,
    String status,
    String notes,
    String prescriptionUrl,
    LocalDate followUpDate,
    String followUpNotes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
