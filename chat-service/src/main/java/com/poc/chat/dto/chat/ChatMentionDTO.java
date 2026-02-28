package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatMention;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMentionDTO {
    private Long id;
    private Long mentionedUserId;
    private String mentionType;
    private ChatUserDTO mentionedUser;

    public static ChatMentionDTO fromEntity(ChatMention mention, ChatUserDTO user) {
        return ChatMentionDTO.builder()
                .id(mention.getId())
                .mentionedUserId(mention.getMentionedUserId())
                .mentionType(mention.getMentionType())
                .mentionedUser(user)
                .build();
    }
}
