package com.poc.chat.repository;

import com.poc.chat.domain.ChatMention;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMentionRepositoryCustomImpl implements ChatMentionRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public List<ChatMention> findByMessageIdIn(List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) return Collections.emptyList();

        String placeholders = String.join(",", messageIds.stream().map(String::valueOf).toList());
        return jdbcClient.sql("SELECT * FROM CHAT_MENTION WHERE MESSAGE_ID IN (" + placeholders + ")")
                .query((rs, rowNum) -> ChatMention.builder()
                        .id(rs.getLong("ID"))
                        .messageId(rs.getLong("MESSAGE_ID"))
                        .mentionedUserId(rs.getObject("MENTIONED_USER_ID") != null ? rs.getLong("MENTIONED_USER_ID") : null)
                        .mentionType(rs.getString("MENTION_TYPE"))
                        .createdAt(rs.getTimestamp("CREATED_AT") != null ? rs.getTimestamp("CREATED_AT").toInstant() : null)
                        .build())
                .list();
    }
}
