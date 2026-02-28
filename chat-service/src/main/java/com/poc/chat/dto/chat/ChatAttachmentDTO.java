package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatAttachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAttachmentDTO {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String url;
    private String thumbnailUrl;
    private Instant createdAt;

    public static ChatAttachmentDTO fromEntity(ChatAttachment attachment, String baseUrl) {
        return ChatAttachmentDTO.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileSize(attachment.getFileSize())
                .mimeType(attachment.getMimeType())
                .url(baseUrl + "/api/chat/attachments/" + attachment.getId() + "/download")
                .thumbnailUrl(attachment.getThumbnailUrl())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
