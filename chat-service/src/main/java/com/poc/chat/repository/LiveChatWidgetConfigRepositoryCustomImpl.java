package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatWidgetConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LiveChatWidgetConfigRepositoryCustomImpl implements LiveChatWidgetConfigRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public Optional<LiveChatWidgetConfig> findByTenantId(UUID tenantId) {
        return jdbcClient.sql("SELECT * FROM CHAT_LIVECHAT_WIDGET_CONFIG WHERE TENANT_ID = :tenantId AND SOURCE_SERVICE IS NULL")
                .param("tenantId", tenantId.toString())
                .query(LiveChatWidgetConfig.class)
                .optional();
    }

    @Override
    public Optional<LiveChatWidgetConfig> findByTenantIdAndSourceService(UUID tenantId, String sourceService) {
        return jdbcClient.sql("SELECT * FROM CHAT_LIVECHAT_WIDGET_CONFIG WHERE TENANT_ID = :tenantId AND SOURCE_SERVICE = :sourceService")
                .param("tenantId", tenantId.toString())
                .param("sourceService", sourceService)
                .query(LiveChatWidgetConfig.class)
                .optional();
    }

    @Override
    public Optional<LiveChatWidgetConfig> findDefaultByTenantId(UUID tenantId) {
        return jdbcClient.sql("SELECT * FROM CHAT_LIVECHAT_WIDGET_CONFIG WHERE TENANT_ID = :tenantId AND SOURCE_SERVICE IS NULL")
                .param("tenantId", tenantId.toString())
                .query(LiveChatWidgetConfig.class)
                .optional();
    }

    @Override
    public List<LiveChatWidgetConfig> findAllByTenantId(UUID tenantId) {
        return jdbcClient.sql("SELECT * FROM CHAT_LIVECHAT_WIDGET_CONFIG WHERE TENANT_ID = :tenantId ORDER BY SOURCE_SERVICE ASC")
                .param("tenantId", tenantId.toString())
                .query(LiveChatWidgetConfig.class)
                .list();
    }
}
