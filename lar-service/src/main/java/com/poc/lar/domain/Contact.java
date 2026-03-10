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
@Table("LAR_CONTACTS")
public class Contact implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("MEMBER_ID")
    private UUID memberId;

    @Column("NAME")
    private String name;

    @Column("PHONE")
    private String phone;

    @Column("RELATIONSHIP")
    private String relationship;

    @Column("AGE")
    private Integer age;

    @Column("WHERE_MET")
    private String whereMet;

    @Column("SCHOOL_NAME")
    private String schoolName;

    @Column("PARENT_NAME")
    private String parentName;

    @Column("PARENT_PHONE")
    private String parentPhone;

    @Column("PARENT2_NAME")
    private String parent2Name;

    @Column("PARENT2_PHONE")
    private String parent2Phone;

    @Column("ADDRESS")
    private String address;

    @Column("TRUSTED")
    private Boolean trusted;

    @Column("NOTES")
    private String notes;

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
