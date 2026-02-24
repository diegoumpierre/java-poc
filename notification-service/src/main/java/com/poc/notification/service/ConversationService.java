package com.poc.notification.service;

import com.poc.notification.domain.Conversation;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.InboundMessage;
import com.poc.notification.dto.ConversationDTO;
import com.poc.notification.dto.MessageDTO;
import com.poc.notification.repository.ConversationRepository;
import com.poc.notification.repository.EmailHistoryRepository;
import com.poc.notification.repository.InboundMessageRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final InboundMessageRepository inboundMessageRepository;
    private final EmailHistoryRepository emailHistoryRepository;

    @Transactional(readOnly = true)
    public List<ConversationDTO> listConversations() {
        String tenantId = TenantContext.getCurrentTenant().toString();
        return conversationRepository.findByTenantId(tenantId).stream()
                .map(ConversationDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConversationDTO getConversation(Long id) {
        String tenantId = TenantContext.getCurrentTenant().toString();
        Conversation conv = conversationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + id));
        return ConversationDTO.fromEntity(conv);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getMessages(Long conversationId) {
        String tenantId = TenantContext.getCurrentTenant().toString();
        conversationRepository.findByIdAndTenantId(conversationId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));

        List<MessageDTO> messages = new ArrayList<>();

        inboundMessageRepository.findByConversationId(conversationId).stream()
                .map(MessageDTO::fromInbound)
                .forEach(messages::add);

        emailHistoryRepository.findByConversationId(conversationId).stream()
                .map(MessageDTO::fromOutbound)
                .forEach(messages::add);

        messages.sort(Comparator.comparing(MessageDTO::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));
        return messages;
    }

    @Transactional
    public void markAsRead(Long conversationId) {
        String tenantId = TenantContext.getCurrentTenant().toString();
        Conversation conv = conversationRepository.findByIdAndTenantId(conversationId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));

        inboundMessageRepository.markAllReadByConversationId(conversationId);
        conv.setUnreadCount(0);
        conv.setUpdatedAt(Instant.now());
        conversationRepository.save(conv);
    }

    public Conversation getOrCreateConversation(String tenantId, String contactEmail, String contactName,
                                                 String subject, String threadId) {
        Instant now = Instant.now();

        if (threadId != null && !threadId.isBlank()) {
            return conversationRepository.findByTenantIdAndContactEmailAndThreadId(tenantId, contactEmail, threadId)
                    .orElseGet(() -> conversationRepository.save(Conversation.builder()
                            .tenantId(tenantId)
                            .contactEmail(contactEmail)
                            .contactName(contactName)
                            .subject(subject)
                            .threadId(threadId)
                            .lastMessageAt(now)
                            .createdAt(now)
                            .updatedAt(now)
                            .build()));
        }

        return conversationRepository.findByTenantIdAndContactEmailNoThread(tenantId, contactEmail)
                .orElseGet(() -> conversationRepository.save(Conversation.builder()
                        .tenantId(tenantId)
                        .contactEmail(contactEmail)
                        .contactName(contactName)
                        .subject(subject)
                        .lastMessageAt(now)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()));
    }

    public void updateConversationLastMessage(Long conversationId, String preview, boolean incrementUnread) {
        Instant now = Instant.now();
        String truncated = preview != null && preview.length() > 500 ? preview.substring(0, 500) : preview;
        conversationRepository.updateLastMessage(conversationId, now, truncated, incrementUnread ? 1 : 0, now);
    }
}
