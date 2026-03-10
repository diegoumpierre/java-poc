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
@Table("LAR_CHORE_LOG")
public class ChoreLog implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("CHORE_ID")
    private UUID choreId;

    @Column("MEMBER_ID")
    private UUID memberId;

    @Column("COMPLETED_AT")
    private LocalDateTime completedAt;

    @Column("VERIFIED_BY")
    private UUID verifiedBy;

    @Column("VERIFIED_AT")
    private LocalDateTime verifiedAt;

    @Column("PHOTO_URL")
    private String photoUrl;

    @Column("POINTS_EARNED")
    private Integer pointsEarned;

    @Column("NOTE")
    private String note;

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
