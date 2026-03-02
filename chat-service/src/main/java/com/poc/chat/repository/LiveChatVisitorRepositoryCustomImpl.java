package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LiveChatVisitorRepositoryCustomImpl implements LiveChatVisitorRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public Optional<LiveChatVisitor> findByTenantIdAndVisitorId(UUID tenantId, String visitorId) {
        return jdbcClient.sql("""
                SELECT * FROM CHAT_LIVECHAT_VISITOR
                WHERE TENANT_ID = :tenantId AND VISITOR_ID = :visitorId
                """)
                .param("tenantId", tenantId.toString())
                .param("visitorId", visitorId)
                .query(LiveChatVisitor.class)
                .optional();
    }
}
