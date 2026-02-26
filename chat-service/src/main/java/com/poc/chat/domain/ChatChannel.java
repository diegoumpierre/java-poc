package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("CHAT_CHANNEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatChannel {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("TYPE")
    private String type;

    @Column("NAME")
    private String name;

    @Column("SLUG")
    private String slug;

    @Column("DESCRIPTION")
    private String description;

    @Column("AVATAR_URL")
    private String avatarUrl;

    @Column("CREATOR_ID")
    private Long creatorId;

    @Column("IS_ARCHIVED")
    private Boolean isArchived;

    @Column("LAST_MESSAGE_AT")
    private Instant lastMessageAt;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    public ChannelType getTypeEnum() {
        return type != null ? ChannelType.valueOf(type) : null;
    }

    public void setTypeEnum(ChannelType channelType) {
        this.type = channelType != null ? channelType.name() : null;
    }

    public boolean isDm() {
        return ChannelType.DM.name().equals(type);
    }

    public boolean isPublicChannel() {
        return ChannelType.PUBLIC.name().equals(type);
    }
}
