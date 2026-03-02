package com.poc.chat.controller;

import com.poc.chat.dto.chat.ChatMessageDTO;
import com.poc.chat.dto.chat.SendMessageRequest;
import com.poc.chat.service.ChatService;
import com.poc.chat.service.PresenceService;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, SimpMessageHeaderAccessor headerAccessor) {
        setupContext(headerAccessor);
        try {
            ChatMessageDTO message = chatService.sendMessage(request);

            // Get the other participant to send them the message
            var conversation = chatService.getConversation(request.getConversationId());
            if (conversation.isPresent()) {
                String otherUserId = conversation.get().getOtherParticipant().getExternalUserId().toString();
                messagingTemplate.convertAndSendToUser(otherUserId, "/queue/messages", message);
                log.debug("WS message sent to user: {}", otherUserId);
            }
        } catch (Exception e) {
            log.error("Error sending WS message: {}", e.getMessage(), e);
        } finally {
            TenantContext.clear();
        }
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingPayload payload, SimpMessageHeaderAccessor headerAccessor) {
        setupContext(headerAccessor);
        try {
            var conversation = chatService.getConversation(payload.conversationId());
            if (conversation.isPresent()) {
                String otherUserId = conversation.get().getOtherParticipant().getExternalUserId().toString();
                String userId = getUserId(headerAccessor);
                messagingTemplate.convertAndSendToUser(otherUserId, "/queue/typing",
                        Map.of("conversationId", payload.conversationId(),
                               "userId", userId != null ? userId : "",
                               "typing", payload.typing()));
            }
        } catch (Exception e) {
            log.error("Error handling typing event: {}", e.getMessage());
        } finally {
            TenantContext.clear();
        }
    }

    @MessageMapping("/chat.presence")
    public void presence(@Payload PresencePayload payload, SimpMessageHeaderAccessor headerAccessor) {
        String userId = getUserId(headerAccessor);
        if (userId != null) {
            presenceService.updatePresence(userId, payload.status());
        }
    }

    private void setupContext(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            String userId = (String) sessionAttributes.get("userId");
            String tenantId = (String) sessionAttributes.get("tenantId");
            if (userId != null) TenantContext.setCurrentUser(UUID.fromString(userId));
            if (tenantId != null) TenantContext.setCurrentTenant(UUID.fromString(tenantId));
        }
    }

    private String getUserId(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            return (String) sessionAttributes.get("userId");
        }
        return null;
    }

    public record TypingPayload(Long conversationId, boolean typing) {}
    public record PresencePayload(String status) {}
}
