package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatWidgetConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LiveChatWidgetConfigRepositoryCustom {

    Optional<LiveChatWidgetConfig> findByTenantId(UUID tenantId);

    Optional<LiveChatWidgetConfig> findByTenantIdAndSourceService(UUID tenantId, String sourceService);

    Optional<LiveChatWidgetConfig> findDefaultByTenantId(UUID tenantId);

    List<LiveChatWidgetConfig> findAllByTenantId(UUID tenantId);
}
