package com.poc.chat.repository;

import com.poc.chat.domain.LiveChatMessage;

import java.time.Instant;
import java.util.List;

public interface LiveChatMessageRepositoryCustom {

    List<LiveChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    List<LiveChatMessage> findBySessionIdSince(Long sessionId, Instant since);

    void markAsReadBySenderType(Long sessionId, String senderType, Instant readAt);
}
