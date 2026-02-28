package com.poc.chat.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatReactionDTO {
    private String emoji;
    private int count;
    private boolean userReacted;
}
