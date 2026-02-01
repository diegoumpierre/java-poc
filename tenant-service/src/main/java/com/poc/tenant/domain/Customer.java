package com.poc.tenant.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Table("TNT_CORE_CUSTOMERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("USER_ID")
    private UUID userId;

    @Column("NAME")
    private String name;

    @Column("EMAIL")
    private String email;

    @Column("COMPANY")
    private String company;

    @Column("COUNTRY_NAME")
    private String countryName;

    @Column("COUNTRY_CODE")
    private String countryCode;

    @Column("CITY")
    private String city;

    @Column("STATE")
    private String state;

    @Column("WEBSITE")
    private String website;

    @Column("BIO")
    private String bio;

    @Column("AVATAR")
    private String avatar;

    @Column("NICKNAME")
    private String nickname;

    @Column("JOIN_DATE")
    private LocalDate joinDate;

    @Column("STATUS")
    @Builder.Default
    private String status = "new";

    @Column("ACTIVITY")
    @Builder.Default
    private Integer activity = 0;

    @Column("BALANCE")
    private BigDecimal balance;

    @Column("VERIFIED")
    @Builder.Default
    private Boolean verified = false;

    @Column("AMOUNT")
    private BigDecimal amount;

    @Column("PRICE")
    private BigDecimal price;

    @Column("RATING")
    private Integer rating;

    @Column("IMAGE")
    private String image;

    @Column("REPRESENTATIVE_NAME")
    private String representativeName;

    @Column("REPRESENTATIVE_IMAGE")
    private String representativeImage;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("EXTERNAL_ID")
    private String externalId;

    @Column("KIND")
    private String kind;

    @Column("METADATA")
    private String metadata;

    @Column("PHONE")
    private String phone;

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
