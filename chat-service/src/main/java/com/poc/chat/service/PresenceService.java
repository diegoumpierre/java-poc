package com.poc.chat.service;

import com.poc.chat.domain.ChatUserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceService {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionRegistry sessionRegistry;

    // userId -> current status
    private final Map<String, String> userPresence = new ConcurrentHashMap<>();

    public void userConnected(String userId) {
        userPresence.put(userId, ChatUserStatus.ONLINE.name());
        broadcastPresence(userId, ChatUserStatus.ONLINE.name());
        log.debug("User connected: {}", userId);
    }

    public void userDisconnected(String userId) {
        // Only mark offline if no more sessions
        if (!sessionRegistry.hasActiveSessions(userId)) {
            userPresence.put(userId, ChatUserStatus.OFFLINE.name());
            broadcastPresence(userId, ChatUserStatus.OFFLINE.name());
            log.debug("User disconnected (no sessions left): {}", userId);
        } else {
            log.debug("User disconnected but still has active sessions: {}", userId);
        }
    }

    public void updatePresence(String userId, String status) {
        userPresence.put(userId, status);
        broadcastPresence(userId, status);
        log.debug("User presence updated: userId={}, status={}", userId, status);
    }

    public String getPresence(String userId) {
        return userPresence.getOrDefault(userId, ChatUserStatus.OFFLINE.name());
    }

    public Map<String, String> getAllPresence() {
        return Map.copyOf(userPresence);
    }

    private void broadcastPresence(String userId, String status) {
        Map<String, Object> presenceEvent = Map.of(
                "userId", userId,
                "status", status
        );

        // Broadcast to all connected users
        Set<String> connectedUsers = sessionRegistry.getConnectedUserIds();
        for (String connectedUserId : connectedUsers) {
            if (!connectedUserId.equals(userId)) {
                messagingTemplate.convertAndSendToUser(connectedUserId, "/queue/presence", presenceEvent);
            }
        }
    }
}
