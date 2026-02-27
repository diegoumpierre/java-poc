package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("CHAT_LIVECHAT_VISITOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveChatVisitor {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("VISITOR_ID")
    private String visitorId;

    @Column("NAME")
    private String name;

    @Column("EMAIL")
    private String email;

    @Column("METADATA")
    private String metadata;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
