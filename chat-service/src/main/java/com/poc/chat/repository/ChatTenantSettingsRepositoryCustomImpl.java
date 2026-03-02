package com.poc.chat.repository;

import com.poc.chat.domain.ChatTenantSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChatTenantSettingsRepositoryCustomImpl implements ChatTenantSettingsRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public Optional<ChatTenantSettings> findByTenantId(UUID tenantId) {
        return jdbcClient.sql("SELECT * FROM CHAT_TENANT_SETTINGS WHERE TENANT_ID = :tenantId")
                .param("tenantId", tenantId.toString())
                .query(ChatTenantSettings.class)
                .optional();
    }

    @Override
    public List<ChatTenantSettings> findAllWithRetention() {
        return jdbcClient.sql("SELECT * FROM CHAT_TENANT_SETTINGS WHERE MESSAGE_RETENTION_DAYS > 0")
                .query(ChatTenantSettings.class)
                .list();
    }
}
