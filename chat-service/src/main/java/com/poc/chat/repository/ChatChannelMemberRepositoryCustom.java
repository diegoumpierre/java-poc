package com.poc.chat.repository;

import com.poc.chat.domain.ChatChannelMember;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChatChannelMemberRepositoryCustom {

    List<ChatChannelMember> findByChannelId(Long channelId);

    Optional<ChatChannelMember> findByChannelIdAndUserId(Long channelId, Long userId);

    boolean isMember(Long channelId, Long userId);

    int countByChannelId(Long channelId);

    void deleteByChannelIdAndUserId(Long channelId, Long userId);

    void updateLastReadAt(Long channelId, Long userId, Instant lastReadAt);

    List<Long> findUserIdsByChannelId(Long channelId);

    List<Long> findChannelIdsByUserId(Long userId);
}
