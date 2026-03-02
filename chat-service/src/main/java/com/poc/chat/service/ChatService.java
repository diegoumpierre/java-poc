package com.poc.chat.service;

import com.poc.shared.tenant.TenantContext;
import com.poc.chat.domain.*;
import com.poc.chat.dto.chat.*;
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
public class ChatService {

    private final ChatUserRepository chatUserRepository;
    private final ChatConversationRepository chatConversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatChannelMemberRepository channelMemberRepository;
    private final ChatChannelRepository channelRepository;
    private final ReactionService reactionService;
    private final MentionService mentionService;
    private final ChatNotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    // ==================== User Operations ====================

    @Transactional(readOnly = true)
    public List<ChatUserDTO> getAvailableUsers() {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID currentExternalUserId = TenantContext.getCurrentUser();

        ChatUser currentUser = chatUserRepository.findByExternalUserIdAndTenantId(currentExternalUserId, tenantId)
                .orElse(null);

        if (currentUser == null) {
            log.warn("Current user not found in chat users: externalUserId={}, tenantId={}", currentExternalUserId, tenantId);
            return Collections.emptyList();
        }

        return chatUserRepository.findByTenantIdExcludingUser(tenantId, currentUser.getId())
                .stream()
                .map(ChatUserDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ChatUser> getCurrentChatUser() {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID currentExternalUserId = TenantContext.getCurrentUser();
        return chatUserRepository.findByExternalUserIdAndTenantId(currentExternalUserId, tenantId);
    }

    @Transactional
    public ChatUser getOrCreateChatUser(UUID externalUserId, UUID tenantId, String name, String email, String avatarUrl) {
        return chatUserRepository.findByExternalUserIdAndTenantId(externalUserId, tenantId)
                .orElseGet(() -> {
                    Instant now = Instant.now();
                    ChatUser newUser = ChatUser.builder()
                            .externalUserId(externalUserId)
                            .tenantId(tenantId)
                            .name(name)
                            .email(email)
                            .avatarUrl(avatarUrl)
                            .status(ChatUserStatus.OFFLINE.name())
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return chatUserRepository.save(newUser);
                });
    }

    @Transactional
    public void updateUserStatus(Long userId, ChatUserStatus status) {
        Instant now = Instant.now();
        chatUserRepository.updateStatus(userId, status.name(), now, now);
    }

    @Transactional
    public void updateUserProfile(Long userId, String name, String email, String avatarUrl) {
        Instant now = Instant.now();
        chatUserRepository.updateProfile(userId, name, email, avatarUrl, now);
    }

    // ==================== Conversation Operations ====================

    @Transactional(readOnly = true)
    public List<ChatConversationDTO> getConversations() {
        UUID tenantId = TenantContext.getCurrentTenant();
        ChatUser currentUser = getCurrentChatUser().orElse(null);

        if (currentUser == null) {
            return Collections.emptyList();
        }

        List<ChatConversation> conversations = chatConversationRepository
                .findByUserIdOrderByLastMessageAtDesc(tenantId, currentUser.getId());

        return conversations.stream()
                .map(conv -> toConversationDTO(conv, currentUser.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ChatConversationDTO> getConversation(Long conversationId) {
        ChatUser currentUser = getCurrentChatUser().orElse(null);
        if (currentUser == null) {
            return Optional.empty();
        }

        return chatConversationRepository.findById(conversationId)
                .filter(conv -> isParticipant(conv, currentUser.getId()))
                .map(conv -> toConversationDTO(conv, currentUser.getId()));
    }

    @Transactional
    public ChatConversationDTO createOrGetConversation(CreateConversationRequest request) {
        UUID tenantId = TenantContext.getCurrentTenant();
        ChatUser currentUser = getCurrentChatUser()
                .orElseThrow(() -> new IllegalStateException("Current user not found in chat users"));

        Long otherUserId = request.getOtherUserId();

        ChatUser otherUser = chatUserRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("Other user not found: " + otherUserId));

        if (!otherUser.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("Cannot create conversation with user from different tenant");
        }

        Optional<ChatConversation> existingConv = chatConversationRepository
                .findByParticipants(tenantId, currentUser.getId(), otherUserId);

        if (existingConv.isPresent()) {
            return toConversationDTO(existingConv.get(), currentUser.getId());
        }

        Instant now = Instant.now();
        ChatConversation newConv = ChatConversation.builder()
                .tenantId(tenantId)
                .participantOneId(currentUser.getId())
                .participantTwoId(otherUserId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        ChatConversation savedConv = chatConversationRepository.save(newConv);
        log.info("Created new conversation: id={}, participants=[{}, {}]",
                savedConv.getId(), currentUser.getId(), otherUserId);

        return toConversationDTO(savedConv, currentUser.getId());
    }

    // ==================== Message Operations ====================

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getMessages(Long conversationId, int offset, int limit) {
        ChatUser currentUser = getCurrentChatUser().orElse(null);
        if (currentUser == null) {
            return Collections.emptyList();
        }

        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .filter(conv -> isParticipant(conv, currentUser.getId()))
                .orElse(null);

        if (conversation == null) {
            return Collections.emptyList();
        }

        List<ChatMessage> messages = limit > 0
                ? chatMessageRepository.findByConversationIdPaginated(conversationId, offset, limit)
                : chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        Map<Long, ChatUserDTO> userCache = new HashMap<>();
        userCache.put(currentUser.getId(), ChatUserDTO.fromEntity(currentUser));

        Long otherUserId = getOtherParticipantId(conversation, currentUser.getId());
        chatUserRepository.findById(otherUserId)
                .ifPresent(u -> userCache.put(u.getId(), ChatUserDTO.fromEntity(u)));

        return messages.stream()
                .map(msg -> ChatMessageDTO.fromEntity(msg, userCache.get(msg.getSenderId()), currentUser.getId()))
                .toList();
    }

    @Transactional
    public ChatMessageDTO sendMessage(SendMessageRequest request) {
        ChatUser currentUser = getCurrentChatUser()
                .orElseThrow(() -> new IllegalStateException("Current user not found in chat users"));

        Long conversationId = request.getConversationId();

        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .filter(conv -> isParticipant(conv, currentUser.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found or access denied: " + conversationId));

        Instant now = Instant.now();

        ChatMessage message = ChatMessage.builder()
                .conversationId(conversationId)
                .senderId(currentUser.getId())
                .text(request.getText())
                .createdAt(now)
                .updatedAt(now)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        chatConversationRepository.updateLastMessageAt(conversationId, now, now);

        log.debug("Message sent: conversationId={}, senderId={}, messageId={}",
                conversationId, currentUser.getId(), savedMessage.getId());

        return ChatMessageDTO.fromEntity(savedMessage, ChatUserDTO.fromEntity(currentUser), currentUser.getId());
    }

    @Transactional
    public void markMessageAsRead(Long messageId) {
        ChatUser currentUser = getCurrentChatUser().orElse(null);
        if (currentUser == null) return;

        ChatMessage message = chatMessageRepository.findById(messageId).orElse(null);
        if (message == null || message.getSenderId().equals(currentUser.getId())) {
            return;
        }

        ChatConversation conversation = chatConversationRepository.findById(message.getConversationId())
                .filter(conv -> isParticipant(conv, currentUser.getId()))
                .orElse(null);

        if (conversation == null) return;

        Instant now = Instant.now();
        chatMessageRepository.markAsRead(messageId, now, now);
    }

    @Transactional
    public void markAllMessagesAsRead(Long conversationId) {
        ChatUser currentUser = getCurrentChatUser().orElse(null);
        if (currentUser == null) return;

        ChatConversation conversation = chatConversationRepository.findById(conversationId)
                .filter(conv -> isParticipant(conv, currentUser.getId()))
                .orElse(null);

        if (conversation == null) return;

        Instant now = Instant.now();
        chatMessageRepository.markAllAsRead(conversationId, currentUser.getId(), now, now);
    }

    // ==================== Channel Message Operations ====================

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChannelMessages(Long channelId, int offset, int limit) {
        ChatUser currentUser = getCurrentChatUser().orElse(null);
        if (currentUser == null) return Collections.emptyList();

        if (!channelMemberRepository.isMember(channelId, currentUser.getId())) {
            return Collections.emptyList();
        }

        // Only top-level messages (not thread replies)
        List<ChatMessage> messages = chatMessageRepository.findTopLevelByChannelId(channelId, offset, limit);

        Map<Long, ChatUserDTO> userCache = new HashMap<>();
        userCache.put(currentUser.getId(), ChatUserDTO.fromEntity(currentUser));

        // Batch-load reactions for all messages
        List<Long> messageIds = messages.stream().map(ChatMessage::getId).toList();
        Map<Long, List<ChatReactionDTO>> reactionsMap = reactionService.getReactionSummaries(messageIds, currentUser.getId());

        return messages.stream()
                .map(msg -> {
                    ChatUserDTO sender = userCache.computeIfAbsent(msg.getSenderId(),
                            id -> chatUserRepository.findById(id).map(ChatUserDTO::fromEntity).orElse(null));
                    ChatMessageDTO dto = ChatMessageDTO.fromEntity(msg, sender, currentUser.getId());
                    dto.setReactions(reactionsMap.getOrDefault(msg.getId(), Collections.emptyList()));
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getThreadMessages(Long parentMessageId, int offset, int limit) {
        ChatUser currentUser = getCurrentChatUser().orElse(null);
        if (currentUser == null) return Collections.emptyList();

        ChatMessage parentMessage = chatMessageRepository.findById(parentMessageId).orElse(null);
        if (parentMessage == null || parentMessage.getChannelId() == null) return Collections.emptyList();

        if (!channelMemberRepository.isMember(parentMessage.getChannelId(), currentUser.getId())) {
            return Collections.emptyList();
        }

        List<ChatMessage> replies = limit > 0
                ? chatMessageRepository.findThreadRepliesPaginated(parentMessageId, offset, limit)
                : chatMessageRepository.findThreadReplies(parentMessageId);

        Map<Long, ChatUserDTO> userCache = new HashMap<>();
        userCache.put(currentUser.getId(), ChatUserDTO.fromEntity(currentUser));

        List<Long> messageIds = replies.stream().map(ChatMessage::getId).toList();
        Map<Long, List<ChatReactionDTO>> reactionsMap = reactionService.getReactionSummaries(messageIds, currentUser.getId());

        return replies.stream()
                .map(msg -> {
                    ChatUserDTO sender = userCache.computeIfAbsent(msg.getSenderId(),
                            id -> chatUserRepository.findById(id).map(ChatUserDTO::fromEntity).orElse(null));
                    ChatMessageDTO dto = ChatMessageDTO.fromEntity(msg, sender, currentUser.getId());
                    dto.setReactions(reactionsMap.getOrDefault(msg.getId(), Collections.emptyList()));
                    return dto;
                })
                .toList();
    }

    @Transactional
    public ChatMessageDTO sendChannelMessage(SendMessageRequest request) {
        ChatUser currentUser = getCurrentChatUser()
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        Long channelId = request.getChannelId();

        if (!channelMemberRepository.isMember(channelId, currentUser.getId())) {
            throw new IllegalArgumentException("Not a member of channel: " + channelId);
        }

        Instant now = Instant.now();

        ChatMessage message = ChatMessage.builder()
                .channelId(channelId)
                .senderId(currentUser.getId())
                .text(request.getText())
                .messageType("TEXT")
                .parentMessageId(request.getParentMessageId())
                .replyCount(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);

        // Process @mentions in the message text (with senderId for notifications)
        mentionService.processMentions(saved.getId(), request.getText(), channelId, currentUser.getId());

        // Update parent message reply count if this is a thread reply
        if (request.getParentMessageId() != null) {
            chatMessageRepository.incrementReplyCount(request.getParentMessageId(), now, now);
        }

        channelRepository.updateLastMessageAt(channelId, now, now);

        ChatMessageDTO messageDTO = ChatMessageDTO.fromEntity(saved, ChatUserDTO.fromEntity(currentUser), currentUser.getId());

        // Push message to all channel members via WebSocket
        List<Long> memberUserIds = channelMemberRepository.findUserIdsByChannelId(channelId);

        // Check if this is a DM channel and create DM notification
        channelRepository.findById(channelId).ifPresent(channel -> {
            if ("DM".equals(channel.getType())) {
                for (Long memberId : memberUserIds) {
                    if (!memberId.equals(currentUser.getId())) {
                        try {
                            notificationService.createDmNotification(memberId, currentUser.getId(), channelId, saved.getId(), request.getText());
                        } catch (Exception e) {
                            log.warn("Failed to create DM notification: {}", e.getMessage());
                        }
                    }
                }
            }
        });

        for (Long memberId : memberUserIds) {
            if (!memberId.equals(currentUser.getId())) {
                chatUserRepository.findById(memberId).ifPresent(user ->
                        messagingTemplate.convertAndSendToUser(
                                user.getExternalUserId().toString(),
                                "/queue/messages",
                                messageDTO
                        )
                );
            }
        }

        log.debug("Channel message sent: channelId={}, senderId={}, messageId={}",
                channelId, currentUser.getId(), saved.getId());

        return messageDTO;
    }

    // ==================== Edit Message ====================

    @Transactional
    public ChatMessageDTO editMessage(Long messageId, String newText) {
        ChatUser currentUser = getCurrentChatUser()
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        if (!message.getSenderId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Can only edit own messages");
        }

        Instant now = Instant.now();
        message.setText(newText);
        message.setEditedAt(now);
        message.setUpdatedAt(now);

        ChatMessage saved = chatMessageRepository.save(message);

        // Re-process mentions
        if (message.getChannelId() != null) {
            mentionService.processMentions(saved.getId(), newText, message.getChannelId());
        }

        ChatMessageDTO dto = ChatMessageDTO.fromEntity(saved, ChatUserDTO.fromEntity(currentUser), currentUser.getId());

        // Broadcast edit to channel members
        if (message.getChannelId() != null) {
            Map<String, Object> editEvent = Map.of(
                    "type", "message_edited",
                    "messageId", messageId,
                    "channelId", message.getChannelId(),
                    "text", newText,
                    "editedAt", now.toString()
            );
            List<Long> memberUserIds = channelMemberRepository.findUserIdsByChannelId(message.getChannelId());
            for (Long memberId : memberUserIds) {
                chatUserRepository.findById(memberId).ifPresent(user ->
                        messagingTemplate.convertAndSendToUser(
                                user.getExternalUserId().toString(),
                                "/queue/messages",
                                editEvent
                        )
                );
            }
        }

        return dto;
    }

    // ==================== Helper Methods ====================

    private ChatConversationDTO toConversationDTO(ChatConversation conversation, Long currentUserId) {
        Long otherParticipantId = getOtherParticipantId(conversation, currentUserId);
        ChatUserDTO otherParticipant = chatUserRepository.findById(otherParticipantId)
                .map(ChatUserDTO::fromEntity)
                .orElse(null);

        ChatMessage lastMsg = chatMessageRepository.findLastMessage(conversation.getId());
        ChatMessageDTO lastMessageDTO = null;
        if (lastMsg != null) {
            ChatUserDTO sender = lastMsg.getSenderId().equals(currentUserId)
                    ? getCurrentChatUser().map(ChatUserDTO::fromEntity).orElse(null)
                    : otherParticipant;
            lastMessageDTO = ChatMessageDTO.fromEntity(lastMsg, sender, currentUserId);
        }

        int unreadCount = chatMessageRepository.countUnreadMessages(conversation.getId(), currentUserId);

        return ChatConversationDTO.fromEntity(conversation, otherParticipant, lastMessageDTO, unreadCount);
    }

    private Long getOtherParticipantId(ChatConversation conversation, Long currentUserId) {
        return conversation.getParticipantOneId().equals(currentUserId)
                ? conversation.getParticipantTwoId()
                : conversation.getParticipantOneId();
    }

    private boolean isParticipant(ChatConversation conversation, Long userId) {
        return conversation.getParticipantOneId().equals(userId)
                || conversation.getParticipantTwoId().equals(userId);
    }
}
