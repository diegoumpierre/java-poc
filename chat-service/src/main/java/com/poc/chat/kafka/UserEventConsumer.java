package com.poc.chat.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.chat.dto.event.UserEventDTO;
import com.poc.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${app.kafka.topics.user-events}",
            groupId = "${app.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.debug("Received user event: key={}, value={}", record.key(), record.value());
            UserEventDTO event = objectMapper.readValue(record.value(), UserEventDTO.class);

            switch (event.getEventType()) {
                case "USER_CREATED" -> handleUserCreated(event);
                case "USER_UPDATED" -> handleUserUpdated(event);
                case "USER_DELETED" -> handleUserDeleted(event);
                default -> log.warn("Unknown user event type: {}", event.getEventType());
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing user event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    private void handleUserCreated(UserEventDTO event) {
        log.info("Creating chat user for: userId={}, tenantId={}, name={}",
                event.getUserId(), event.getTenantId(), event.getName());
        chatService.getOrCreateChatUser(
                event.getUserId(),
                event.getTenantId(),
                event.getName(),
                event.getEmail(),
                event.getAvatarUrl()
        );
    }

    private void handleUserUpdated(UserEventDTO event) {
        log.info("Updating chat user for: userId={}, tenantId={}",
                event.getUserId(), event.getTenantId());
        var chatUser = chatService.getOrCreateChatUser(
                event.getUserId(),
                event.getTenantId(),
                event.getName(),
                event.getEmail(),
                event.getAvatarUrl()
        );
        chatService.updateUserProfile(
                chatUser.getId(),
                event.getName(),
                event.getEmail(),
                event.getAvatarUrl()
        );
    }

    private void handleUserDeleted(UserEventDTO event) {
        log.info("User deleted event received: userId={}, tenantId={} - preserving chat history",
                event.getUserId(), event.getTenantId());
    }
}
