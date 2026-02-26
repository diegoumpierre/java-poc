package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("CHAT_REACTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatReaction {

    @Id
    @Column("ID")
    private Long id;

    @Column("MESSAGE_ID")
    private Long messageId;

    @Column("USER_ID")
    private Long userId;

    @Column("EMOJI")
    private String emoji;

    @Column("CREATED_AT")
    private Instant createdAt;
}
