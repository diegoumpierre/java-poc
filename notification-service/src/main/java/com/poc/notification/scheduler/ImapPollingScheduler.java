package com.poc.notification.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.notification.domain.Conversation;
import com.poc.notification.domain.InboundMessage;
import com.poc.notification.domain.TenantConfig;
import com.poc.notification.dto.event.EmailInboundEventDTO;
import com.poc.notification.repository.InboundMessageRepository;
import com.poc.notification.repository.TenantConfigRepository;
import com.poc.notification.service.ConversationService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImapPollingScheduler {

    private final TenantConfigRepository configRepository;
    private final InboundMessageRepository inboundMessageRepository;
    private final ConversationService conversationService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.inbound-events}")
    private String inboundEventsTopic;

    @Scheduled(fixedDelayString = "${app.scheduler.imap-poll-rate:30000}")
    public void pollInboxes() {
        var configs = configRepository.findAllEnabledWithImap();
        for (TenantConfig config : configs) {
            try {
                pollTenantInbox(config);
            } catch (Exception e) {
                log.error("Failed to poll inbox for tenant {} type {}: {}", config.getTenantId(), config.getConfigType(), e.getMessage());
            }
        }
    }

    private void pollTenantInbox(TenantConfig config) throws Exception {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", config.getImapHost());
        props.put("mail.imaps.port", String.valueOf(config.getImapPort()));
        props.put("mail.imaps.ssl.enable", String.valueOf(Boolean.TRUE.equals(config.getImapUseTls())));
        props.put("mail.imaps.connectiontimeout", "10000");
        props.put("mail.imaps.timeout", "10000");

        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");

        try {
            store.connect(config.getImapHost(), config.getImapUsername(), config.getImapPassword());

            String folderName = config.getImapFolder() != null ? config.getImapFolder() : "INBOX";
            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.search(new jakarta.mail.search.FlagTerm(
                    new Flags(Flags.Flag.SEEN), false));

            if (messages.length == 0) {
                folder.close(false);
                return;
            }

            log.info("Found {} new emails for tenant {} type {}", messages.length, config.getTenantId(), config.getConfigType());

            for (Message message : messages) {
                try {
                    processInboundEmail(config, (MimeMessage) message);
                    message.setFlag(Flags.Flag.SEEN, true);
                } catch (Exception e) {
                    log.error("Failed to process email for tenant {}: {}", config.getTenantId(), e.getMessage());
                }
            }

            config.setLastPollAt(Instant.now());
            configRepository.save(config);

            folder.close(false);
        } finally {
            if (store.isConnected()) {
                store.close();
            }
        }
    }

    private void processInboundEmail(TenantConfig config, MimeMessage message) throws Exception {
        String tenantId = config.getTenantId();

        Address[] fromAddresses = message.getFrom();
        String fromEmail = fromAddresses != null && fromAddresses.length > 0
                ? ((InternetAddress) fromAddresses[0]).getAddress() : "unknown";
        String fromName = fromAddresses != null && fromAddresses.length > 0
                ? ((InternetAddress) fromAddresses[0]).getPersonal() : null;

        String subject = message.getSubject();
        String messageId = message.getMessageID();
        String inReplyTo = message.getHeader("In-Reply-To") != null
                ? message.getHeader("In-Reply-To")[0] : null;
        String references = message.getHeader("References") != null
                ? message.getHeader("References")[0] : null;

        if (fromEmail.equalsIgnoreCase(config.getFromAddress())) {
            return;
        }

        if (messageId != null && inboundMessageRepository.findByProviderMessageIdAndTenantId(messageId, tenantId).isPresent()) {
            return;
        }

        String textContent = extractTextContent(message);
        String htmlContent = extractHtmlContent(message);
        boolean hasAttachments = hasAttachments(message);

        String threadId = references != null ? references : inReplyTo;

        Conversation conversation = conversationService.getOrCreateConversation(
                tenantId, fromEmail, fromName, subject, threadId);

        Instant now = Instant.now();
        InboundMessage inbound = InboundMessage.builder()
                .tenantId(tenantId)
                .conversationId(conversation.getId())
                .fromEmail(fromEmail)
                .fromName(fromName)
                .subject(subject)
                .contentText(textContent)
                .contentHtml(htmlContent)
                .providerMessageId(messageId)
                .inReplyTo(inReplyTo)
                .referencesHeader(references)
                .hasAttachments(hasAttachments)
                .receivedAt(message.getReceivedDate() != null ? message.getReceivedDate().toInstant() : now)
                .createdAt(now)
                .build();
        inboundMessageRepository.save(inbound);

        String preview = textContent != null ? textContent : (subject != null ? subject : "");
        conversationService.updateConversationLastMessage(conversation.getId(), preview, true);

        publishInboundEvent(tenantId, conversation.getId(), fromEmail, fromName,
                subject, textContent, htmlContent, messageId, threadId);

        log.info("Inbound email processed: from={}, tenant={}", fromEmail, tenantId);
    }

    private void publishInboundEvent(String tenantId, Long conversationId, String contactEmail,
                                     String contactName, String subject, String contentText,
                                     String contentHtml, String providerMessageId, String threadId) {
        try {
            EmailInboundEventDTO event = EmailInboundEventDTO.builder()
                    .eventType("INBOUND_MESSAGE")
                    .tenantId(tenantId)
                    .emailConversationId(conversationId)
                    .contactEmail(contactEmail)
                    .contactName(contactName)
                    .subject(subject)
                    .contentText(contentText)
                    .contentHtml(contentHtml)
                    .messageType(contentHtml != null ? "HTML" : "TEXT")
                    .providerMessageId(providerMessageId)
                    .threadId(threadId)
                    .timestamp(Instant.now())
                    .build();
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(inboundEventsTopic, tenantId, json);
        } catch (Exception e) {
            log.error("Failed to publish inbound email event: {}", e.getMessage(), e);
        }
    }

    private String extractTextContent(MimeMessage message) throws Exception {
        Object content = message.getContent();
        if (content instanceof String) {
            return message.isMimeType("text/plain") ? (String) content : null;
        }
        if (content instanceof Multipart multipart) {
            return extractTextFromMultipart(multipart);
        }
        return null;
    }

    private String extractHtmlContent(MimeMessage message) throws Exception {
        Object content = message.getContent();
        if (content instanceof String) {
            return message.isMimeType("text/html") ? (String) content : null;
        }
        if (content instanceof Multipart multipart) {
            return extractHtmlFromMultipart(multipart);
        }
        return null;
    }

    private String extractTextFromMultipart(Multipart multipart) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (part.isMimeType("text/plain")) {
                return (String) part.getContent();
            }
            if (part.getContent() instanceof Multipart nested) {
                String result = extractTextFromMultipart(nested);
                if (result != null) return result;
            }
        }
        return null;
    }

    private String extractHtmlFromMultipart(Multipart multipart) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (part.isMimeType("text/html")) {
                return (String) part.getContent();
            }
            if (part.getContent() instanceof Multipart nested) {
                String result = extractHtmlFromMultipart(nested);
                if (result != null) return result;
            }
        }
        return null;
    }

    private boolean hasAttachments(MimeMessage message) throws Exception {
        Object content = message.getContent();
        if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    return true;
                }
            }
        }
        return false;
    }
}
