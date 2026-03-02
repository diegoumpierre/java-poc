package com.poc.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryCustomImpl implements ChatMessageRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public long countByConversationId(Long conversationId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM CHAT_MESSAGE WHERE CONVERSATION_ID = :conversationId")
                .param("conversationId", conversationId)
                .query(Long.class)
                .single();
    }
}
