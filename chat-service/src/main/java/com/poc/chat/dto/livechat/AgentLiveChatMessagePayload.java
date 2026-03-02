package com.poc.chat.dto.livechat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentLiveChatMessagePayload {

    private Long sessionId;
    private SendLiveChatMessageRequest request;
}
