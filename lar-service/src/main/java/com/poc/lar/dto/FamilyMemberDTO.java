package com.poc.lar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FamilyMemberDTO(
    UUID id,
    UUID userId,
    String nickname,
    LocalDate birthDate,
    String bloodType,
    String roleType,
    String schoolName,
    String schoolPhone,
    String schoolGrade,
    String healthInsurance,
    String healthInsuranceNumber,
    List<String> allergies,
    List<String> medicalConditions,
    String emergencyNotes,
    String avatarUrl,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
