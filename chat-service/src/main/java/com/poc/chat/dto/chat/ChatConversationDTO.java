package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatConversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationDTO {
    private Long id;
    private ChatUserDTO otherParticipant;
    private ChatMessageDTO lastMessage;
    private int unreadCount;
    private Instant lastMessageAt;
    private Instant createdAt;

    public static ChatConversationDTO fromEntity(ChatConversation conversation,
                                                   ChatUserDTO otherParticipant,
                                                   ChatMessageDTO lastMessage,
                                                   int unreadCount) {
        if (conversation == null) return null;
        return ChatConversationDTO.builder()
                .id(conversation.getId())
                .otherParticipant(otherParticipant)
                .lastMessage(lastMessage)
                .unreadCount(unreadCount)
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .build();
    }
}
