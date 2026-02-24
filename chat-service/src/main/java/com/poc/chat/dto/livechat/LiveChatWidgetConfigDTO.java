package com.poc.chat.dto.livechat;

import com.poc.chat.domain.LiveChatWidgetConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveChatWidgetConfigDTO {

    private Long id;
    private String tenantId;
    private String sourceService;
    private Boolean enabled;
    private String primaryColor;
    private String headerText;
    private String welcomeMessage;
    private String offlineMessage;
    private String position;
    private Boolean requireEmail;
    private Integer autoOpenDelaySeconds;
    private Instant createdAt;
    private Instant updatedAt;

    public static LiveChatWidgetConfigDTO fromEntity(LiveChatWidgetConfig config) {
        return LiveChatWidgetConfigDTO.builder()
                .id(config.getId())
                .tenantId(config.getTenantId() != null ? config.getTenantId().toString() : null)
                .sourceService(config.getSourceService())
                .enabled(config.getEnabled())
                .primaryColor(config.getPrimaryColor())
                .headerText(config.getHeaderText())
                .welcomeMessage(config.getWelcomeMessage())
                .offlineMessage(config.getOfflineMessage())
                .position(config.getPosition())
                .requireEmail(config.getRequireEmail())
                .autoOpenDelaySeconds(config.getAutoOpenDelaySeconds())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
