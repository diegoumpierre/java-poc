package com.poc.chat.repository;

import com.poc.chat.domain.ChatAttachment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatAttachmentRepositoryCustomImpl implements ChatAttachmentRepositoryCustom {

    private final JdbcClient jdbcClient;

    @Override
    public List<ChatAttachment> findByMessageIdIn(List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) return Collections.emptyList();

        String placeholders = String.join(",", messageIds.stream().map(String::valueOf).toList());
        return jdbcClient.sql("SELECT * FROM CHAT_ATTACHMENT WHERE MESSAGE_ID IN (" + placeholders + ")")
                .query((rs, rowNum) -> ChatAttachment.builder()
                        .id(rs.getLong("ID"))
                        .messageId(rs.getLong("MESSAGE_ID"))
                        .fileName(rs.getString("FILE_NAME"))
                        .fileSize(rs.getLong("FILE_SIZE"))
                        .mimeType(rs.getString("MIME_TYPE"))
                        .storageKey(rs.getString("STORAGE_KEY"))
                        .thumbnailUrl(rs.getString("THUMBNAIL_URL"))
                        .createdAt(rs.getTimestamp("CREATED_AT") != null ? rs.getTimestamp("CREATED_AT").toInstant() : null)
                        .build())
                .list();
    }

    @Override
    public List<ChatAttachment> findByChannelId(Long channelId, int offset, int limit) {
        return jdbcClient.sql("""
                SELECT a.* FROM CHAT_ATTACHMENT a
                JOIN CHAT_MESSAGE m ON a.MESSAGE_ID = m.ID
                WHERE m.CHANNEL_ID = :channelId
                ORDER BY a.CREATED_AT DESC
                LIMIT :limit OFFSET :offset
                """)
                .param("channelId", channelId)
                .param("offset", offset)
                .param("limit", limit)
                .query((rs, rowNum) -> ChatAttachment.builder()
                        .id(rs.getLong("ID"))
                        .messageId(rs.getLong("MESSAGE_ID"))
                        .fileName(rs.getString("FILE_NAME"))
                        .fileSize(rs.getLong("FILE_SIZE"))
                        .mimeType(rs.getString("MIME_TYPE"))
                        .storageKey(rs.getString("STORAGE_KEY"))
                        .thumbnailUrl(rs.getString("THUMBNAIL_URL"))
                        .createdAt(rs.getTimestamp("CREATED_AT") != null ? rs.getTimestamp("CREATED_AT").toInstant() : null)
                        .build())
                .list();
    }
}
