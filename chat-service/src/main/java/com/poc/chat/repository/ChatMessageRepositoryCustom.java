package com.poc.chat.repository;

import java.util.UUID;

public interface ChatMessageRepositoryCustom {

    long countByConversationId(Long conversationId);
}
