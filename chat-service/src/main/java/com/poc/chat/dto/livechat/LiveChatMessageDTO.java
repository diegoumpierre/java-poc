package com.poc.chat.dto.livechat;

import com.poc.chat.domain.LiveChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveChatMessageDTO {

    private Long id;
    private Long sessionId;
    private String senderType;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private String attachmentUrl;
    private String attachmentName;
    private Boolean isRead;
    private Instant readAt;
    private Instant createdAt;

    public static LiveChatMessageDTO fromEntity(LiveChatMessage message) {
        return LiveChatMessageDTO.builder()
                .id(message.getId())
                .sessionId(message.getSessionId())
                .senderType(message.getSenderType())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .attachmentUrl(message.getAttachmentUrl())
                .attachmentName(message.getAttachmentName())
                .isRead(message.getIsRead())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static LiveChatMessageDTO fromEntity(LiveChatMessage message, String senderName) {
        LiveChatMessageDTO dto = fromEntity(message);
        dto.setSenderName(senderName);
        return dto;
    }
}
