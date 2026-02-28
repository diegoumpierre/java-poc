package com.poc.chat.dto.chat;

import com.poc.chat.domain.ChatTenantSettings;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantSettingsDTO {

    private Integer messageRetentionDays;
    private Integer maxFileSizeMb;
    private Boolean allowPublicChannels;
    private Boolean allowFileUploads;

    public static TenantSettingsDTO fromEntity(ChatTenantSettings settings) {
        return TenantSettingsDTO.builder()
                .messageRetentionDays(settings.getMessageRetentionDays())
                .maxFileSizeMb(settings.getMaxFileSizeMb())
                .allowPublicChannels(settings.getAllowPublicChannels())
                .allowFileUploads(settings.getAllowFileUploads())
                .build();
    }
}
