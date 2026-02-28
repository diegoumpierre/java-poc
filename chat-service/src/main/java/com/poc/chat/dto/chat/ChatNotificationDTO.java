package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatNotification;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotificationDTO {
    private Long id;
    private String type;
    private Long channelId;
    private Long messageId;
    private String title;
    private String body;
    private Boolean isRead;
    private Instant createdAt;
    private ChatUserDTO sender;
    private String channelName;

    public static ChatNotificationDTO fromEntity(ChatNotification entity, ChatUserDTO sender, String channelName) {
        return ChatNotificationDTO.builder()
                .id(entity.getId())
                .type(entity.getType())
                .channelId(entity.getChannelId())
                .messageId(entity.getMessageId())
                .title(entity.getTitle())
                .body(entity.getBody())
                .isRead(entity.getIsRead())
                .createdAt(entity.getCreatedAt())
                .sender(sender)
                .channelName(channelName)
                .build();
    }
}
