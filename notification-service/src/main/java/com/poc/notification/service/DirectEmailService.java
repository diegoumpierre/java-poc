package com.poc.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.notification.domain.ConfigType;
import com.poc.notification.domain.Conversation;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.TenantConfig;
import com.poc.notification.dto.SendEmailRequest;
import com.poc.notification.dto.SendEmailResponse;
import com.poc.notification.provider.SmtpEmailProvider;
import com.poc.notification.repository.EmailHistoryRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectEmailService {

    private final EmailHistoryRepository emailHistoryRepository;
    private final TenantConfigService tenantConfigService;
    private final ConversationService conversationService;
    private final RateLimitService rateLimitService;
    private final SmtpEmailProvider smtpProvider;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.outbound-queue}")
    private String outboundQueueTopic;

    @Transactional
    public SendEmailResponse queueEmail(SendEmailRequest request) {
        String tenantId = TenantContext.getCurrentTenant().toString();
        String userId = TenantContext.getCurrentUser() != null ? TenantContext.getCurrentUser().toString() : null;
        Instant now = Instant.now();
        String messageId = UUID.randomUUID().toString();

        // Resolve or create conversation
        Long conversationId = request.getConversationId();
        if (conversationId == null) {
            Conversation conv = conversationService.getOrCreateConversation(
                    tenantId, request.getRecipientEmail(), null,
                    request.getSubject(), null);
            conversationId = conv.getId();
        }

        // Save email history with CONFIG_TYPE = ATENDIMENTO
        EmailHistory msg = EmailHistory.builder()
                .messageId(messageId)
                .tenantId(tenantId)
                .userId(userId)
                .configType(ConfigType.ATENDIMENTO.name())
                .conversationId(conversationId)
                .recipient(request.getRecipientEmail())
                .subject(request.getSubject())
                .contentText(request.getContentText())
                .contentHtml(request.getContentHtml())
                .inReplyTo(request.getInReplyTo())
                .referencesHeader(request.getReferencesHeader())
                .status("PENDING")
                .createdAt(now)
                .updatedAt(now)
                .build();
        emailHistoryRepository.save(msg);

        // Update conversation
        String preview = request.getContentText() != null ? request.getContentText() : "[HTML Email]";
        conversationService.updateConversationLastMessage(conversationId, preview, false);

        // Publish to outbound queue
        try {
            String json = objectMapper.writeValueAsString(msg);
            kafkaTemplate.send(outboundQueueTopic, tenantId, json);
            log.info("Direct email queued: messageId={}, to={}", messageId, request.getRecipientEmail());
        } catch (Exception e) {
            log.error("Failed to publish email to outbound queue: {}", e.getMessage(), e);
        }

        return SendEmailResponse.builder()
                .messageId(messageId)
                .status("PENDING")
                .recipientEmail(request.getRecipientEmail())
                .createdAt(now)
                .build();
    }

    public void sendEmail(EmailHistory msg) {
        String configType = msg.getConfigType() != null ? msg.getConfigType() : ConfigType.ATENDIMENTO.name();
        TenantConfig config = tenantConfigService.getConfigByTenantIdAndType(msg.getTenantId(), configType);
        Instant now = Instant.now();

        try {
            msg.setStatus("SENDING");
            msg.setUpdatedAt(now);
            emailHistoryRepository.save(msg);

            smtpProvider.sendEmail(config, msg.getRecipient(), msg.getSubject(),
                    msg.getContentText(), msg.getContentHtml(),
                    msg.getInReplyTo(), msg.getReferencesHeader());

            rateLimitService.recordSend(msg.getTenantId(), configType);

            msg.setStatus("SENT");
            msg.setSentAt(Instant.now());
            msg.setUpdatedAt(Instant.now());
            emailHistoryRepository.save(msg);

            log.info("Direct email sent: messageId={}, to={}", msg.getMessageId(), msg.getRecipient());
        } catch (Exception e) {
            handleSendError(msg, configType, e);
        }
    }

    public void sendTestEmail(String toAddress) {
        String tenantId = TenantContext.getCurrentTenant().toString();
        TenantConfig config = tenantConfigService.getConfig(ConfigType.ATENDIMENTO.name());
        smtpProvider.sendEmail(config, toAddress, "Test Email from 101 Platform",
                "This is a test email from your email service configuration.", null, null, null);
    }

    private void handleSendError(EmailHistory msg, String configType, Exception e) {
        String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
        Instant now = Instant.now();

        if (errorMessage.contains("550")) {
            msg.setStatus("FAILED");
            msg.setErrorMessage("Permanent failure: " + errorMessage);
            log.warn("Permanent email failure for messageId={}: {}", msg.getMessageId(), errorMessage);
        } else {
            msg.setRetryCount(msg.getRetryCount() + 1);
            if (msg.getRetryCount() >= 3) {
                msg.setStatus("DEAD");
                msg.setErrorMessage("Max retries exceeded: " + errorMessage);
                log.error("Email dead after 3 retries: messageId={}", msg.getMessageId());
            } else {
                msg.setStatus("FAILED");
                msg.setErrorMessage(errorMessage);
                log.warn("Email send failed (retry {}): messageId={}", msg.getRetryCount(), msg.getMessageId());
            }
        }

        msg.setUpdatedAt(now);
        emailHistoryRepository.save(msg);

        rateLimitService.handleSmtpError(msg.getTenantId(), configType, e);
    }
}
