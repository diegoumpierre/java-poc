package com.poc.chat.repository;

import com.poc.chat.domain.ChatMention;

import java.util.List;

public interface ChatMentionRepositoryCustom {

    List<ChatMention> findByMessageIdIn(List<Long> messageIds);
}
