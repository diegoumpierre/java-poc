package com.poc.lar.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("LAR_FAMILY_MEMBERS")
public class FamilyMember implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("USER_ID")
    private UUID userId;

    @Column("NICKNAME")
    private String nickname;

    @Column("BIRTH_DATE")
    private LocalDate birthDate;

    @Column("BLOOD_TYPE")
    private String bloodType;

    @Column("ROLE_TYPE")
    private String roleType;

    @Column("SCHOOL_NAME")
    private String schoolName;

    @Column("SCHOOL_PHONE")
    private String schoolPhone;

    @Column("SCHOOL_GRADE")
    private String schoolGrade;

    @Column("HEALTH_INSURANCE")
    private String healthInsurance;

    @Column("HEALTH_INSURANCE_NUMBER")
    private String healthInsuranceNumber;

    @Column("ALLERGIES")
    private String allergies;

    @Column("MEDICAL_CONDITIONS")
    private String medicalConditions;

    @Column("EMERGENCY_NOTES")
    private String emergencyNotes;

    @Column("AVATAR_URL")
    private String avatarUrl;

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
