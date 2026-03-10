package com.poc.lar.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("LAR_HEALTH_APPOINTMENTS")
public class HealthAppointment implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("MEMBER_ID")
    private UUID memberId;

    @Column("DOCTOR_NAME")
    private String doctorName;

    @Column("SPECIALTY")
    private String specialty;

    @Column("CLINIC_NAME")
    private String clinicName;

    @Column("CLINIC_PHONE")
    private String clinicPhone;

    @Column("CLINIC_ADDRESS")
    private String clinicAddress;

    @Column("APPOINTMENT_DATE")
    private LocalDate appointmentDate;

    @Column("APPOINTMENT_TIME")
    private LocalTime appointmentTime;

    @Column("STATUS")
    private String status;

    @Column("NOTES")
    private String notes;

    @Column("PRESCRIPTION_URL")
    private String prescriptionUrl;

    @Column("FOLLOW_UP_DATE")
    private LocalDate followUpDate;

    @Column("FOLLOW_UP_NOTES")
    private String followUpNotes;

    @Column("CREATED_AT")
    private LocalDateTime createdAt;

    @Column("UPDATED_AT")
    private LocalDateTime updatedAt;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markAsExisting() {
        this.isNew = false;
    }
}
