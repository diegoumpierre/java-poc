package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatChannel;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatChannelDTO {

    private Long id;
    private String tenantId;
    private String type;
    private String name;
    private String slug;
    private String description;
    private String avatarUrl;
    private Boolean isArchived;
    private Instant lastMessageAt;
    private Instant createdAt;
    private int memberCount;
    private int unreadCount;
    private ChatMessageDTO lastMessage;
    private List<ChannelMemberDTO> members;

    // For DMs - the other participant
    private ChatUserDTO otherParticipant;

    public static ChatChannelDTO fromEntity(ChatChannel channel) {
        return ChatChannelDTO.builder()
                .id(channel.getId())
                .tenantId(channel.getTenantId() != null ? channel.getTenantId().toString() : null)
                .type(channel.getType())
                .name(channel.getName())
                .slug(channel.getSlug())
                .description(channel.getDescription())
                .avatarUrl(channel.getAvatarUrl())
                .isArchived(channel.getIsArchived())
                .lastMessageAt(channel.getLastMessageAt())
                .createdAt(channel.getCreatedAt())
                .build();
    }
}
