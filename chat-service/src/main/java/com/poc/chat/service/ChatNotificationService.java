package com.poc.chat.service;

import com.poc.shared.tenant.TenantContext;
import com.poc.chat.domain.*;
import com.poc.chat.dto.chat.ChatNotificationDTO;
import com.poc.chat.dto.chat.ChatUserDTO;
import com.poc.chat.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatNotificationService {

    private final ChatNotificationRepository notificationRepository;
    private final ChatUserSettingsRepository userSettingsRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatChannelRepository channelRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void createMentionNotification(Long mentionedUserId, Long senderId, Long channelId, Long messageId, String messagePreview) {
        // Check user settings
        ChatUserSettings settings = userSettingsRepository.findByUserId(mentionedUserId).orElse(null);
        if (settings != null && !Boolean.TRUE.equals(settings.getNotifyMention())) return;

        ChatUser sender = chatUserRepository.findById(senderId).orElse(null);
        String senderName = sender != null ? sender.getName() : "Someone";
        String channelName = channelRepository.findById(channelId)
                .map(ch -> ch.getName() != null ? ch.getName() : "DM")
                .orElse("Unknown");

        String title = senderName + " mentioned you in #" + channelName;
        String body = messagePreview.length() > 100 ? messagePreview.substring(0, 100) + "..." : messagePreview;

        createAndBroadcast(mentionedUserId, NotificationType.MENTION, channelId, messageId, senderId, title, body);
    }

    @Transactional
    public void createDmNotification(Long recipientUserId, Long senderId, Long channelId, Long messageId, String messagePreview) {
        // Check user settings
        ChatUserSettings settings = userSettingsRepository.findByUserId(recipientUserId).orElse(null);
        if (settings != null && !Boolean.TRUE.equals(settings.getNotifyDm())) return;

        ChatUser sender = chatUserRepository.findById(senderId).orElse(null);
        String senderName = sender != null ? sender.getName() : "Someone";

        String title = "New message from " + senderName;
        String body = messagePreview.length() > 100 ? messagePreview.substring(0, 100) + "..." : messagePreview;

        createAndBroadcast(recipientUserId, NotificationType.DM, channelId, messageId, senderId, title, body);
    }

    @Transactional
    public void createReactionNotification(Long messageOwnerId, Long reactorId, Long channelId, Long messageId, String emoji) {
        // Don't notify self-reactions
        if (messageOwnerId.equals(reactorId)) return;

        ChatUser reactor = chatUserRepository.findById(reactorId).orElse(null);
        String reactorName = reactor != null ? reactor.getName() : "Someone";

        String title = reactorName + " reacted " + emoji + " to your message";

        createAndBroadcast(messageOwnerId, NotificationType.REACTION, channelId, messageId, reactorId, title, null);
    }

    @Transactional(readOnly = true)
    public List<ChatNotificationDTO> getNotifications(int offset, int limit) {
        ChatUser currentUser = getCurrentChatUser();
        if (currentUser == null) return Collections.emptyList();

        List<ChatNotification> notifications = notificationRepository.findByUserId(currentUser.getId(), offset, limit);

        Map<Long, ChatUserDTO> userCache = new HashMap<>();
        Map<Long, String> channelNameCache = new HashMap<>();

        return notifications.stream()
                .map(notif -> {
                    ChatUserDTO sender = notif.getSenderId() != null
                            ? userCache.computeIfAbsent(notif.getSenderId(),
                                id -> chatUserRepository.findById(id).map(ChatUserDTO::fromEntity).orElse(null))
                            : null;
                    String channelName = notif.getChannelId() != null
                            ? channelNameCache.computeIfAbsent(notif.getChannelId(),
                                id -> channelRepository.findById(id).map(ch -> ch.getName() != null ? ch.getName() : "DM").orElse(null))
                            : null;
                    return ChatNotificationDTO.fromEntity(notif, sender, channelName);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public int getUnreadCount() {
        ChatUser currentUser = getCurrentChatUser();
        if (currentUser == null) return 0;
        return notificationRepository.countUnread(currentUser.getId());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        ChatUser currentUser = getCurrentChatUser();
        if (currentUser == null) return;
        notificationRepository.markAsRead(notificationId, currentUser.getId());
    }

    @Transactional
    public void markAllAsRead() {
        ChatUser currentUser = getCurrentChatUser();
        if (currentUser == null) return;
        notificationRepository.markAllAsRead(currentUser.getId());
    }

    // ==================== Internal ====================

    private ChatUser getCurrentChatUser() {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID externalUserId = TenantContext.getCurrentUser();
        return chatUserRepository.findByExternalUserIdAndTenantId(externalUserId, tenantId).orElse(null);
    }

    private void createAndBroadcast(Long userId, NotificationType type, Long channelId, Long messageId, Long senderId, String title, String body) {
        UUID tenantId = TenantContext.getCurrentTenant();

        ChatNotification notification = ChatNotification.builder()
                .tenantId(tenantId)
                .userId(userId)
                .type(type.name())
                .channelId(channelId)
                .messageId(messageId)
                .senderId(senderId)
                .title(title)
                .body(body)
                .isRead(false)
                .createdAt(Instant.now())
                .build();

        ChatNotification saved = notificationRepository.save(notification);

        // Broadcast via WS
        chatUserRepository.findById(userId).ifPresent(user -> {
            ChatUserDTO senderDto = senderId != null
                    ? chatUserRepository.findById(senderId).map(ChatUserDTO::fromEntity).orElse(null)
                    : null;
            String channelName = channelId != null
                    ? channelRepository.findById(channelId).map(ch -> ch.getName() != null ? ch.getName() : "DM").orElse(null)
                    : null;

            ChatNotificationDTO dto = ChatNotificationDTO.fromEntity(saved, senderDto, channelName);

            messagingTemplate.convertAndSendToUser(
                    user.getExternalUserId().toString(),
                    "/queue/notifications",
                    dto
            );
        });

        log.debug("Notification created: type={}, userId={}, title={}", type, userId, title);
    }
}
