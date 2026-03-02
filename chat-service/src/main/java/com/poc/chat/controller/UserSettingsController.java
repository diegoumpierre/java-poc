package com.poc.chat.controller;

import com.poc.chat.domain.ChatUser;
import com.poc.chat.domain.ChatUserSettings;
import com.poc.chat.dto.chat.ChatUserSettingsDTO;
import com.poc.chat.repository.ChatUserRepository;
import com.poc.chat.repository.ChatUserSettingsRepository;
import com.poc.shared.security.RequiresPermission;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/user-settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final ChatUserSettingsRepository settingsRepository;
    private final ChatUserRepository chatUserRepository;

    @GetMapping
    public ResponseEntity<ChatUserSettingsDTO> getSettings() {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) return ResponseEntity.notFound().build();

        return settingsRepository.findByUserId(currentUser.getId())
                .map(settings -> ResponseEntity.ok(ChatUserSettingsDTO.fromEntity(settings)))
                .orElseGet(() -> ResponseEntity.ok(ChatUserSettingsDTO.defaults()));
    }

    @PutMapping
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<ChatUserSettingsDTO> updateSettings(@RequestBody ChatUserSettingsDTO dto) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) return ResponseEntity.notFound().build();

        Instant now = Instant.now();
        ChatUserSettings settings = settingsRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> ChatUserSettings.builder()
                        .userId(currentUser.getId())
                        .notifyDm(true)
                        .notifyMention(true)
                        .notifyChannelMessages(false)
                        .notifySound(true)
                        .notifyDesktop(true)
                        .createdAt(now)
                        .build());

        if (dto.getNotifyDm() != null) settings.setNotifyDm(dto.getNotifyDm());
        if (dto.getNotifyMention() != null) settings.setNotifyMention(dto.getNotifyMention());
        if (dto.getNotifyChannelMessages() != null) settings.setNotifyChannelMessages(dto.getNotifyChannelMessages());
        if (dto.getNotifySound() != null) settings.setNotifySound(dto.getNotifySound());
        if (dto.getNotifyDesktop() != null) settings.setNotifyDesktop(dto.getNotifyDesktop());
        settings.setUpdatedAt(now);

        ChatUserSettings saved = settingsRepository.save(settings);
        return ResponseEntity.ok(ChatUserSettingsDTO.fromEntity(saved));
    }

    private ChatUser getCurrentUser() {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID externalUserId = TenantContext.getCurrentUser();
        return chatUserRepository.findByExternalUserIdAndTenantId(externalUserId, tenantId).orElse(null);
    }
}
