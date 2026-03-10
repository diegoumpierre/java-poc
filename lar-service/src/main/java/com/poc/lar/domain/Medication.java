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
@Table("LAR_MEDICATIONS")
public class Medication implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("MEMBER_ID")
    private UUID memberId;

    @Column("NAME")
    private String name;

    @Column("DOSAGE")
    private String dosage;

    @Column("FREQUENCY")
    private String frequency;

    @Column("SCHEDULE_TIMES")
    private String scheduleTimes;

    @Column("START_DATE")
    private LocalDate startDate;

    @Column("END_DATE")
    private LocalDate endDate;

    @Column("PRESCRIBING_DOCTOR")
    private String prescribingDoctor;

    @Column("NOTES")
    private String notes;

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
