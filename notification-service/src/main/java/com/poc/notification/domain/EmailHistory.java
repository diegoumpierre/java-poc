package com.poc.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("NOTF_EMAIL_HISTORY")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailHistory {

    @Id
    @Column("ID")
    private Long id;

    @Column("MESSAGE_ID")
    private String messageId;

    @Column("USER_ID")
    private String userId;

    @Column("TENANT_ID")
    private String tenantId;

    @Column("CONFIG_TYPE")
    @Builder.Default
    private String configType = "NOTIFICATION";

    @Column("CONVERSATION_ID")
    private Long conversationId;

    @Column("RECIPIENT")
    private String recipient;

    @Column("SUBJECT")
    private String subject;

    @Column("TEMPLATE")
    private String template;

    @Column("VARIABLES")
    private String variables;

    @Column("CONTENT_TEXT")
    private String contentText;

    @Column("CONTENT_HTML")
    private String contentHtml;

    @Column("IN_REPLY_TO")
    private String inReplyTo;

    @Column("REFERENCES_HEADER")
    private String referencesHeader;

    @Column("STATUS")
    private String status;

    @Column("RETRY_COUNT")
    @Builder.Default
    private Integer retryCount = 0;

    @Column("ERROR_MESSAGE")
    private String errorMessage;

    @Column("PROVIDER_MESSAGE_ID")
    private String providerMessageId;

    @Column("SCHEDULED_AT")
    private Instant scheduledAt;

    @Column("SENT_AT")
    private Instant sentAt;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
