package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("CHAT_TENANT_SETTINGS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatTenantSettings {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("MESSAGE_RETENTION_DAYS")
    @Builder.Default
    private Integer messageRetentionDays = 365;

    @Column("MAX_FILE_SIZE_MB")
    @Builder.Default
    private Integer maxFileSizeMb = 50;

    @Column("ALLOW_PUBLIC_CHANNELS")
    @Builder.Default
    private Boolean allowPublicChannels = true;

    @Column("ALLOW_FILE_UPLOADS")
    @Builder.Default
    private Boolean allowFileUploads = true;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
