package com.poc.chat.dto.livechat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendLiveChatMessageRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @Builder.Default
    private String messageType = "TEXT";

    private String attachmentUrl;
    private String attachmentName;
}
