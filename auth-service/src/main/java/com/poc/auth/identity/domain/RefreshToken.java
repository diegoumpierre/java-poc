package com.poc.auth.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("AUTH_REFRESH_TOKENS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken implements Persistable<UUID> {

    @Id
    @Column("id")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("token")
    private String token;

    @Column("user_id")
    private UUID userId;

    @Column("expiry_date")
    private Instant expiryDate;

    @Column("created_at")
    private Instant createdAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
