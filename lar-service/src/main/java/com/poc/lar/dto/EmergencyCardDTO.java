package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EmergencyCardDTO(
    UUID id,
    UUID memberId,
    String memberNickname,
    String bloodType,
    List<String> allergies,
    List<String> medicalConditions,
    List<String> currentMedications,
    String emergencyContact1,
    String emergencyPhone1,
    String emergencyContact2,
    String emergencyPhone2,
    String doctorName,
    String doctorPhone,
    String healthInsurance,
    String healthInsuranceNumber,
    String specialInstructions,
    UUID publicToken,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
