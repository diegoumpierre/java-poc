package com.poc.lar.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("LAR_EMERGENCY_CARDS")
public class EmergencyCard implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("MEMBER_ID")
    private UUID memberId;

    @Column("BLOOD_TYPE")
    private String bloodType;

    @Column("ALLERGIES")
    private String allergies;

    @Column("MEDICAL_CONDITIONS")
    private String medicalConditions;

    @Column("CURRENT_MEDICATIONS")
    private String currentMedications;

    @Column("EMERGENCY_CONTACT_1")
    private String emergencyContact1;

    @Column("EMERGENCY_PHONE_1")
    private String emergencyPhone1;

    @Column("EMERGENCY_CONTACT_2")
    private String emergencyContact2;

    @Column("EMERGENCY_PHONE_2")
    private String emergencyPhone2;

    @Column("DOCTOR_NAME")
    private String doctorName;

    @Column("DOCTOR_PHONE")
    private String doctorPhone;

    @Column("HEALTH_INSURANCE")
    private String healthInsurance;

    @Column("HEALTH_INSURANCE_NUMBER")
    private String healthInsuranceNumber;

    @Column("SPECIAL_INSTRUCTIONS")
    private String specialInstructions;

    @Column("PUBLIC_TOKEN")
    private UUID publicToken;

    @Column("ACTIVE")
    private Boolean active;

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
