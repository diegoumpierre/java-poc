package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("CHAT_LIVECHAT_MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveChatMessage {

    @Id
    @Column("ID")
    private Long id;

    @Column("SESSION_ID")
    private Long sessionId;

    @Column("SENDER_TYPE")
    private String senderType;

    @Column("SENDER_ID")
    private Long senderId;

    @Column("CONTENT")
    private String content;

    @Column("MESSAGE_TYPE")
    @Builder.Default
    private String messageType = "TEXT";

    @Column("ATTACHMENT_URL")
    private String attachmentUrl;

    @Column("ATTACHMENT_NAME")
    private String attachmentName;

    @Column("IS_READ")
    @Builder.Default
    private Boolean isRead = false;

    @Column("READ_AT")
    private Instant readAt;

    @Column("CREATED_AT")
    private Instant createdAt;

    public LiveChatSenderType getSenderTypeEnum() {
        return senderType != null ? LiveChatSenderType.valueOf(senderType) : null;
    }

    public void setSenderTypeEnum(LiveChatSenderType type) {
        this.senderType = type != null ? type.name() : null;
    }
}
