package com.poc.chat.service;

import com.poc.chat.domain.ChatMention;
import com.poc.chat.domain.ChatUser;
import com.poc.chat.domain.MentionType;
import com.poc.chat.dto.chat.ChatMentionDTO;
import com.poc.chat.dto.chat.ChatUserDTO;
import com.poc.chat.repository.ChatMentionRepository;
import com.poc.chat.repository.ChatUserRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentionService {

    private final ChatMentionRepository mentionRepository;
    private final ChatUserRepository userRepository;
    private final ChatNotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

    @Transactional
    public List<ChatMention> processMentions(Long messageId, String text, Long channelId) {
        return processMentions(messageId, text, channelId, null);
    }

    @Transactional
    public List<ChatMention> processMentions(Long messageId, String text, Long channelId, Long senderId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        List<ChatMention> mentions = new ArrayList<>();

        // Check for @here
        if (text.contains("@here")) {
            ChatMention hereMention = ChatMention.builder()
                    .messageId(messageId)
                    .mentionType(MentionType.HERE.name())
                    .createdAt(Instant.now())
                    .build();
            mentions.add(mentionRepository.save(hereMention));
        }

        // Parse @username mentions
        Matcher matcher = MENTION_PATTERN.matcher(text);
        Set<String> processedNames = new HashSet<>();

        while (matcher.find()) {
            String name = matcher.group(1);
            if ("here".equals(name) || processedNames.contains(name.toLowerCase())) continue;
            processedNames.add(name.toLowerCase());

            // Find user by name (case-insensitive)
            Optional<ChatUser> user = userRepository.findByNameIgnoreCaseAndTenantId(name, tenantId);
            user.ifPresent(u -> {
                ChatMention mention = ChatMention.builder()
                        .messageId(messageId)
                        .mentionedUserId(u.getId())
                        .mentionType(MentionType.USER.name())
                        .createdAt(Instant.now())
                        .build();
                mentions.add(mentionRepository.save(mention));

                // Notify mentioned user via WebSocket
                Map<String, Object> event = Map.of(
                        "type", "mention",
                        "messageId", messageId,
                        "channelId", channelId
                );
                messagingTemplate.convertAndSendToUser(
                        u.getExternalUserId().toString(),
                        "/queue/mentions",
                        event
                );

                // Create persistent notification
                if (senderId != null && !u.getId().equals(senderId)) {
                    try {
                        notificationService.createMentionNotification(u.getId(), senderId, channelId, messageId, text);
                    } catch (Exception e) {
                        log.warn("Failed to create mention notification for user {}: {}", u.getId(), e.getMessage());
                    }
                }
            });
        }

        return mentions;
    }

    @Transactional(readOnly = true)
    public List<ChatMentionDTO> getMentionsForMessage(Long messageId) {
        List<ChatMention> mentions = mentionRepository.findByMessageId(messageId);
        Map<Long, ChatUserDTO> userCache = new HashMap<>();

        return mentions.stream()
                .map(m -> {
                    ChatUserDTO user = null;
                    if (m.getMentionedUserId() != null) {
                        user = userCache.computeIfAbsent(m.getMentionedUserId(),
                                id -> userRepository.findById(id).map(ChatUserDTO::fromEntity).orElse(null));
                    }
                    return ChatMentionDTO.fromEntity(m, user);
                })
                .toList();
    }

    public Map<Long, List<ChatMentionDTO>> getMentionSummaries(List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) return Collections.emptyMap();

        List<ChatMention> allMentions = mentionRepository.findByMessageIdIn(messageIds);
        Map<Long, ChatUserDTO> userCache = new HashMap<>();

        return allMentions.stream()
                .map(m -> {
                    ChatUserDTO user = null;
                    if (m.getMentionedUserId() != null) {
                        user = userCache.computeIfAbsent(m.getMentionedUserId(),
                                id -> userRepository.findById(id).map(ChatUserDTO::fromEntity).orElse(null));
                    }
                    return ChatMentionDTO.fromEntity(m, user);
                })
                .collect(Collectors.groupingBy(dto -> {
                    // Find the original mention to get messageId
                    return allMentions.stream()
                            .filter(m -> m.getId().equals(dto.getId()))
                            .findFirst()
                            .map(ChatMention::getMessageId)
                            .orElse(0L);
                }));
    }
}
