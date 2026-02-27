package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("CHAT_USER_SETTINGS")
public class ChatUserSettings {
    @Id
    private Long id;
    private Long userId;
    private Boolean notifyDm;
    private Boolean notifyMention;
    private Boolean notifyChannelMessages;
    private Boolean notifySound;
    private Boolean notifyDesktop;
    private Instant createdAt;
    private Instant updatedAt;
}
