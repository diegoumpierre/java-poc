package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("CHAT_MENTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMention {

    @Id
    @Column("ID")
    private Long id;

    @Column("MESSAGE_ID")
    private Long messageId;

    @Column("MENTIONED_USER_ID")
    private Long mentionedUserId;

    @Column("MENTION_TYPE")
    private String mentionType;

    @Column("CREATED_AT")
    private Instant createdAt;
}
