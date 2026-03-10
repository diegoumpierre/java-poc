package com.poc.lar.controller;

import com.poc.lar.dto.HealthAppointmentDTO;
import com.poc.lar.dto.HealthAppointmentRequest;
import com.poc.lar.dto.MedicationDTO;
import com.poc.lar.dto.MedicationRequest;
import com.poc.lar.dto.VaccinationDTO;
import com.poc.lar.dto.VaccinationRequest;
import com.poc.lar.service.HealthService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService service;

    // --- Appointments ---

    @GetMapping("/appointments")
    public ResponseEntity<List<HealthAppointmentDTO>> findAllAppointments() {
        return ResponseEntity.ok(service.findAllAppointments());
    }

    @GetMapping("/appointments/member/{id}")
    public ResponseEntity<List<HealthAppointmentDTO>> findAppointmentsByMember(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findAppointmentsByMember(id));
    }

    @PostMapping("/appointments")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<HealthAppointmentDTO> createAppointment(@Valid @RequestBody HealthAppointmentRequest request) {
        HealthAppointmentDTO created = service.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/appointments/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<HealthAppointmentDTO> updateAppointment(@PathVariable UUID id, @Valid @RequestBody HealthAppointmentRequest request) {
        return ResponseEntity.ok(service.updateAppointment(id, request));
    }

    @GetMapping("/appointments/upcoming")
    public ResponseEntity<List<HealthAppointmentDTO>> findUpcoming() {
        return ResponseEntity.ok(service.findUpcomingAppointments());
    }

    // --- Medications ---

    @GetMapping("/medications/member/{id}")
    public ResponseEntity<List<MedicationDTO>> findMedicationsByMember(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findMedicationsByMember(id));
    }

    @PostMapping("/medications")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<MedicationDTO> createMedication(@Valid @RequestBody MedicationRequest request) {
        MedicationDTO created = service.createMedication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/medications/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<MedicationDTO> updateMedication(@PathVariable UUID id, @Valid @RequestBody MedicationRequest request) {
        return ResponseEntity.ok(service.updateMedication(id, request));
    }

    @GetMapping("/medications/active")
    public ResponseEntity<List<MedicationDTO>> findActiveMedications() {
        return ResponseEntity.ok(service.findActiveMedications());
    }

    // --- Vaccinations ---

    @GetMapping("/vaccinations/member/{id}")
    public ResponseEntity<List<VaccinationDTO>> findVaccinationsByMember(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findVaccinationsByMember(id));
    }

    @PostMapping("/vaccinations")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<VaccinationDTO> createVaccination(@Valid @RequestBody VaccinationRequest request) {
        VaccinationDTO created = service.createVaccination(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
