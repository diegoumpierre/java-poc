package com.poc.notification.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("NOTF_CONVERSATIONS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private String tenantId;

    @Column("CONTACT_EMAIL")
    private String contactEmail;

    @Column("CONTACT_NAME")
    private String contactName;

    @Column("SUBJECT")
    private String subject;

    @Column("THREAD_ID")
    private String threadId;

    @Column("LAST_MESSAGE_AT")
    private Instant lastMessageAt;

    @Column("LAST_MESSAGE_PREVIEW")
    private String lastMessagePreview;

    @Column("UNREAD_COUNT")
    @Builder.Default
    private Integer unreadCount = 0;

    @Column("STATUS")
    @Builder.Default
    private String status = "ACTIVE";

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
