package com.poc.notification.dto;

import com.poc.notification.domain.Conversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {

    private Long id;
    private String contactEmail;
    private String contactName;
    private String subject;
    private String threadId;
    private Instant lastMessageAt;
    private String lastMessagePreview;
    private Integer unreadCount;
    private String status;

    public static ConversationDTO fromEntity(Conversation c) {
        return ConversationDTO.builder()
                .id(c.getId())
                .contactEmail(c.getContactEmail())
                .contactName(c.getContactName())
                .subject(c.getSubject())
                .threadId(c.getThreadId())
                .lastMessageAt(c.getLastMessageAt())
                .lastMessagePreview(c.getLastMessagePreview())
                .unreadCount(c.getUnreadCount())
                .status(c.getStatus())
                .build();
    }
}
