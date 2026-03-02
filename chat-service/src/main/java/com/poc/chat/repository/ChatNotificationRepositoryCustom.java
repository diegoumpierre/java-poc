package com.poc.chat.repository;

import com.poc.chat.domain.ChatNotification;

import java.util.List;

public interface ChatNotificationRepositoryCustom {
    List<ChatNotification> findByUserId(Long userId, int offset, int limit);
}
