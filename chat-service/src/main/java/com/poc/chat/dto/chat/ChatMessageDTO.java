package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private Long conversationId;
    private Long channelId;
    private Long senderId;
    private ChatUserDTO sender;
    private String text;
    private boolean ownMessage;
    private Instant readAt;
    private Instant createdAt;
    private String messageType;
    private Long parentMessageId;
    private Integer replyCount;
    private Instant lastReplyAt;
    private Instant editedAt;
    private List<ChatReactionDTO> reactions;
    private List<ChatMentionDTO> mentions;
    private List<ChatAttachmentDTO> attachments;

    public static ChatMessageDTO fromEntity(ChatMessage message, ChatUserDTO sender, Long currentUserId) {
        if (message == null) return null;
        return ChatMessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .channelId(message.getChannelId())
                .senderId(message.getSenderId())
                .sender(sender)
                .text(message.getText())
                .messageType(message.getMessageType() != null ? message.getMessageType() : "TEXT")
                .ownMessage(message.getSenderId().equals(currentUserId))
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .editedAt(message.getEditedAt())
                .parentMessageId(message.getParentMessageId())
                .replyCount(message.getReplyCount() != null ? message.getReplyCount() : 0)
                .lastReplyAt(message.getLastReplyAt())
                .build();
    }
}
