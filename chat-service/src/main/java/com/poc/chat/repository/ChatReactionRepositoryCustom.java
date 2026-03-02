package com.poc.chat.repository;

import com.poc.chat.domain.ChatReaction;

import java.util.List;

public interface ChatReactionRepositoryCustom {

    List<ChatReaction> findByMessageIdIn(List<Long> messageIds);
}
