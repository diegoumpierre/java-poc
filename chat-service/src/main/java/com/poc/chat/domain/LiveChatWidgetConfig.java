package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("CHAT_LIVECHAT_WIDGET_CONFIG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveChatWidgetConfig {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("SOURCE_SERVICE")
    private String sourceService;

    @Column("ENABLED")
    @Builder.Default
    private Boolean enabled = true;

    @Column("PRIMARY_COLOR")
    @Builder.Default
    private String primaryColor = "#4F46E5";

    @Column("HEADER_TEXT")
    @Builder.Default
    private String headerText = "Chat with us";

    @Column("WELCOME_MESSAGE")
    @Builder.Default
    private String welcomeMessage = "Hello! How can we help you?";

    @Column("OFFLINE_MESSAGE")
    @Builder.Default
    private String offlineMessage = "We are currently offline. Please leave a message.";

    @Column("POSITION")
    @Builder.Default
    private String position = "BOTTOM_RIGHT";

    @Column("REQUIRE_EMAIL")
    @Builder.Default
    private Boolean requireEmail = false;

    @Column("AUTO_OPEN_DELAY_SECONDS")
    private Integer autoOpenDelaySeconds;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
