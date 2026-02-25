package com.poc.notification.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("NOTF_INBOUND_MESSAGES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundMessage {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private String tenantId;

    @Column("CONVERSATION_ID")
    private Long conversationId;

    @Column("FROM_EMAIL")
    private String fromEmail;

    @Column("FROM_NAME")
    private String fromName;

    @Column("SUBJECT")
    private String subject;

    @Column("CONTENT_TEXT")
    private String contentText;

    @Column("CONTENT_HTML")
    private String contentHtml;

    @Column("PROVIDER_MESSAGE_ID")
    private String providerMessageId;

    @Column("IN_REPLY_TO")
    private String inReplyTo;

    @Column("REFERENCES_HEADER")
    private String referencesHeader;

    @Column("HAS_ATTACHMENTS")
    @Builder.Default
    private Boolean hasAttachments = false;

    @Column("IS_READ")
    @Builder.Default
    private Boolean isRead = false;

    @Column("RECEIVED_AT")
    private Instant receivedAt;

    @Column("CREATED_AT")
    private Instant createdAt;
}
