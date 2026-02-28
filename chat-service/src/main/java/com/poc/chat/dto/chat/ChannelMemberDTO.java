package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatChannelMember;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelMemberDTO {

    private Long id;
    private Long channelId;
    private Long userId;
    private String role;
    private Instant lastReadAt;
    private Boolean muted;
    private Instant joinedAt;

    // Enriched user info
    private ChatUserDTO user;

    public static ChannelMemberDTO fromEntity(ChatChannelMember member) {
        return ChannelMemberDTO.builder()
                .id(member.getId())
                .channelId(member.getChannelId())
                .userId(member.getUserId())
                .role(member.getRole())
                .lastReadAt(member.getLastReadAt())
                .muted(member.getMuted())
                .joinedAt(member.getJoinedAt())
                .build();
    }

    public static ChannelMemberDTO fromEntity(ChatChannelMember member, ChatUserDTO user) {
        ChannelMemberDTO dto = fromEntity(member);
        dto.setUser(user);
        return dto;
    }
}
