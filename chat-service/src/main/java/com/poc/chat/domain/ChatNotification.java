package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("CHAT_NOTIFICATION")
public class ChatNotification {
    @Id
    private Long id;
    private UUID tenantId;
    private Long userId;
    private String type;
    private Long channelId;
    private Long messageId;
    private Long senderId;
    private String title;
    private String body;
    private Boolean isRead;
    private Instant createdAt;
}
