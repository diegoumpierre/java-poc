package com.poc.chat.repository;

import com.poc.chat.domain.ChatChannelMember;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatChannelMemberRepositoryCustomImpl implements ChatChannelMemberRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public List<ChatChannelMember> findByChannelId(Long channelId) {
        return jdbcClient.sql("SELECT * FROM CHAT_CHANNEL_MEMBER WHERE CHANNEL_ID = :channelId ORDER BY JOINED_AT ASC")
                .param("channelId", channelId)
                .query(ChatChannelMember.class)
                .list();
    }

    @Override
    public Optional<ChatChannelMember> findByChannelIdAndUserId(Long channelId, Long userId) {
        return jdbcClient.sql("SELECT * FROM CHAT_CHANNEL_MEMBER WHERE CHANNEL_ID = :channelId AND USER_ID = :userId")
                .param("channelId", channelId)
                .param("userId", userId)
                .query(ChatChannelMember.class)
                .optional();
    }

    @Override
    public boolean isMember(Long channelId, Long userId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM CHAT_CHANNEL_MEMBER WHERE CHANNEL_ID = :channelId AND USER_ID = :userId")
                .param("channelId", channelId)
                .param("userId", userId)
                .query(Integer.class)
                .single() > 0;
    }

    @Override
    public int countByChannelId(Long channelId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM CHAT_CHANNEL_MEMBER WHERE CHANNEL_ID = :channelId")
                .param("channelId", channelId)
                .query(Integer.class)
                .single();
    }

    @Override
    public void deleteByChannelIdAndUserId(Long channelId, Long userId) {
        jdbcClient.sql("DELETE FROM CHAT_CHANNEL_MEMBER WHERE CHANNEL_ID = :channelId AND USER_ID = :userId")
                .param("channelId", channelId)
                .param("userId", userId)
                .update();
    }

    @Override
    public void updateLastReadAt(Long channelId, Long userId, Instant lastReadAt) {
        jdbcClient.sql("UPDATE CHAT_CHANNEL_MEMBER SET LAST_READ_AT = :lastReadAt WHERE CHANNEL_ID = :channelId AND USER_ID = :userId")
                .param("channelId", channelId)
                .param("userId", userId)
                .param("lastReadAt", lastReadAt)
                .update();
    }

    @Override
    public List<Long> findUserIdsByChannelId(Long channelId) {
        return jdbcClient.sql("SELECT USER_ID FROM CHAT_CHANNEL_MEMBER WHERE CHANNEL_ID = :channelId")
                .param("channelId", channelId)
                .query(Long.class)
                .list();
    }

    @Override
    public List<Long> findChannelIdsByUserId(Long userId) {
        return jdbcClient.sql("SELECT CHANNEL_ID FROM CHAT_CHANNEL_MEMBER WHERE USER_ID = :userId")
                .param("userId", userId)
                .query(Long.class)
                .list();
    }
}
