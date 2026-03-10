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
@Table("LAR_REWARD_REDEMPTIONS")
public class RewardRedemption implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("REWARD_ID")
    private UUID rewardId;

    @Column("MEMBER_ID")
    private UUID memberId;

    @Column("POINTS_SPENT")
    private Integer pointsSpent;

    @Column("STATUS")
    private String status;

    @Column("APPROVED_BY")
    private UUID approvedBy;

    @Column("REDEEMED_AT")
    private LocalDateTime redeemedAt;

    @Column("CREATED_AT")
    private LocalDateTime createdAt;

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
