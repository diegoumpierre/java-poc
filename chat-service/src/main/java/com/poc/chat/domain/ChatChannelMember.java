package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("CHAT_CHANNEL_MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatChannelMember {

    @Id
    @Column("ID")
    private Long id;

    @Column("CHANNEL_ID")
    private Long channelId;

    @Column("USER_ID")
    private Long userId;

    @Column("ROLE")
    @Builder.Default
    private String role = ChannelMemberRole.MEMBER.name();

    @Column("LAST_READ_AT")
    private Instant lastReadAt;

    @Column("MUTED")
    @Builder.Default
    private Boolean muted = false;

    @Column("JOINED_AT")
    private Instant joinedAt;

    public ChannelMemberRole getRoleEnum() {
        return role != null ? ChannelMemberRole.valueOf(role) : ChannelMemberRole.MEMBER;
    }

    public void setRoleEnum(ChannelMemberRole memberRole) {
        this.role = memberRole != null ? memberRole.name() : ChannelMemberRole.MEMBER.name();
    }
}
