package com.poc.auth.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("AUTH_TWO_FACTOR_CODES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorCode implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("USER_ID")
    private UUID userId;

    @Column("CODE")
    private String code;

    @Column("EXPIRES_AT")
    private Instant expiresAt;

    @Column("USED")
    @Builder.Default
    private Boolean used = false;

    @Column("IP_ADDRESS")
    private String ipAddress;

    @Column("USER_AGENT")
    private String userAgent;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !Boolean.TRUE.equals(used) && !isExpired();
    }
}
