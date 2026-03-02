package com.poc.chat.service;

import com.poc.chat.domain.ChatTenantSettings;
import com.poc.chat.dto.chat.TenantSettingsDTO;
import com.poc.chat.repository.ChatTenantSettingsRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantSettingsService {

    private final ChatTenantSettingsRepository settingsRepository;

    @Transactional(readOnly = true)
    public TenantSettingsDTO getSettings() {
        UUID tenantId = TenantContext.getCurrentTenant();
        return settingsRepository.findByTenantId(tenantId)
                .map(TenantSettingsDTO::fromEntity)
                .orElse(TenantSettingsDTO.builder()
                        .messageRetentionDays(365)
                        .maxFileSizeMb(50)
                        .allowPublicChannels(true)
                        .allowFileUploads(true)
                        .build());
    }

    @Transactional
    public TenantSettingsDTO updateSettings(TenantSettingsDTO request) {
        UUID tenantId = TenantContext.getCurrentTenant();
        Instant now = Instant.now();

        ChatTenantSettings settings = settingsRepository.findByTenantId(tenantId)
                .orElseGet(() -> ChatTenantSettings.builder()
                        .tenantId(tenantId)
                        .createdAt(now)
                        .build());

        if (request.getMessageRetentionDays() != null) settings.setMessageRetentionDays(request.getMessageRetentionDays());
        if (request.getMaxFileSizeMb() != null) settings.setMaxFileSizeMb(request.getMaxFileSizeMb());
        if (request.getAllowPublicChannels() != null) settings.setAllowPublicChannels(request.getAllowPublicChannels());
        if (request.getAllowFileUploads() != null) settings.setAllowFileUploads(request.getAllowFileUploads());
        settings.setUpdatedAt(now);

        ChatTenantSettings saved = settingsRepository.save(settings);
        log.info("Tenant settings updated: tenantId={}, retention={}d", tenantId, saved.getMessageRetentionDays());
        return TenantSettingsDTO.fromEntity(saved);
    }
}
