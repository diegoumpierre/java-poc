package com.poc.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("CHAT_ATTACHMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatAttachment {

    @Id
    @Column("ID")
    private Long id;

    @Column("MESSAGE_ID")
    private Long messageId;

    @Column("FILE_NAME")
    private String fileName;

    @Column("FILE_SIZE")
    private Long fileSize;

    @Column("MIME_TYPE")
    private String mimeType;

    @Column("STORAGE_KEY")
    private String storageKey;

    @Column("THUMBNAIL_URL")
    private String thumbnailUrl;

    @Column("CREATED_AT")
    private Instant createdAt;

    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }
}
