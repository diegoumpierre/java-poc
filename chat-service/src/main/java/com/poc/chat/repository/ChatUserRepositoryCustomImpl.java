package com.poc.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChatUserRepositoryCustomImpl implements ChatUserRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public long countByTenantId(UUID tenantId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM CHAT_USER WHERE TENANT_ID = :tenantId")
                .param("tenantId", tenantId.toString())
                .query(Long.class)
                .single();
    }
}
