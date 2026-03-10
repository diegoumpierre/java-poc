package com.poc.lar.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record HealthAppointmentRequest(
    UUID memberId,
    String doctorName,
    String specialty,
    String clinicName,
    String clinicPhone,
    String clinicAddress,
    @NotNull(message = "Appointment date is required")
    LocalDate appointmentDate,
    LocalTime appointmentTime,
    String notes,
    String prescriptionUrl,
    LocalDate followUpDate,
    String followUpNotes
) {}
