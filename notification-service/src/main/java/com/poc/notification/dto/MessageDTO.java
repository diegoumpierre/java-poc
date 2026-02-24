package com.poc.notification.dto;

import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.InboundMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private String direction;
    private String senderUserId;
    private String fromEmail;
    private String fromName;
    private String recipientEmail;
    private String subject;
    private String contentText;
    private String contentHtml;
    private String status;
    private Boolean hasAttachments;
    private Instant timestamp;

    public static MessageDTO fromInbound(InboundMessage msg) {
        return MessageDTO.builder()
                .id(msg.getId())
                .direction("INBOUND")
                .fromEmail(msg.getFromEmail())
                .fromName(msg.getFromName())
                .subject(msg.getSubject())
                .contentText(msg.getContentText())
                .contentHtml(msg.getContentHtml())
                .hasAttachments(msg.getHasAttachments())
                .status("RECEIVED")
                .timestamp(msg.getReceivedAt())
                .build();
    }

    public static MessageDTO fromOutbound(EmailHistory msg) {
        return MessageDTO.builder()
                .id(msg.getId())
                .direction("OUTBOUND")
                .senderUserId(msg.getUserId())
                .recipientEmail(msg.getRecipient())
                .subject(msg.getSubject())
                .contentText(msg.getContentText())
                .contentHtml(msg.getContentHtml())
                .status(msg.getStatus())
                .hasAttachments(false)
                .timestamp(msg.getSentAt() != null ? msg.getSentAt() : msg.getCreatedAt())
                .build();
    }
}
