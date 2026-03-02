package com.poc.chat.repository;

import com.poc.chat.domain.ChatReaction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatReactionRepositoryCustomImpl implements ChatReactionRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public List<ChatReaction> findByMessageIdIn(List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = String.join(",", messageIds.stream().map(String::valueOf).toList());
        return jdbcClient.sql("SELECT * FROM CHAT_REACTION WHERE MESSAGE_ID IN (" + placeholders + ") ORDER BY CREATED_AT ASC")
                .query((rs, rowNum) -> ChatReaction.builder()
                        .id(rs.getLong("ID"))
                        .messageId(rs.getLong("MESSAGE_ID"))
                        .userId(rs.getLong("USER_ID"))
                        .emoji(rs.getString("EMOJI"))
                        .createdAt(rs.getTimestamp("CREATED_AT") != null ? rs.getTimestamp("CREATED_AT").toInstant() : null)
                        .build())
                .list();
    }
}
