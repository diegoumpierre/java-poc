package com.poc.chat.service;

import com.poc.chat.domain.*;
import com.poc.chat.dto.chat.*;
import com.poc.chat.repository.*;
import com.poc.shared.tenant.TenantContext;
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
public class ChannelService {

    private final ChatChannelRepository channelRepository;
    private final ChatChannelMemberRepository memberRepository;
    private final ChatUserRepository userRepository;
    private final ChatMessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public List<ChatChannelDTO> getUserChannels() {
        UUID tenantId = TenantContext.getCurrentTenant();
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) return Collections.emptyList();

        List<ChatChannel> channels = channelRepository.findByTenantIdAndUserId(tenantId, currentUser.getId());

        return channels.stream()
                .map(ch -> enrichChannelDTO(ch, currentUser.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatChannelDTO> getPublicChannels() {
        UUID tenantId = TenantContext.getCurrentTenant();
        List<ChatChannel> channels = channelRepository.findPublicByTenantId(tenantId);
        return channels.stream()
                .map(ch -> {
                    ChatChannelDTO dto = ChatChannelDTO.fromEntity(ch);
                    dto.setMemberCount(memberRepository.countByChannelId(ch.getId()));
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ChatChannelDTO> getChannel(Long channelId) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) return Optional.empty();

        return channelRepository.findById(channelId)
                .filter(ch -> memberRepository.isMember(ch.getId(), currentUser.getId()))
                .map(ch -> enrichChannelDTO(ch, currentUser.getId()));
    }

    @Transactional
    public ChatChannelDTO createChannel(CreateChannelRequest request) {
        UUID tenantId = TenantContext.getCurrentTenant();
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        Instant now = Instant.now();
        String slug = generateSlug(request.getName(), tenantId);

        ChatChannel channel = ChatChannel.builder()
                .tenantId(tenantId)
                .type(request.getType())
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .creatorId(currentUser.getId())
                .isArchived(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        ChatChannel saved = channelRepository.save(channel);

        // Add creator as OWNER
        ChatChannelMember ownerMember = ChatChannelMember.builder()
                .channelId(saved.getId())
                .userId(currentUser.getId())
                .role(ChannelMemberRole.OWNER.name())
                .joinedAt(now)
                .build();
        memberRepository.save(ownerMember);

        // Add additional members
        if (request.getMemberIds() != null) {
            for (Long memberId : request.getMemberIds()) {
                if (!memberId.equals(currentUser.getId())) {
                    ChatChannelMember member = ChatChannelMember.builder()
                            .channelId(saved.getId())
                            .userId(memberId)
                            .role(ChannelMemberRole.MEMBER.name())
                            .joinedAt(now)
                            .build();
                    memberRepository.save(member);
                }
            }
        }

        log.info("Channel created: id={}, name={}, type={}", saved.getId(), saved.getName(), saved.getType());

        ChatChannelDTO dto = enrichChannelDTO(saved, currentUser.getId());
        broadcastChannelUpdate(saved.getId(), "channel_created", dto);
        return dto;
    }

    @Transactional
    public ChatChannelDTO updateChannel(Long channelId, UpdateChannelRequest request) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        ChatChannel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));

        if (!memberRepository.isMember(channelId, currentUser.getId())) {
            throw new IllegalArgumentException("Not a member of this channel");
        }

        Instant now = Instant.now();
        if (request.getName() != null) {
            channel.setName(request.getName());
            channel.setSlug(generateSlug(request.getName(), channel.getTenantId()));
        }
        if (request.getDescription() != null) channel.setDescription(request.getDescription());
        if (request.getAvatarUrl() != null) channel.setAvatarUrl(request.getAvatarUrl());
        channel.setUpdatedAt(now);

        ChatChannel updated = channelRepository.save(channel);
        ChatChannelDTO dto = enrichChannelDTO(updated, currentUser.getId());
        broadcastChannelUpdate(channelId, "channel_updated", dto);
        return dto;
    }

    @Transactional
    public void archiveChannel(Long channelId) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        ChatChannelMember member = memberRepository.findByChannelIdAndUserId(channelId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Not a member of this channel"));

        if (!ChannelMemberRole.OWNER.name().equals(member.getRole())
                && !ChannelMemberRole.ADMIN.name().equals(member.getRole())) {
            throw new IllegalArgumentException("Only owners/admins can archive channels");
        }

        channelRepository.archiveChannel(channelId, Instant.now());
        broadcastChannelUpdate(channelId, "channel_archived", Map.of("channelId", channelId));
    }

    @Transactional
    public void joinChannel(Long channelId) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        ChatChannel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        if (!channel.isPublicChannel()) {
            throw new IllegalArgumentException("Cannot join non-public channel");
        }

        if (memberRepository.isMember(channelId, currentUser.getId())) {
            return; // Already a member
        }

        Instant now = Instant.now();
        ChatChannelMember member = ChatChannelMember.builder()
                .channelId(channelId)
                .userId(currentUser.getId())
                .role(ChannelMemberRole.MEMBER.name())
                .joinedAt(now)
                .build();
        memberRepository.save(member);
        broadcastChannelUpdate(channelId, "member_joined", Map.of("userId", currentUser.getId()));
    }

