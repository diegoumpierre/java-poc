package com.poc.chat.security;

import com.poc.shared.tenant.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class StompChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        StompCommand command = accessor.getCommand();
        if (command == null) return message;

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) return message;

        String userId = (String) sessionAttributes.get("userId");
        String tenantId = (String) sessionAttributes.get("tenantId");
        String isVisitor = (String) sessionAttributes.get("isVisitor");

        switch (command) {
            case CONNECT, SEND, SUBSCRIBE -> {
                if ("true".equals(isVisitor)) {
                    // Visitor session: only set tenant, no user
                    if (tenantId != null) {
                        try {
                            TenantContext.setCurrentTenant(UUID.fromString(tenantId));
                        } catch (IllegalArgumentException e) {
                            log.warn("Invalid tenantId in visitor session: {}", tenantId);
                        }
                    }
                    if (command == StompCommand.CONNECT) {
                        String sessionToken = (String) sessionAttributes.get("sessionToken");
                        String visitorPrincipal = "visitor-" + sessionToken;
                        accessor.setUser(() -> visitorPrincipal);
                        log.debug("STOMP CONNECT (visitor): sessionToken={}, tenantId={}", sessionToken, tenantId);
                    }
                } else {
                    // Authenticated user session
                    if (userId != null) {
                        try {
                            TenantContext.setCurrentUser(UUID.fromString(userId));
                        } catch (IllegalArgumentException e) {
                            log.warn("Invalid userId in session: {}", userId);
                        }
                    }
                    if (tenantId != null) {
                        try {
                            TenantContext.setCurrentTenant(UUID.fromString(tenantId));
                        } catch (IllegalArgumentException e) {
                            log.warn("Invalid tenantId in session: {}", tenantId);
                        }
                    }
                    if (command == StompCommand.CONNECT) {
                        accessor.setUser(() -> userId);
                        log.debug("STOMP CONNECT: userId={}, tenantId={}", userId, tenantId);
                    }
                }
            }
            case DISCONNECT -> {
                if ("true".equals(isVisitor)) {
                    String sessionToken = (String) sessionAttributes.get("sessionToken");
                    log.debug("STOMP DISCONNECT (visitor): sessionToken={}", sessionToken);
                } else {
                    log.debug("STOMP DISCONNECT: userId={}", userId);
                }
                TenantContext.clear();
            }
            default -> { }
        }

        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        TenantContext.clear();
    }
}
