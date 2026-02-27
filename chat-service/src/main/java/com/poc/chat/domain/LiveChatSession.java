package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("CHAT_LIVECHAT_SESSION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveChatSession {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("SESSION_TOKEN")
    private String sessionToken;

    @Column("VISITOR_ID")
    private Long visitorId;

    @Column("ASSIGNED_AGENT_ID")
    private Long assignedAgentId;

    @Column("QUEUE_ID")
    private String queueId;

    @Column("SOURCE_SERVICE")
    @Builder.Default
    private String sourceService = "HELPDESK";

    @Column("STATUS")
    @Builder.Default
    private String status = "WAITING";

    @Column("PAGE_URL")
    private String pageUrl;

    @Column("VISITOR_IP")
    private String visitorIp;

    @Column("USER_AGENT")
    private String userAgent;

    @Column("MESSAGE_COUNT")
    @Builder.Default
    private Integer messageCount = 0;

    @Column("RATING")
    private Integer rating;

    @Column("FEEDBACK")
    private String feedback;

    @Column("AGENT_JOINED_AT")
    private Instant agentJoinedAt;

    @Column("CLOSED_AT")
    private Instant closedAt;

    @Column("LAST_ACTIVITY_AT")
    private Instant lastActivityAt;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("EXTERNAL_CONTACT_PHONE")
    private String externalContactPhone;

    @Column("EXTERNAL_CONTACT_EMAIL")
    private String externalContactEmail;

    @Column("EXTERNAL_CONTACT_NAME")
    private String externalContactName;

    @Column("EXTERNAL_CHANNEL")
    @Builder.Default
    private String externalChannel = "LIVECHAT";

    @Column("EXTERNAL_CONVERSATION_ID")
    private Long externalConversationId;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    public LiveChatSessionStatus getStatusEnum() {
        return status != null ? LiveChatSessionStatus.valueOf(status) : null;
    }

    public void setStatusEnum(LiveChatSessionStatus sessionStatus) {
        this.status = sessionStatus != null ? sessionStatus.name() : null;
    }
}
