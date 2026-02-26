package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("CHAT_USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUser {

    @Id
    @Column("ID")
    private Long id;

    @Column("EXTERNAL_USER_ID")
    private UUID externalUserId;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("NAME")
    private String name;

    @Column("EMAIL")
    private String email;

    @Column("AVATAR_URL")
    private String avatarUrl;

    @Column("STATUS")
    @Builder.Default
    private String status = ChatUserStatus.OFFLINE.name();

    @Column("LAST_SEEN_AT")
    private Instant lastSeenAt;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    public ChatUserStatus getStatusEnum() {
        return status != null ? ChatUserStatus.valueOf(status) : ChatUserStatus.OFFLINE;
    }

    public void setStatusEnum(ChatUserStatus status) {
        this.status = status != null ? status.name() : ChatUserStatus.OFFLINE.name();
    }
}
