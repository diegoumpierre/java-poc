package com.poc.auth.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("AUTH_USER_SESSIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("USER_ID")
    private UUID userId;

    @Column("TOKEN_ID")
    private String tokenId;

    @Column("DEVICE_NAME")
    private String deviceName;

    @Column("DEVICE_TYPE")
    private String deviceType;

    @Column("BROWSER")
    private String browser;

    @Column("OPERATING_SYSTEM")
    private String operatingSystem;

    @Column("IP_ADDRESS")
    private String ipAddress;

    @Column("LOCATION")
    private String location;

    @Column("IS_CURRENT")
    @Builder.Default
    private Boolean isCurrent = false;

    @Column("LAST_ACTIVITY_AT")
    private Instant lastActivityAt;

    @Column("EXPIRES_AT")
    private Instant expiresAt;

    @Column("REVOKED")
    @Builder.Default
    private Boolean revoked = false;

    @Column("REVOKED_AT")
    private Instant revokedAt;

    @Column("REVOKED_REASON")
    private String revokedReason;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }

    public boolean isActive() {
        return !Boolean.TRUE.equals(revoked) &&
               (expiresAt == null || Instant.now().isBefore(expiresAt));
    }

    public void revoke(String reason) {
        this.revoked = true;
        this.revokedAt = Instant.now();
        this.revokedReason = reason;
    }

    public void updateActivity() {
        this.lastActivityAt = Instant.now();
    }

    /**
     * Device types for classification
     */
    public static class DeviceType {
        public static final String DESKTOP = "DESKTOP";
        public static final String MOBILE = "MOBILE";
        public static final String TABLET = "TABLET";
        public static final String UNKNOWN = "UNKNOWN";
    }
}
