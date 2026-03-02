package com.poc.chat.dto.livechat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartLiveChatRequest {

    private String visitorId;
    private String visitorName;
    private String visitorEmail;
    private String pageUrl;
    private String initialMessage;
    private String queueId;

    @Builder.Default
    private String sourceService = "HELPDESK";

    private String metadata;
}
