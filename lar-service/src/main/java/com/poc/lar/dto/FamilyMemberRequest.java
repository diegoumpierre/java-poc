package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record FamilyMemberRequest(
    String nickname,
    LocalDate birthDate,
    String bloodType,
    @NotBlank(message = "Role type is required")
    String roleType,
    String schoolName,
    String schoolPhone,
    String schoolGrade,
    String healthInsurance,
    String healthInsuranceNumber,
    List<String> allergies,
    List<String> medicalConditions,
    String emergencyNotes,
    String avatarUrl
) {}
