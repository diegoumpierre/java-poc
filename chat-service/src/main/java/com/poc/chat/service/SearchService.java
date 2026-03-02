package com.poc.chat.service;

import com.poc.chat.dto.chat.SearchResultDTO;
import com.poc.chat.repository.ChatChannelMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final JdbcClient jdbcClient;
    private final ChatChannelMemberRepository channelMemberRepository;
    private final ChatService chatService;

    @Transactional(readOnly = true)
    public List<SearchResultDTO> search(String query, Long channelId, int offset, int limit) {
        var currentUser = chatService.getCurrentChatUser().orElse(null);
        if (currentUser == null) return Collections.emptyList();

        // Get channels the user is a member of
        List<Long> memberChannelIds = channelMemberRepository.findChannelIdsByUserId(currentUser.getId());
        if (memberChannelIds.isEmpty()) return Collections.emptyList();

        // If channelId specified, verify membership
        if (channelId != null) {
            if (!memberChannelIds.contains(channelId)) return Collections.emptyList();
            memberChannelIds = List.of(channelId);
        }

        // Build the search query using LIKE for portability (FULLTEXT is MySQL-only optimization)
        String searchPattern = "%" + query.replace("%", "\\%").replace("_", "\\_") + "%";

        StringBuilder sql = new StringBuilder("""
                SELECT m.ID AS MESSAGE_ID, m.CHANNEL_ID, m.TEXT, m.CREATED_AT, m.PARENT_MESSAGE_ID,
                       c.NAME AS CHANNEL_NAME, c.TYPE AS CHANNEL_TYPE,
                       u.NAME AS SENDER_NAME, u.AVATAR_URL AS SENDER_AVATAR_URL
                FROM CHAT_MESSAGE m
                JOIN CHAT_CHANNEL c ON m.CHANNEL_ID = c.ID
                JOIN CHAT_USER u ON m.SENDER_ID = u.ID
                WHERE m.CHANNEL_ID IN (:channelIds)
                AND m.TEXT LIKE :searchPattern
                ORDER BY m.CREATED_AT DESC
                LIMIT :limit OFFSET :offset
                """);

        return jdbcClient.sql(sql.toString())
                .param("channelIds", memberChannelIds)
                .param("searchPattern", searchPattern)
                .param("offset", offset)
                .param("limit", limit)
                .query((rs, rowNum) -> SearchResultDTO.builder()
                        .messageId(rs.getLong("MESSAGE_ID"))
                        .channelId(rs.getLong("CHANNEL_ID"))
                        .channelName(rs.getString("CHANNEL_NAME"))
                        .channelType(rs.getString("CHANNEL_TYPE"))
                        .senderName(rs.getString("SENDER_NAME"))
                        .senderAvatarUrl(rs.getString("SENDER_AVATAR_URL"))
                        .text(rs.getString("TEXT"))
                        .createdAt(rs.getTimestamp("CREATED_AT").toInstant())
                        .parentMessageId(rs.getObject("PARENT_MESSAGE_ID") != null ? rs.getLong("PARENT_MESSAGE_ID") : null)
                        .build())
                .list();
    }
}
