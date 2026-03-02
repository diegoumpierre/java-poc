package com.poc.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WebSocketSessionRegistry {

    // userId -> Set<sessionId>
    private final Map<String, Set<String>> userSessions = new ConcurrentHashMap<>();
    // sessionId -> userId
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();

    public void registerSession(String userId, String sessionId) {
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        sessionToUser.put(sessionId, userId);
        log.debug("Session registered: userId={}, sessionId={}, totalSessions={}",
                userId, sessionId, userSessions.get(userId).size());
    }

    public void removeSession(String sessionId) {
        String userId = sessionToUser.remove(sessionId);
        if (userId != null) {
            Set<String> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
            log.debug("Session removed: userId={}, sessionId={}, remainingSessions={}",
                    userId, sessionId, sessions != null ? sessions.size() : 0);
        }
    }

    public String getUserIdBySession(String sessionId) {
        return sessionToUser.get(sessionId);
    }

    public boolean hasActiveSessions(String userId) {
        Set<String> sessions = userSessions.get(userId);
        return sessions != null && !sessions.isEmpty();
    }

    public Set<String> getConnectedUserIds() {
        return Collections.unmodifiableSet(userSessions.keySet());
    }

    public int getSessionCount(String userId) {
        Set<String> sessions = userSessions.get(userId);
        return sessions != null ? sessions.size() : 0;
    }
}
