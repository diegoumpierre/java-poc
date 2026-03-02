package com.poc.chat.repository;

import com.poc.chat.domain.ChatAttachment;

import java.util.List;

public interface ChatAttachmentRepositoryCustom {

    List<ChatAttachment> findByMessageIdIn(List<Long> messageIds);

    List<ChatAttachment> findByChannelId(Long channelId, int offset, int limit);
}
