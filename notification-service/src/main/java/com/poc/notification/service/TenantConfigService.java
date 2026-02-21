package com.poc.notification.service;

import com.poc.notification.domain.ConfigType;
import com.poc.notification.domain.TenantConfig;
import com.poc.notification.dto.TenantConfigRequest;
import com.poc.notification.provider.SmtpEmailProvider;
import com.poc.notification.repository.TenantConfigRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantConfigService {

    private final TenantConfigRepository configRepository;
    private final SmtpEmailProvider smtpProvider;

    @Value("${app.platform-tenant-id:00000000-0000-0000-0000-000000000000}")
    private String platformTenantId;

    @Transactional
    public TenantConfig createOrUpdate(TenantConfigRequest request) {
        String tenantId = TenantContext.getCurrentTenant().toString();
        String configType = request.getConfigType() != null ? request.getConfigType() : ConfigType.ATENDIMENTO.name();

        // Validate: NOTIFICATION config only for platform tenant
        if (ConfigType.NOTIFICATION.name().equals(configType) && !platformTenantId.equals(tenantId)) {
            throw new IllegalArgumentException("Only the platform tenant can create NOTIFICATION config");
        }

        Instant now = Instant.now();

        TenantConfig config = configRepository.findByTenantIdAndConfigType(tenantId, configType)
                .orElseGet(() -> TenantConfig.builder()
                        .tenantId(tenantId)
                        .configType(configType)
                        .createdAt(now)
                        .build());

        config.setSmtpHost(request.getSmtpHost());
        config.setSmtpPort(request.getSmtpPort() != null ? request.getSmtpPort() : 587);
        config.setSmtpUsername(request.getSmtpUsername());
        config.setSmtpPassword(request.getSmtpPassword());
        if (request.getSmtpUseTls() != null) config.setSmtpUseTls(request.getSmtpUseTls());

        if (request.getImapHost() != null) config.setImapHost(request.getImapHost());
        if (request.getImapPort() != null) config.setImapPort(request.getImapPort());
        if (request.getImapUsername() != null) config.setImapUsername(request.getImapUsername());
        if (request.getImapPassword() != null) config.setImapPassword(request.getImapPassword());
        if (request.getImapUseTls() != null) config.setImapUseTls(request.getImapUseTls());
        if (request.getImapFolder() != null) config.setImapFolder(request.getImapFolder());

        config.setFromAddress(request.getFromAddress());
        if (request.getFromName() != null) config.setFromName(request.getFromName());
        if (request.getReplyTo() != null) config.setReplyTo(request.getReplyTo());
        if (request.getBaseUrl() != null) config.setBaseUrl(request.getBaseUrl());

        if (request.getPollIntervalSeconds() != null) config.setPollIntervalSeconds(request.getPollIntervalSeconds());
        if (request.getMaxEmailsPerMinute() != null) config.setMaxEmailsPerMinute(request.getMaxEmailsPerMinute());
        if (request.getMaxEmailsPerHour() != null) config.setMaxEmailsPerHour(request.getMaxEmailsPerHour());
        if (request.getMaxEmailsPerDay() != null) config.setMaxEmailsPerDay(request.getMaxEmailsPerDay());
        if (request.getCooldownSeconds() != null) config.setCooldownSeconds(request.getCooldownSeconds());
        if (request.getEnabled() != null) config.setEnabled(request.getEnabled());

        config.setUpdatedAt(now);

        TenantConfig saved = configRepository.save(config);
        log.info("Config saved for tenant {} type {}", tenantId, configType);
        return saved;
    }

    @Transactional(readOnly = true)
    public TenantConfig getConfig(String configType) {
        String tenantId = TenantContext.getCurrentTenant().toString();
        return configRepository.findByTenantIdAndConfigType(tenantId, configType)
                .orElseThrow(() -> new IllegalArgumentException("Config not found for tenant, type: " + configType));
    }

    @Transactional(readOnly = true)
    public List<TenantConfig> getConfigs() {
        String tenantId = TenantContext.getCurrentTenant().toString();
        return configRepository.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public TenantConfig getConfigByTenantIdAndType(String tenantId, String configType) {
        return configRepository.findByTenantIdAndConfigType(tenantId, configType)
                .orElseThrow(() -> new IllegalArgumentException("Config not found for tenant: " + tenantId + " type: " + configType));
    }

    @Transactional(readOnly = true)
    public TenantConfig getNotificationConfig() {
        return configRepository.findByTenantIdAndConfigType(platformTenantId, ConfigType.NOTIFICATION.name())
                .orElseThrow(() -> new IllegalArgumentException("NOTIFICATION config not found for platform tenant"));
    }

    public Map<String, Object> testConnection(String configType) {
        TenantConfig config = getConfig(configType);
        Map<String, Object> result = new LinkedHashMap<>();

        boolean smtpOk = smtpProvider.testConnection(config);
        result.put("smtp", smtpOk ? "connected" : "failed");

        boolean imapOk = false;
        if (config.getImapHost() != null && !config.getImapHost().isBlank()) {
            imapOk = testImapConnection(config);
        }
        result.put("imap", config.getImapHost() != null ? (imapOk ? "connected" : "failed") : "not_configured");
        result.put("enabled", config.getEnabled());
        result.put("configType", configType);

        return result;
    }

    private boolean testImapConnection(TenantConfig config) {
        try {
            var props = new java.util.Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", config.getImapHost());
            props.put("mail.imaps.port", String.valueOf(config.getImapPort()));
            props.put("mail.imaps.ssl.enable", String.valueOf(Boolean.TRUE.equals(config.getImapUseTls())));

            var session = jakarta.mail.Session.getInstance(props);
            var store = session.getStore("imaps");
            store.connect(config.getImapHost(), config.getImapUsername(), config.getImapPassword());
            store.close();
            return true;
        } catch (Exception e) {
            log.warn("IMAP connection test failed: {}", e.getMessage());
            return false;
        }
    }
}
