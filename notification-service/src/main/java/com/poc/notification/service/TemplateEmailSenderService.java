package com.poc.notification.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.notification.domain.ConfigType;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.EmailTemplate;
import com.poc.notification.domain.TenantConfig;
import com.poc.notification.provider.SmtpEmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateEmailSenderService {

    private final SmtpEmailProvider smtpProvider;
    private final TenantConfigService tenantConfigService;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    public void send(EmailHistory history) {
        // Get NOTIFICATION config from platform tenant
        TenantConfig config = tenantConfigService.getNotificationConfig();

        if (!Boolean.TRUE.equals(config.getEnabled())) {
            log.info("[EMAIL DISABLED] To: {} | Template: {} | Subject: {}",
                    history.getRecipient(), history.getTemplate(), history.getSubject());
            return;
        }

        try {
            // Process template
            EmailTemplate template = EmailTemplate.valueOf(history.getTemplate());
            Map<String, Object> variables = parseVariables(history.getVariables());
            variables.put("baseUrl", config.getBaseUrl());

            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(
                    "email/" + template.getTemplateName(),
                    context
            );

            // Send via SmtpEmailProvider (using NOTIFICATION config)
            smtpProvider.sendEmail(config, history.getRecipient(), history.getSubject(),
                    null, htmlContent, null, null);

            log.info("Template email sent successfully to: {}", history.getRecipient());

        } catch (Exception e) {
            log.error("Failed to send template email {}: {}", history.getMessageId(), e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> parseVariables(String json) {
        if (json == null || json.isEmpty() || json.equals("null")) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse variables JSON: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
