package com.poc.chat.repository;

import com.poc.chat.domain.ChatNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChatNotificationRepositoryCustomImpl implements ChatNotificationRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public List<ChatNotification> findByUserId(Long userId, int offset, int limit) {
        return jdbcClient.sql("""
                SELECT ID, TENANT_ID, USER_ID, TYPE, CHANNEL_ID, MESSAGE_ID, SENDER_ID,
                       TITLE, BODY, IS_READ, CREATED_AT
                FROM CHAT_NOTIFICATION
                WHERE USER_ID = :userId
                ORDER BY CREATED_AT DESC
                LIMIT :limit OFFSET :offset
                """)
                .param("userId", userId)
                .param("offset", offset)
                .param("limit", limit)
                .query((rs, rowNum) -> ChatNotification.builder()
                        .id(rs.getLong("ID"))
                        .tenantId(UUID.fromString(rs.getString("TENANT_ID")))
                        .userId(rs.getLong("USER_ID"))
                        .type(rs.getString("TYPE"))
                        .channelId(rs.getObject("CHANNEL_ID") != null ? rs.getLong("CHANNEL_ID") : null)
                        .messageId(rs.getObject("MESSAGE_ID") != null ? rs.getLong("MESSAGE_ID") : null)
                        .senderId(rs.getObject("SENDER_ID") != null ? rs.getLong("SENDER_ID") : null)
                        .title(rs.getString("TITLE"))
                        .body(rs.getString("BODY"))
                        .isRead(rs.getBoolean("IS_READ"))
                        .createdAt(rs.getTimestamp("CREATED_AT").toInstant())
                        .build())
                .list();
    }
}
