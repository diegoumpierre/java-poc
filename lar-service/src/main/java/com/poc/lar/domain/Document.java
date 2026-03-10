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
@Table("LAR_DOCUMENTS")
public class Document implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("MEMBER_ID")
    private UUID memberId;

    @Column("TITLE")
    private String title;

    @Column("DESCRIPTION")
    private String description;

    @Column("CATEGORY")
    private String category;

    @Column("FILE_URL")
    private String fileUrl;

    @Column("EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column("REMINDER_DAYS_BEFORE")
    private Integer reminderDaysBefore;

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
