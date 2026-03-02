package com.poc.chat.dto.livechat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveChatEventDTO {

    private String eventType;
    private String tenantId;
    private Long sessionId;
    private String sessionToken;
    private String visitorName;
    private String visitorEmail;
    private String sourceService;
    private Long assignedAgentId;
    private String agentName;
    private Integer rating;
    private String feedback;
    private String pageUrl;
    private String messagesJson;   // JSON array of messages snapshot (included on SESSION_CLOSED)
    private Instant timestamp;
}
