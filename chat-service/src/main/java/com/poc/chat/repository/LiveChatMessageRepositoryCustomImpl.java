package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LiveChatMessageRepositoryCustomImpl implements LiveChatMessageRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public List<LiveChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId) {
        return jdbcClient.sql("""
                SELECT * FROM CHAT_LIVECHAT_MESSAGE
                WHERE SESSION_ID = :sessionId
                ORDER BY CREATED_AT ASC
                """)
                .param("sessionId", sessionId)
                .query(LiveChatMessage.class)
                .list();
    }

    @Override
    public List<LiveChatMessage> findBySessionIdSince(Long sessionId, Instant since) {
        return jdbcClient.sql("""
                SELECT * FROM CHAT_LIVECHAT_MESSAGE
                WHERE SESSION_ID = :sessionId AND CREATED_AT > :since
                ORDER BY CREATED_AT ASC
                """)
                .param("sessionId", sessionId)
                .param("since", since)
                .query(LiveChatMessage.class)
                .list();
    }

    @Override
    public void markAsReadBySenderType(Long sessionId, String senderType, Instant readAt) {
        jdbcClient.sql("""
                UPDATE CHAT_LIVECHAT_MESSAGE
                SET IS_READ = 1, READ_AT = :readAt
                WHERE SESSION_ID = :sessionId AND SENDER_TYPE = :senderType AND IS_READ = 0
                """)
                .param("sessionId", sessionId)
                .param("senderType", senderType)
                .param("readAt", readAt)
                .update();
    }
}
