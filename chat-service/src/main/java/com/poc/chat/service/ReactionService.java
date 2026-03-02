package com.poc.chat.service;

import com.poc.chat.domain.ChatMessage;
import com.poc.chat.domain.ChatReaction;
import com.poc.chat.domain.ChatUser;
import com.poc.chat.dto.chat.ChatReactionDTO;
import com.poc.chat.dto.chat.ChatUserDTO;
import com.poc.chat.repository.*;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionService {

    private final ChatReactionRepository reactionRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatChannelMemberRepository channelMemberRepository;
    private final ChatUserRepository userRepository;
    private final ChatNotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public List<ChatReactionDTO> addReaction(Long messageId, String emoji) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        // Verify user is member of the channel
        if (message.getChannelId() != null && !channelMemberRepository.isMember(message.getChannelId(), currentUser.getId())) {
            throw new IllegalArgumentException("Not a member of this channel");
        }

        // Check if already reacted with this emoji
        Optional<ChatReaction> existing = reactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, currentUser.getId(), emoji);
        if (existing.isPresent()) {
            return getReactionSummary(messageId, currentUser.getId());
        }

        ChatReaction reaction = ChatReaction.builder()
                .messageId(messageId)
                .userId(currentUser.getId())
                .emoji(emoji)
                .createdAt(Instant.now())
                .build();
        reactionRepository.save(reaction);

        log.debug("Reaction added: messageId={}, userId={}, emoji={}", messageId, currentUser.getId(), emoji);

        // Create notification for message owner
        try {
            notificationService.createReactionNotification(
                    message.getSenderId(), currentUser.getId(),
                    message.getChannelId(), messageId, emoji
            );
        } catch (Exception e) {
            log.warn("Failed to create reaction notification: {}", e.getMessage());
        }

        List<ChatReactionDTO> summary = getReactionSummary(messageId, currentUser.getId());

        // Broadcast to channel members
        broadcastReactionUpdate(message, summary);

        return summary;
    }

    @Transactional
    public List<ChatReactionDTO> removeReaction(Long messageId, String emoji) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        reactionRepository.deleteByMessageIdAndUserIdAndEmoji(messageId, currentUser.getId(), emoji);

        log.debug("Reaction removed: messageId={}, userId={}, emoji={}", messageId, currentUser.getId(), emoji);

        List<ChatReactionDTO> summary = getReactionSummary(messageId, currentUser.getId());

        ChatMessage message = messageRepository.findById(messageId).orElse(null);
        if (message != null) {
            broadcastReactionUpdate(message, summary);
        }

        return summary;
    }

    @Transactional(readOnly = true)
    public List<ChatReactionDTO> getReactionSummary(Long messageId, Long currentUserId) {
        List<ChatReaction> reactions = reactionRepository.findByMessageId(messageId);

        Map<String, List<ChatReaction>> grouped = reactions.stream()
                .collect(Collectors.groupingBy(ChatReaction::getEmoji));

        return grouped.entrySet().stream()
                .map(entry -> ChatReactionDTO.builder()
                        .emoji(entry.getKey())
                        .count(entry.getValue().size())
                        .userReacted(entry.getValue().stream().anyMatch(r -> r.getUserId().equals(currentUserId)))
                        .build())
                .sorted(Comparator.comparing(ChatReactionDTO::getEmoji))
                .toList();
    }

    public Map<Long, List<ChatReactionDTO>> getReactionSummaries(List<Long> messageIds, Long currentUserId) {
        if (messageIds == null || messageIds.isEmpty()) return Collections.emptyMap();

        List<ChatReaction> allReactions = reactionRepository.findByMessageIdIn(messageIds);

        Map<Long, List<ChatReaction>> byMessage = allReactions.stream()
                .collect(Collectors.groupingBy(ChatReaction::getMessageId));

        Map<Long, List<ChatReactionDTO>> result = new HashMap<>();
        for (Map.Entry<Long, List<ChatReaction>> entry : byMessage.entrySet()) {
            Map<String, List<ChatReaction>> grouped = entry.getValue().stream()
                    .collect(Collectors.groupingBy(ChatReaction::getEmoji));

            List<ChatReactionDTO> summaries = grouped.entrySet().stream()
                    .map(e -> ChatReactionDTO.builder()
                            .emoji(e.getKey())
                            .count(e.getValue().size())
                            .userReacted(e.getValue().stream().anyMatch(r -> r.getUserId().equals(currentUserId)))
                            .build())
                    .sorted(Comparator.comparing(ChatReactionDTO::getEmoji))
                    .toList();

            result.put(entry.getKey(), summaries);
        }

        return result;
    }

    private void broadcastReactionUpdate(ChatMessage message, List<ChatReactionDTO> summary) {
        if (message.getChannelId() == null) return;

        Map<String, Object> event = Map.of(
                "type", "reaction_update",
                "messageId", message.getId(),
                "channelId", message.getChannelId(),
                "reactions", summary
        );

        List<Long> memberUserIds = channelMemberRepository.findUserIdsByChannelId(message.getChannelId());
        for (Long memberId : memberUserIds) {
            userRepository.findById(memberId).ifPresent(user ->
                    messagingTemplate.convertAndSendToUser(
                            user.getExternalUserId().toString(),
                            "/queue/reactions",
                            event
                    )
            );
        }
    }

    private ChatUser getCurrentUser() {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID externalUserId = TenantContext.getCurrentUser();
        return userRepository.findByExternalUserIdAndTenantId(externalUserId, tenantId).orElse(null);
    }
}
