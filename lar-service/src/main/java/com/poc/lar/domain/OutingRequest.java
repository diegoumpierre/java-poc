package com.poc.lar.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("LAR_OUTING_REQUESTS")
public class OutingRequest implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("MEMBER_ID")
    private UUID memberId;

    @Column("EVENT_NAME")
    private String eventName;

    @Column("EVENT_DATE")
    private LocalDate eventDate;

    @Column("EVENT_TIME")
    private LocalTime eventTime;

    @Column("ADDRESS")
    private String address;

    @Column("ADDRESS_LAT")
    private BigDecimal addressLat;

    @Column("ADDRESS_LNG")
    private BigDecimal addressLng;

    @Column("LOCATION_CONTACT_NAME")
    private String locationContactName;

    @Column("LOCATION_CONTACT_PHONE")
    private String locationContactPhone;

    @Column("DEPARTURE_TIME")
    private LocalTime departureTime;

    @Column("RETURN_METHOD")
    private String returnMethod;

    @Column("RETURN_METHOD_DETAIL")
    private String returnMethodDetail;

    @Column("ESTIMATED_RETURN_TIME")
    private LocalTime estimatedReturnTime;

    @Column("COMPANIONS")
    private String companions;

    @Column("STATUS")
    private String status;

    @Column("APPROVED_BY")
    private UUID approvedBy;

    @Column("APPROVED_AT")
    private LocalDateTime approvedAt;

    @Column("REJECTION_REASON")
    private String rejectionReason;

    @Column("ACTUAL_DEPARTURE")
    private LocalDateTime actualDeparture;

    @Column("ACTUAL_RETURN")
    private LocalDateTime actualReturn;

    @Column("PARENT_NOTES")
    private String parentNotes;

    @Column("TEEN_NOTES")
    private String teenNotes;

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
