package com.poc.tenant.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("TNT_CORE_SUPPLIERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("NAME")
    private String name;

    @Column("EMAIL")
    private String email;

    @Column("PHONE")
    private String phone;

    @Column("DOCUMENT")
    private String document;

    @Column("CATEGORY")
    private String category;

    @Column("ADDRESS")
    private String address;

    @Column("CITY")
    private String city;

    @Column("STATE")
    private String state;

    @Column("NOTES")
    private String notes;

    @Column("ACTIVE")
    @Builder.Default
    private Boolean active = true;

    @Column("CREATED_AT")
    @CreatedDate
    private Instant createdAt;

    @Column("UPDATED_AT")
    @LastModifiedDate
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
