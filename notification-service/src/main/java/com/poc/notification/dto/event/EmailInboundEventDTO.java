package com.poc.notification.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailInboundEventDTO {

    private String eventType;
    private String tenantId;
    private Long emailConversationId;
    private String contactEmail;
    private String contactName;
    private String subject;
    private String contentText;
    private String contentHtml;
    private String messageType;
    private String providerMessageId;
    private String threadId;
    private Instant timestamp;
}
