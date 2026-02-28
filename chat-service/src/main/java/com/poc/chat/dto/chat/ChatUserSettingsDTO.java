package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatUserSettings;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserSettingsDTO {
    private Boolean notifyDm;
    private Boolean notifyMention;
    private Boolean notifyChannelMessages;
    private Boolean notifySound;
    private Boolean notifyDesktop;

    public static ChatUserSettingsDTO fromEntity(ChatUserSettings entity) {
        return ChatUserSettingsDTO.builder()
                .notifyDm(entity.getNotifyDm())
                .notifyMention(entity.getNotifyMention())
                .notifyChannelMessages(entity.getNotifyChannelMessages())
                .notifySound(entity.getNotifySound())
                .notifyDesktop(entity.getNotifyDesktop())
                .build();
    }

    public static ChatUserSettingsDTO defaults() {
        return ChatUserSettingsDTO.builder()
                .notifyDm(true)
                .notifyMention(true)
                .notifyChannelMessages(false)
                .notifySound(true)
                .notifyDesktop(true)
                .build();
    }
}
