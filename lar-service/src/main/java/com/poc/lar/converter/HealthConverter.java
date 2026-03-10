package com.poc.lar.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.lar.domain.HealthAppointment;
import com.poc.lar.domain.Medication;
import com.poc.lar.domain.Vaccination;
import com.poc.lar.dto.HealthAppointmentDTO;
import com.poc.lar.dto.MedicationDTO;
import com.poc.lar.dto.VaccinationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthConverter {

    private final ObjectMapper objectMapper;

    // --- HealthAppointment ---

    public HealthAppointmentDTO toDTO(HealthAppointment appointment, String memberNickname) {
        return new HealthAppointmentDTO(
            appointment.getId(),
            appointment.getMemberId(),
            memberNickname,
            appointment.getDoctorName(),
            appointment.getSpecialty(),
            appointment.getClinicName(),
            appointment.getClinicPhone(),
            appointment.getClinicAddress(),
            appointment.getAppointmentDate(),
            appointment.getAppointmentTime(),
            appointment.getStatus(),
            appointment.getNotes(),
            appointment.getPrescriptionUrl(),
            appointment.getFollowUpDate(),
            appointment.getFollowUpNotes(),
            appointment.getCreatedAt(),
            appointment.getUpdatedAt()
        );
    }

    public HealthAppointmentDTO toDTO(HealthAppointment appointment) {
        return toDTO(appointment, null);
    }

    public List<HealthAppointmentDTO> toAppointmentDTOList(List<HealthAppointment> appointments) {
        return appointments.stream().map(this::toDTO).toList();
    }

    // --- Medication ---

    public MedicationDTO toDTO(Medication medication) {
        return new MedicationDTO(
            medication.getId(),
            medication.getMemberId(),
            medication.getName(),
            medication.getDosage(),
            medication.getFrequency(),
            parseJsonList(medication.getScheduleTimes()),
            medication.getStartDate(),
            medication.getEndDate(),
            medication.getPrescribingDoctor(),
            medication.getNotes(),
            medication.getActive(),
            medication.getCreatedAt(),
            medication.getUpdatedAt()
        );
    }

    public List<MedicationDTO> toMedicationDTOList(List<Medication> medications) {
        return medications.stream().map(this::toDTO).toList();
    }

    public String serializeList(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.warn("Failed to serialize list: {}", list);
            return null;
        }
    }

    // --- Vaccination ---

    public VaccinationDTO toDTO(Vaccination vaccination) {
        return new VaccinationDTO(
            vaccination.getId(),
            vaccination.getMemberId(),
            vaccination.getVaccineName(),
            vaccination.getDoseNumber(),
            vaccination.getDateAdministered(),
            vaccination.getLocation(),
            vaccination.getNextDoseDate(),
            vaccination.getCertificateUrl(),
            vaccination.getNotes(),
            vaccination.getCreatedAt(),
            vaccination.getUpdatedAt()
        );
    }

    public List<VaccinationDTO> toVaccinationDTOList(List<Vaccination> vaccinations) {
        return vaccinations.stream().map(this::toDTO).toList();
    }

    // --- JSON helpers ---

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse JSON list: {}", json);
            return Collections.emptyList();
        }
    }
}
