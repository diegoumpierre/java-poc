package com.poc.chat.controller;

import com.poc.chat.dto.livechat.AgentLiveChatMessagePayload;
import com.poc.chat.dto.livechat.SendLiveChatMessageRequest;
import com.poc.chat.service.LiveChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LiveChatWebSocketController {

    private final LiveChatService liveChatService;

    /**
     * Visitor sends message via WebSocket.
     * Destination: /app/livechat.visitor.send
     */
    @MessageMapping("/livechat.visitor.send")
    public void visitorSendMessage(@Payload SendLiveChatMessageRequest request,
                                    SimpMessageHeaderAccessor headerAccessor) {
        String sessionToken = (String) headerAccessor.getSessionAttributes().get("sessionToken");
        if (sessionToken == null) {
            log.warn("Visitor send attempt without session token");
            return;
        }
        try {
            liveChatService.sendVisitorMessage(sessionToken, request);
        } catch (Exception e) {
            log.error("Error sending visitor message via WS: sessionToken={}, error={}", sessionToken, e.getMessage());
        }
    }

    /**
     * Visitor typing indicator.
     * Destination: /app/livechat.visitor.typing
     */
    @MessageMapping("/livechat.visitor.typing")
    public void visitorTyping(SimpMessageHeaderAccessor headerAccessor) {
        String sessionToken = (String) headerAccessor.getSessionAttributes().get("sessionToken");
        if (sessionToken == null) return;
        try {
            liveChatService.notifyVisitorTyping(sessionToken);
        } catch (Exception e) {
            log.error("Error handling visitor typing: {}", e.getMessage());
        }
    }

    /**
     * Agent sends message via WebSocket (alternative to REST).
     * Destination: /app/livechat.agent.send
     */
    @MessageMapping("/livechat.agent.send")
    public void agentSendMessage(@Payload AgentLiveChatMessagePayload payload,
                                  SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (userId == null) {
            log.warn("Agent send attempt without userId");
            return;
        }
        try {
            liveChatService.sendAgentMessageViaWs(payload.getSessionId(), payload.getRequest(), userId);
        } catch (Exception e) {
            log.error("Error sending agent message via WS: userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * Agent typing indicator.
     * Destination: /app/livechat.agent.typing
     */
    @MessageMapping("/livechat.agent.typing")
    public void agentTyping(@Payload Map<String, Long> payload,
                             SimpMessageHeaderAccessor headerAccessor) {
        Long sessionId = payload.get("sessionId");
        if (sessionId == null) return;
        try {
            liveChatService.notifyAgentTyping(sessionId);
        } catch (Exception e) {
            log.error("Error handling agent typing: {}", e.getMessage());
        }
    }
}
