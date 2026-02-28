package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatUser;
import com.poc.chat.domain.ChatUserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDTO {
    private Long id;
    private UUID externalUserId;
    private String name;
    private String email;
    private String avatarUrl;
    private String status;
    private Instant lastSeenAt;

    public static ChatUserDTO fromEntity(ChatUser user) {
        if (user == null) return null;
        return ChatUserDTO.builder()
                .id(user.getId())
                .externalUserId(user.getExternalUserId())
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .lastSeenAt(user.getLastSeenAt())
                .build();
    }

    public String getStatusDisplay() {
        if (status == null) return "offline";
        try {
            ChatUserStatus userStatus = ChatUserStatus.valueOf(status);
            return userStatus.name().toLowerCase();
        } catch (IllegalArgumentException e) {
            return "offline";
        }
    }
}
