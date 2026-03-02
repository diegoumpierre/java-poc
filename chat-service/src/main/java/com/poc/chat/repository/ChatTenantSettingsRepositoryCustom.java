package com.poc.chat.repository;

import com.poc.chat.domain.ChatTenantSettings;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatTenantSettingsRepositoryCustom {

    Optional<ChatTenantSettings> findByTenantId(UUID tenantId);

    List<ChatTenantSettings> findAllWithRetention();
}
