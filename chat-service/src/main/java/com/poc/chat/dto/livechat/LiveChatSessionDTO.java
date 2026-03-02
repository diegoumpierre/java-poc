package com.poc.chat.dto.livechat;

import com.poc.chat.domain.LiveChatSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveChatSessionDTO {

    private Long id;
    private String tenantId;
    private String sessionToken;
    private Long visitorId;
    private String visitorName;
    private String visitorEmail;
    private Long assignedAgentId;
    private String agentName;
    private String queueId;
    private String sourceService;
    private String status;
    private String pageUrl;
    private String visitorIp;
    private String userAgent;
    private Integer messageCount;
    private Integer rating;
    private String feedback;
    private Instant agentJoinedAt;
    private Instant closedAt;
    private Instant lastActivityAt;
    private String externalContactPhone;
    private String externalContactEmail;
    private String externalContactName;
    private String externalChannel;
    private Long externalConversationId;
    private Instant createdAt;
    private Instant updatedAt;

    public static LiveChatSessionDTO fromEntity(LiveChatSession session) {
        return LiveChatSessionDTO.builder()
                .id(session.getId())
                .tenantId(session.getTenantId() != null ? session.getTenantId().toString() : null)
                .sessionToken(session.getSessionToken())
                .visitorId(session.getVisitorId())
                .assignedAgentId(session.getAssignedAgentId())
                .queueId(session.getQueueId())
                .sourceService(session.getSourceService())
                .status(session.getStatus())
                .pageUrl(session.getPageUrl())
                .visitorIp(session.getVisitorIp())
                .userAgent(session.getUserAgent())
                .messageCount(session.getMessageCount())
                .rating(session.getRating())
                .feedback(session.getFeedback())
                .agentJoinedAt(session.getAgentJoinedAt())
                .closedAt(session.getClosedAt())
                .lastActivityAt(session.getLastActivityAt())
                .createdAt(session.getCreatedAt())
                .externalContactPhone(session.getExternalContactPhone())
                .externalContactEmail(session.getExternalContactEmail())
                .externalContactName(session.getExternalContactName())
                .externalChannel(session.getExternalChannel())
                .externalConversationId(session.getExternalConversationId())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    public static LiveChatSessionDTO fromEntity(LiveChatSession session, String visitorName, String visitorEmail, String agentName) {
        LiveChatSessionDTO dto = fromEntity(session);
        dto.setVisitorName(visitorName);
        dto.setVisitorEmail(visitorEmail);
        dto.setAgentName(agentName);
        return dto;
    }
}
