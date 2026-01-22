package com.poc.auth.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.auth.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Listens to user-events topic from user-service.
 * Handles USER_DELETED events to revoke all sessions for the deleted user.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class UserEventListener {

    private final SessionService sessionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.user-events:user-events}", groupId = "auth-service")
    public void handleUserEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.has("eventType") ? event.get("eventType").asText() : null;

            if ("USER_DELETED".equals(eventType)) {
                String userIdStr = event.has("userId") ? event.get("userId").asText() : null;
                if (userIdStr != null) {
                    UUID userId = UUID.fromString(userIdStr);
                    log.info("Received USER_DELETED event for user: {}", userId);
                    try {
                        sessionService.revokeAllSessions(userId, "User account deleted");
                        log.info("Revoked all sessions for deleted user: {}", userId);
                    } catch (Exception e) {
                        log.warn("Failed to revoke sessions for user {}: {}", userId, e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to process user event: {}", e.getMessage(), e);
        }
    }
}
