package com.poc.chat.repository;

import com.poc.chat.domain.ChatChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChatChannelRepositoryCustomImpl implements ChatChannelRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public List<ChatChannel> findByTenantIdAndUserId(UUID tenantId, Long userId) {
        return jdbcClient.sql("""
                SELECT c.* FROM CHAT_CHANNEL c
                JOIN CHAT_CHANNEL_MEMBER cm ON cm.CHANNEL_ID = c.ID
                WHERE c.TENANT_ID = :tenantId
                AND cm.USER_ID = :userId
                AND (c.IS_ARCHIVED = 0 OR c.IS_ARCHIVED IS NULL)
                ORDER BY c.LAST_MESSAGE_AT DESC NULLS LAST, c.CREATED_AT DESC
                """)
                .param("tenantId", tenantId.toString())
                .param("userId", userId)
                .query(ChatChannel.class)
                .list();
    }

    @Override
    public List<ChatChannel> findPublicByTenantId(UUID tenantId) {
        return jdbcClient.sql("""
                SELECT * FROM CHAT_CHANNEL
                WHERE TENANT_ID = :tenantId
                AND TYPE = 'PUBLIC'
                AND (IS_ARCHIVED = 0 OR IS_ARCHIVED IS NULL)
                ORDER BY NAME ASC
                """)
                .param("tenantId", tenantId.toString())
                .query(ChatChannel.class)
                .list();
    }

    @Override
    public Optional<ChatChannel> findByTenantIdAndSlug(UUID tenantId, String slug) {
        return jdbcClient.sql("""
                SELECT * FROM CHAT_CHANNEL
                WHERE TENANT_ID = :tenantId AND SLUG = :slug
                """)
                .param("tenantId", tenantId.toString())
                .param("slug", slug)
                .query(ChatChannel.class)
                .optional();
    }

    @Override
    public Optional<ChatChannel> findDmBetweenUsers(UUID tenantId, Long userOneId, Long userTwoId) {
        return jdbcClient.sql("""
                SELECT c.* FROM CHAT_CHANNEL c
                JOIN CHAT_CHANNEL_MEMBER cm1 ON cm1.CHANNEL_ID = c.ID AND cm1.USER_ID = :userOneId
                JOIN CHAT_CHANNEL_MEMBER cm2 ON cm2.CHANNEL_ID = c.ID AND cm2.USER_ID = :userTwoId
                WHERE c.TENANT_ID = :tenantId AND c.TYPE = 'DM'
                LIMIT 1
                """)
                .param("tenantId", tenantId.toString())
                .param("userOneId", userOneId)
                .param("userTwoId", userTwoId)
                .query(ChatChannel.class)
                .optional();
    }

    @Override
    public void updateLastMessageAt(Long channelId, Instant lastMessageAt, Instant updatedAt) {
        jdbcClient.sql("""
                UPDATE CHAT_CHANNEL
                SET LAST_MESSAGE_AT = :lastMessageAt, UPDATED_AT = :updatedAt
                WHERE ID = :channelId
                """)
                .param("channelId", channelId)
                .param("lastMessageAt", lastMessageAt)
                .param("updatedAt", updatedAt)
                .update();
    }

    @Override
    public void archiveChannel(Long channelId, Instant updatedAt) {
        jdbcClient.sql("""
                UPDATE CHAT_CHANNEL SET IS_ARCHIVED = 1, UPDATED_AT = :updatedAt WHERE ID = :channelId
                """)
                .param("channelId", channelId)
                .param("updatedAt", updatedAt)
                .update();
    }
}
