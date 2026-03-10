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
@Table("LAR_CHORES")
public class Chore implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("NAME")
    private String name;

    @Column("DESCRIPTION")
    private String description;

    @Column("FREQUENCY")
    private String frequency;

    @Column("DAY_OF_WEEK")
    private Integer dayOfWeek;

    @Column("POINTS")
    private Integer points;

    @Column("ASSIGNMENT_TYPE")
    private String assignmentType;

    @Column("ASSIGNED_TO")
    private UUID assignedTo;

    @Column("ROTATION_MEMBERS")
    private String rotationMembers;

    @Column("ROTATION_INDEX")
    private Integer rotationIndex;

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
