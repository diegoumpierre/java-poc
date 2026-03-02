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
public class WhatsAppInboundConsumer {

    private final LiveChatService liveChatService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.whatsapp-inbound-events}",
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
            String contactPhone = (String) event.get("contactPhone");
            String contactName = (String) event.get("contactName");
            String content = (String) event.get("content");
            String messageType = (String) event.get("messageType");
            Number conversationIdNum = (Number) event.get("whatsappConversationId");
            Long whatsappConversationId = conversationIdNum != null ? conversationIdNum.longValue() : null;

            liveChatService.handleExternalInbound(
                    tenantId, "WHATSAPP", contactPhone, null,
                    contactName, content, messageType, whatsappConversationId);

            ack.acknowledge();
            log.debug("Processed WhatsApp inbound event for tenant {} from {}", tenantId, contactPhone);
        } catch (Exception e) {
            log.error("Failed to process WhatsApp inbound event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
