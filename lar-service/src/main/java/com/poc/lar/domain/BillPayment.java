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
@Table("LAR_BILL_PAYMENTS")
public class BillPayment implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("BILL_ID")
    private UUID billId;

    @Column("REFERENCE_MONTH")
    private LocalDate referenceMonth;

    @Column("AMOUNT_CENTS")
    private Integer amountCents;

    @Column("STATUS")
    private String status;

    @Column("PAID_AT")
    private LocalDateTime paidAt;

    @Column("PAID_BY")
    private UUID paidBy;

    @Column("RECEIPT_URL")
    private String receiptUrl;

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
