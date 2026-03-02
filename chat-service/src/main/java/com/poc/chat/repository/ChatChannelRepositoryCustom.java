package com.poc.chat.repository;

import com.poc.chat.domain.ChatChannel;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatChannelRepositoryCustom {

    List<ChatChannel> findByTenantIdAndUserId(UUID tenantId, Long userId);

    List<ChatChannel> findPublicByTenantId(UUID tenantId);

    Optional<ChatChannel> findByTenantIdAndSlug(UUID tenantId, String slug);

    Optional<ChatChannel> findDmBetweenUsers(UUID tenantId, Long userOneId, Long userTwoId);

    void updateLastMessageAt(Long channelId, Instant lastMessageAt, Instant updatedAt);

    void archiveChannel(Long channelId, Instant updatedAt);
}