    @Transactional
    public void leaveChannel(Long channelId) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        memberRepository.deleteByChannelIdAndUserId(channelId, currentUser.getId());
        broadcastChannelUpdate(channelId, "member_left", Map.of("userId", currentUser.getId()));
    }

    @Transactional(readOnly = true)
    public List<ChannelMemberDTO> getMembers(Long channelId) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null || !memberRepository.isMember(channelId, currentUser.getId())) {
            return Collections.emptyList();
        }

        List<ChatChannelMember> members = memberRepository.findByChannelId(channelId);
        Map<Long, ChatUserDTO> userCache = new HashMap<>();

        return members.stream()
                .map(m -> {
                    ChatUserDTO user = userCache.computeIfAbsent(m.getUserId(),
                            id -> userRepository.findById(id).map(ChatUserDTO::fromEntity).orElse(null));
                    return ChannelMemberDTO.fromEntity(m, user);
                })
                .toList();
    }

    @Transactional
    public void addMembers(Long channelId, List<Long> userIds) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        if (!memberRepository.isMember(channelId, currentUser.getId())) {
            throw new IllegalArgumentException("Not a member of this channel");
        }

        Instant now = Instant.now();
        for (Long userId : userIds) {
            if (!memberRepository.isMember(channelId, userId)) {
                ChatChannelMember member = ChatChannelMember.builder()
                        .channelId(channelId)
                        .userId(userId)
                        .role(ChannelMemberRole.MEMBER.name())
                        .joinedAt(now)
                        .build();
                memberRepository.save(member);
            }
        }
        broadcastChannelUpdate(channelId, "members_added", Map.of("userIds", userIds));
    }

    @Transactional
    public void removeMember(Long channelId, Long userId) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        ChatChannelMember currentMember = memberRepository.findByChannelIdAndUserId(channelId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Not a member of this channel"));

        if (!ChannelMemberRole.OWNER.name().equals(currentMember.getRole())
                && !ChannelMemberRole.ADMIN.name().equals(currentMember.getRole())) {
            throw new IllegalArgumentException("Only owners/admins can remove members");
        }

        memberRepository.deleteByChannelIdAndUserId(channelId, userId);
        broadcastChannelUpdate(channelId, "member_removed", Map.of("userId", userId));
    }

    @Transactional
    public void updateReadCursor(Long channelId) {
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) return;

        memberRepository.updateLastReadAt(channelId, currentUser.getId(), Instant.now());
    }

    @Transactional
    public ChatChannelDTO createOrGetDm(Long otherUserId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        ChatUser currentUser = getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("Current user not found");

        // Check if DM already exists
        Optional<ChatChannel> existing = channelRepository.findDmBetweenUsers(tenantId, currentUser.getId(), otherUserId);
        if (existing.isPresent()) {
            return enrichChannelDTO(existing.get(), currentUser.getId());
        }

        // Create new DM
        Instant now = Instant.now();
        ChatChannel dm = ChatChannel.builder()
                .tenantId(tenantId)
                .type(ChannelType.DM.name())
                .creatorId(currentUser.getId())
                .isArchived(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        ChatChannel saved = channelRepository.save(dm);

        // Add both users
        memberRepository.save(ChatChannelMember.builder()
                .channelId(saved.getId()).userId(currentUser.getId())
                .role(ChannelMemberRole.MEMBER.name()).joinedAt(now).build());
        memberRepository.save(ChatChannelMember.builder()
                .channelId(saved.getId()).userId(otherUserId)
                .role(ChannelMemberRole.MEMBER.name()).joinedAt(now).build());

        return enrichChannelDTO(saved, currentUser.getId());
    }

    // ==================== Helpers ====================

    private ChatChannelDTO enrichChannelDTO(ChatChannel channel, Long currentUserId) {
        ChatChannelDTO dto = ChatChannelDTO.fromEntity(channel);
        dto.setMemberCount(memberRepository.countByChannelId(channel.getId()));

        // Unread count based on last_read_at cursor
        ChatChannelMember membership = memberRepository.findByChannelIdAndUserId(channel.getId(), currentUserId)
                .orElse(null);
        if (membership != null && membership.getLastReadAt() != null) {
            // Count messages after last_read_at that aren't from current user
            int unread = messageRepository.countUnreadInChannel(channel.getId(), currentUserId, membership.getLastReadAt());
            dto.setUnreadCount(unread);
        }

        // For DMs, set the other participant
        if (channel.isDm()) {
            List<ChatChannelMember> members = memberRepository.findByChannelId(channel.getId());
            members.stream()
                    .filter(m -> !m.getUserId().equals(currentUserId))
                    .findFirst()
                    .ifPresent(otherMember -> {
                        userRepository.findById(otherMember.getUserId())
                                .map(ChatUserDTO::fromEntity)
                                .ifPresent(dto::setOtherParticipant);
                    });
        }

        // Last message
        ChatMessage lastMsg = messageRepository.findLastMessageInChannel(channel.getId());
        if (lastMsg != null) {
            ChatUserDTO sender = userRepository.findById(lastMsg.getSenderId())
                    .map(ChatUserDTO::fromEntity).orElse(null);
            dto.setLastMessage(ChatMessageDTO.fromEntity(lastMsg, sender, currentUserId));
        }

        return dto;
    }

    private ChatUser getCurrentUser() {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID externalUserId = TenantContext.getCurrentUser();
        return userRepository.findByExternalUserIdAndTenantId(externalUserId, tenantId).orElse(null);
    }

    private String generateSlug(String name, UUID tenantId) {
        String slug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        // Check uniqueness
        Optional<ChatChannel> existing = channelRepository.findByTenantIdAndSlug(tenantId, slug);
        if (existing.isPresent()) {
            slug = slug + "-" + System.currentTimeMillis() % 10000;
        }
        return slug;
    }

    private void broadcastChannelUpdate(Long channelId, String eventType, Object data) {
        List<Long> memberUserIds = memberRepository.findUserIdsByChannelId(channelId);
        Map<String, Object> event = Map.of("eventType", eventType, "channelId", channelId, "data", data);

        for (Long userId : memberUserIds) {
            userRepository.findById(userId).ifPresent(user ->
                    messagingTemplate.convertAndSendToUser(
                            user.getExternalUserId().toString(),
                            "/queue/channel-updates",
                            event
                    )
            );
        }
    }
}
