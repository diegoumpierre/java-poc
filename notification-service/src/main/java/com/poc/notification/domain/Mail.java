package com.poc.notification.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("NOTF_MAILS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mail implements Persistable<UUID> {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column("USER_ID")
    private UUID userId;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("SENDER_NAME")
    private String senderName;

    @Column("SENDER_EMAIL")
    private String senderEmail;

    @Column("SENDER_IMAGE")
    private String senderImage;

    @Column("TO_NAME")
    private String toName;

    @Column("TO_EMAIL")
    private String toEmail;

    @Column("TITLE")
    private String title;

    @Column("MESSAGE")
    private String message;

    @Column("DATE")
    private String date;

    @Column("IMPORTANT")
    @Builder.Default
    private Boolean important = false;

    @Column("STARRED")
    @Builder.Default
    private Boolean starred = false;

    @Column("TRASH")
    @Builder.Default
    private Boolean trash = false;

    @Column("SPAM")
    @Builder.Default
    private Boolean spam = false;

    @Column("ARCHIVED")
    @Builder.Default
    private Boolean archived = false;

    @Column("SENT")
    @Builder.Default
    private Boolean sent = false;

    @Column("READ_STATUS")
    @Builder.Default
    private Boolean readStatus = false;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
