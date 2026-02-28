package com.poc.chat.dto.chat;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    private Long messageId;
    private Long channelId;
    private String channelName;
    private String channelType;
    private String senderName;
    private String senderAvatarUrl;
    private String text;
    private Instant createdAt;
    private Long parentMessageId;
}
