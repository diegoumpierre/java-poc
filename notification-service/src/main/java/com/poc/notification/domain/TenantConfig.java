package com.poc.notification.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("NOTF_TENANT_CONFIG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfig {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private String tenantId;

    @Column("CONFIG_TYPE")
    private String configType;

    @Column("SMTP_HOST")
    private String smtpHost;

    @Column("SMTP_PORT")
    @Builder.Default
    private Integer smtpPort = 587;

    @Column("SMTP_USERNAME")
    private String smtpUsername;

    @Column("SMTP_PASSWORD")
    private String smtpPassword;

    @Column("SMTP_USE_TLS")
    @Builder.Default
    private Boolean smtpUseTls = true;

    @Column("IMAP_HOST")
    private String imapHost;

    @Column("IMAP_PORT")
    @Builder.Default
    private Integer imapPort = 993;

    @Column("IMAP_USERNAME")
    private String imapUsername;

    @Column("IMAP_PASSWORD")
    private String imapPassword;

    @Column("IMAP_USE_TLS")
    @Builder.Default
    private Boolean imapUseTls = true;

    @Column("IMAP_FOLDER")
    @Builder.Default
    private String imapFolder = "INBOX";

    @Column("FROM_ADDRESS")
    private String fromAddress;

    @Column("FROM_NAME")
    private String fromName;

    @Column("REPLY_TO")
    private String replyTo;

    @Column("BASE_URL")
    @Builder.Default
    private String baseUrl = "http://localhost:3001";

    @Column("POLL_INTERVAL_SECONDS")
    @Builder.Default
    private Integer pollIntervalSeconds = 30;

    @Column("LAST_POLL_AT")
    private Instant lastPollAt;

    @Column("MAX_EMAILS_PER_MINUTE")
    @Builder.Default
    private Integer maxEmailsPerMinute = 10;

    @Column("MAX_EMAILS_PER_HOUR")
    @Builder.Default
    private Integer maxEmailsPerHour = 100;

    @Column("MAX_EMAILS_PER_DAY")
    @Builder.Default
    private Integer maxEmailsPerDay = 500;

    @Column("COOLDOWN_SECONDS")
    @Builder.Default
    private Integer cooldownSeconds = 5;

    @Column("ENABLED")
    @Builder.Default
    private Boolean enabled = false;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
