package com.poc.lar.service;

import com.poc.lar.converter.HealthConverter;
import com.poc.lar.domain.HealthAppointment;
import com.poc.lar.domain.Medication;
import com.poc.lar.domain.Vaccination;
import com.poc.lar.dto.HealthAppointmentDTO;
import com.poc.lar.dto.HealthAppointmentRequest;
import com.poc.lar.dto.MedicationDTO;
import com.poc.lar.dto.MedicationRequest;
import com.poc.lar.dto.VaccinationDTO;
import com.poc.lar.dto.VaccinationRequest;
import com.poc.lar.repository.HealthAppointmentRepository;
import com.poc.lar.repository.MedicationRepository;
import com.poc.lar.repository.VaccinationRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthService {

    private final HealthAppointmentRepository healthAppointmentRepository;
    private final MedicationRepository medicationRepository;
    private final VaccinationRepository vaccinationRepository;
    private final HealthConverter healthConverter;

    // ========== Appointments ==========

    public List<HealthAppointmentDTO> findAllAppointments() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all health appointments for tenant: {}", tenantId);
        return healthConverter.toAppointmentDTOList(healthAppointmentRepository.findByTenantId(tenantId));
    }

    public List<HealthAppointmentDTO> findAppointmentsByMember(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding appointments for member: {} in tenant: {}", memberId, tenantId);
        return healthConverter.toAppointmentDTOList(healthAppointmentRepository.findByMemberId(tenantId, memberId));
    }

    public List<HealthAppointmentDTO> findUpcomingAppointments() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding upcoming appointments for tenant: {}", tenantId);
        return healthConverter.toAppointmentDTOList(healthAppointmentRepository.findUpcoming(tenantId));
    }

    public HealthAppointmentDTO createAppointment(HealthAppointmentRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating health appointment for tenant: {}", tenantId);

        HealthAppointment appointment = HealthAppointment.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .memberId(req.memberId())
                .doctorName(req.doctorName())
                .specialty(req.specialty())
                .clinicName(req.clinicName())
                .clinicPhone(req.clinicPhone())
                .clinicAddress(req.clinicAddress())
                .appointmentDate(req.appointmentDate())
                .appointmentTime(req.appointmentTime())
                .status("SCHEDULED")
                .notes(req.notes())
                .prescriptionUrl(req.prescriptionUrl())
                .followUpDate(req.followUpDate())
                .followUpNotes(req.followUpNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return healthConverter.toDTO(healthAppointmentRepository.save(appointment));
    }

    public HealthAppointmentDTO updateAppointment(UUID id, HealthAppointmentRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating health appointment: {} for tenant: {}", id, tenantId);

        HealthAppointment appointment = healthAppointmentRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Health appointment not found: " + id));

        appointment.setMemberId(req.memberId());
        appointment.setDoctorName(req.doctorName());
        appointment.setSpecialty(req.specialty());
        appointment.setClinicName(req.clinicName());
        appointment.setClinicPhone(req.clinicPhone());
        appointment.setClinicAddress(req.clinicAddress());
        appointment.setAppointmentDate(req.appointmentDate());
        appointment.setAppointmentTime(req.appointmentTime());
        appointment.setNotes(req.notes());
        appointment.setPrescriptionUrl(req.prescriptionUrl());
        appointment.setFollowUpDate(req.followUpDate());
        appointment.setFollowUpNotes(req.followUpNotes());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.markAsExisting();

        return healthConverter.toDTO(healthAppointmentRepository.save(appointment));
    }

    // ========== Medications ==========

    public List<MedicationDTO> findMedicationsByMember(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding medications for member: {} in tenant: {}", memberId, tenantId);
        return healthConverter.toMedicationDTOList(medicationRepository.findByMemberId(tenantId, memberId));
    }

    public List<MedicationDTO> findActiveMedications() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding active medications for tenant: {}", tenantId);
        return healthConverter.toMedicationDTOList(medicationRepository.findActive(tenantId));
    }

    public MedicationDTO createMedication(MedicationRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating medication for tenant: {}", tenantId);

        Medication medication = Medication.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .memberId(req.memberId())
                .name(req.name())
                .dosage(req.dosage())
                .frequency(req.frequency())
                .scheduleTimes(healthConverter.serializeList(req.scheduleTimes()))
                .startDate(req.startDate())
                .endDate(req.endDate())
                .prescribingDoctor(req.prescribingDoctor())
                .notes(req.notes())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return healthConverter.toDTO(medicationRepository.save(medication));
    }

    public MedicationDTO updateMedication(UUID id, MedicationRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating medication: {} for tenant: {}", id, tenantId);

        Medication medication = medicationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Medication not found: " + id));

        medication.setMemberId(req.memberId());
        medication.setName(req.name());
        medication.setDosage(req.dosage());
        medication.setFrequency(req.frequency());
        medication.setScheduleTimes(healthConverter.serializeList(req.scheduleTimes()));
        medication.setStartDate(req.startDate());
        medication.setEndDate(req.endDate());
        medication.setPrescribingDoctor(req.prescribingDoctor());
        medication.setNotes(req.notes());
        medication.setUpdatedAt(LocalDateTime.now());
        medication.markAsExisting();

        return healthConverter.toDTO(medicationRepository.save(medication));
    }

    // ========== Vaccinations ==========

    public List<VaccinationDTO> findVaccinationsByMember(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding vaccinations for member: {} in tenant: {}", memberId, tenantId);
        return healthConverter.toVaccinationDTOList(vaccinationRepository.findByMemberId(tenantId, memberId));
    }

    public VaccinationDTO createVaccination(VaccinationRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating vaccination record for tenant: {}", tenantId);

        Vaccination vaccination = Vaccination.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .memberId(req.memberId())
                .vaccineName(req.vaccineName())
                .doseNumber(req.doseNumber())
                .dateAdministered(req.dateAdministered())
                .location(req.location())
                .nextDoseDate(req.nextDoseDate())
                .certificateUrl(req.certificateUrl())
                .notes(req.notes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return healthConverter.toDTO(vaccinationRepository.save(vaccination));
    }
}
