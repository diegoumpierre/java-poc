package com.poc.chat.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.chat.service.LiveChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailInboundConsumer {

    private final LiveChatService liveChatService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.email-inbound-events}",
                   groupId = "${app.kafka.consumer.group-id}")
    public void consume(String message, Acknowledgment ack) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = objectMapper.readValue(message, Map.class);

            String eventType = (String) event.get("eventType");
            if (!"INBOUND_MESSAGE".equals(eventType)) {
                ack.acknowledge();
                return;
            }

            String tenantId = (String) event.get("tenantId");
            String contactEmail = (String) event.get("contactEmail");
            String contactName = (String) event.get("contactName");
            String subject = (String) event.get("subject");
            String contentText = (String) event.get("contentText");
            Number conversationIdNum = (Number) event.get("emailConversationId");
            Long emailConversationId = conversationIdNum != null ? conversationIdNum.longValue() : null;

            // Use contentText or subject as message content
            String content = contentText != null ? contentText : (subject != null ? "[Email] " + subject : "[New Email]");

            liveChatService.handleExternalInbound(
                    tenantId, "EMAIL", null, contactEmail,
                    contactName, content, "TEXT", emailConversationId);

            ack.acknowledge();
            log.debug("Processed Email inbound event for tenant {} from {}", tenantId, contactEmail);
        } catch (Exception e) {
            log.error("Failed to process Email inbound event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
