package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("CHAT_MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @Column("ID")
    private Long id;

    @Column("CONVERSATION_ID")
    private Long conversationId;

    @Column("CHANNEL_ID")
    private Long channelId;

    @Column("SENDER_ID")
    private Long senderId;

    @Column("TEXT")
    private String text;

    @Column("MESSAGE_TYPE")
    private String messageType;

    @Column("PARENT_MESSAGE_ID")
    private Long parentMessageId;

    @Column("REPLY_COUNT")
    private Integer replyCount;

    @Column("LAST_REPLY_AT")
    private Instant lastReplyAt;

    @Column("EDITED_AT")
    private Instant editedAt;

    @Column("READ_AT")
    private Instant readAt;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    public boolean isRead() {
        return readAt != null;
    }
}
