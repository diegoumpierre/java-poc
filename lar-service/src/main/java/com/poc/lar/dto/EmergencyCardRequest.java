package com.poc.lar.dto;

import java.util.List;
import java.util.UUID;

public record EmergencyCardRequest(
    UUID memberId,
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
    String specialInstructions
) {}
