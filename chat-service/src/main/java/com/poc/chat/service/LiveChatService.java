package com.poc.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.chat.domain.*;
import com.poc.chat.dto.livechat.*;
import com.poc.chat.feign.EmailServiceClient;
import com.poc.chat.feign.WhatsAppServiceClient;
import com.poc.chat.repository.*;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveChatService {

    private final LiveChatVisitorRepository visitorRepository;
    private final LiveChatSessionRepository sessionRepository;
    private final LiveChatMessageRepository messageRepository;
    private final LiveChatWidgetConfigRepository widgetConfigRepository;
    private final ChatUserRepository chatUserRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final WhatsAppServiceClient whatsAppServiceClient;
    private final EmailServiceClient emailServiceClient;

    private static final String LIVECHAT_EVENTS_TOPIC = "livechat-events";

    @Value("${app.inactivity-timeout.livechat-minutes:30}")
    private int livechatTimeoutMinutes;

    @Value("${app.inactivity-timeout.whatsapp-minutes:1440}")
    private int whatsappTimeoutMinutes;

    @Value("${app.inactivity-timeout.email-minutes:4320}")
    private int emailTimeoutMinutes;

    // ==================== Widget (Public, No Auth) ====================

    @Transactional(readOnly = true)
    public LiveChatWidgetConfigDTO getWidgetConfig(UUID tenantId) {
        return getWidgetConfig(tenantId, null);
    }

    @Transactional(readOnly = true)
    public LiveChatWidgetConfigDTO getWidgetConfig(UUID tenantId, String sourceService) {
        Optional<LiveChatWidgetConfig> config;

        if (sourceService != null && !sourceService.isBlank()) {
            config = widgetConfigRepository.findByTenantIdAndSourceService(tenantId, sourceService);
            if (config.isEmpty()) {
                config = widgetConfigRepository.findDefaultByTenantId(tenantId);
            }
        } else {
            config = widgetConfigRepository.findDefaultByTenantId(tenantId);
        }

        return config.map(LiveChatWidgetConfigDTO::fromEntity)
                .orElse(LiveChatWidgetConfigDTO.builder()
                        .tenantId(tenantId.toString())
                        .enabled(true)
                        .primaryColor("#4F46E5")
                        .headerText("Chat with us")
                        .welcomeMessage("Hello! How can we help you?")
                        .offlineMessage("We are currently offline. Please leave a message.")
                        .position("BOTTOM_RIGHT")
                        .requireEmail(false)
                        .build());
    }

    @Transactional
    public LiveChatSessionDTO startChat(UUID tenantId, StartLiveChatRequest request, String visitorIp, String userAgent) {
        Instant now = Instant.now();

        // Find or create visitor
        LiveChatVisitor visitor = visitorRepository.findByTenantIdAndVisitorId(tenantId, request.getVisitorId())
                .map(existing -> {
                    // Update visitor info if changed
                    boolean updated = false;
                    if (request.getVisitorName() != null && !request.getVisitorName().equals(existing.getName())) {
                        existing.setName(request.getVisitorName());
                        updated = true;
                    }
                    if (request.getVisitorEmail() != null && !request.getVisitorEmail().equals(existing.getEmail())) {
                        existing.setEmail(request.getVisitorEmail());
                        updated = true;
                    }
                    if (request.getMetadata() != null && !request.getMetadata().equals(existing.getMetadata())) {
                        existing.setMetadata(request.getMetadata());
                        updated = true;
                    }
                    if (updated) {
                        existing.setUpdatedAt(now);
                        return visitorRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    LiveChatVisitor newVisitor = LiveChatVisitor.builder()
                            .tenantId(tenantId)
                            .visitorId(request.getVisitorId())
                            .name(request.getVisitorName())
                            .email(request.getVisitorEmail())
                            .metadata(request.getMetadata())
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return visitorRepository.save(newVisitor);
                });

        // Generate session token (32 hex chars)
        String sessionToken = UUID.randomUUID().toString().replace("-", "");

        // Create session
        LiveChatSession session = LiveChatSession.builder()
                .tenantId(tenantId)
                .sessionToken(sessionToken)
                .visitorId(visitor.getId())
                .queueId(request.getQueueId())
                .sourceService(request.getSourceService() != null ? request.getSourceService() : "HELPDESK")
                .status(LiveChatSessionStatus.WAITING.name())
                .pageUrl(request.getPageUrl())
                .visitorIp(visitorIp)
                .userAgent(userAgent)
                .messageCount(0)
                .lastActivityAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        LiveChatSession savedSession = sessionRepository.save(session);

        // Add welcome system message from widget config (source-aware)
        String sourceServiceForConfig = request.getSourceService() != null ? request.getSourceService() : "HELPDESK";
        LiveChatWidgetConfigDTO widgetConfig = getWidgetConfig(tenantId, sourceServiceForConfig);
        String welcomeMessage = widgetConfig.getWelcomeMessage() != null
                ? widgetConfig.getWelcomeMessage()
                : "Hello! How can we help you?";
        addSystemMessage(savedSession.getId(), welcomeMessage);

        // Save initial visitor message if provided
        if (request.getInitialMessage() != null && !request.getInitialMessage().isBlank()) {
            LiveChatMessage visitorMessage = LiveChatMessage.builder()
                    .sessionId(savedSession.getId())
                    .senderType(LiveChatSenderType.VISITOR.name())
                    .senderId(visitor.getId())
                    .content(request.getInitialMessage())
                    .messageType("TEXT")
                    .isRead(false)
                    .createdAt(now)
                    .build();
            messageRepository.save(visitorMessage);
            sessionRepository.incrementMessageCount(savedSession.getId(), now, now);
        }

        // Build DTO
        LiveChatSessionDTO sessionDTO = LiveChatSessionDTO.fromEntity(savedSession, visitor.getName(), visitor.getEmail(), null);

        // Publish SESSION_STARTED event to Kafka
        publishEvent(LiveChatEventDTO.builder()
                .eventType("SESSION_STARTED")
                .tenantId(tenantId.toString())
                .sessionId(savedSession.getId())
                .sessionToken(sessionToken)
                .visitorName(visitor.getName())
                .visitorEmail(visitor.getEmail())
                .sourceService(savedSession.getSourceService())
                .pageUrl(savedSession.getPageUrl())
                .timestamp(now)
                .build());

        // Notify agents via WebSocket: waiting queue
        messagingTemplate.convertAndSend(
                "/topic/livechat.waiting." + tenantId,
                sessionDTO
        );

        log.info("Live chat started: sessionId={}, visitorId={}, tenantId={}", savedSession.getId(), visitor.getVisitorId(), tenantId);

        return sessionDTO;
    }

    @Transactional(readOnly = true)
    public LiveChatSessionDTO resumeChat(String sessionToken) {
        LiveChatSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionToken));

        return buildSessionDTO(session);
    }

    @Transactional
    public LiveChatMessageDTO sendVisitorMessage(String sessionToken, SendLiveChatMessageRequest request) {
        LiveChatSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionToken));

        // Validate session is WAITING or ACTIVE
        if (!LiveChatSessionStatus.WAITING.name().equals(session.getStatus())
                && !LiveChatSessionStatus.ACTIVE.name().equals(session.getStatus())) {
            throw new IllegalStateException("Cannot send messages to a " + session.getStatus() + " session");
        }

        Instant now = Instant.now();

        LiveChatMessage message = LiveChatMessage.builder()
                .sessionId(session.getId())
                .senderType(LiveChatSenderType.VISITOR.name())
                .senderId(session.getVisitorId())
                .content(request.getContent())
                .messageType(request.getMessageType() != null ? request.getMessageType() : "TEXT")
                .attachmentUrl(request.getAttachmentUrl())
                .attachmentName(request.getAttachmentName())
                .isRead(false)
                .createdAt(now)
                .build();

        LiveChatMessage savedMessage = messageRepository.save(message);

        // Increment session message count and update last activity
        sessionRepository.incrementMessageCount(session.getId(), now, now);

        // Resolve visitor name for DTO
        String visitorName = visitorRepository.findById(session.getVisitorId())
                .map(LiveChatVisitor::getName)
                .orElse("Visitor");

        LiveChatMessageDTO messageDTO = LiveChatMessageDTO.fromEntity(savedMessage, visitorName);

        // If session has assigned agent, notify via WS
        if (session.getAssignedAgentId() != null) {
            ChatUser agent = resolveAgentChatUser(session.getAssignedAgentId());
            if (agent != null && agent.getExternalUserId() != null) {
                messagingTemplate.convertAndSendToUser(
                        agent.getExternalUserId().toString(),
                        "/queue/livechat",
                        messageDTO
                );
            }
        }

        // Broadcast to session topic
        messagingTemplate.convertAndSend(
                "/topic/livechat.session." + sessionToken,
                messageDTO
        );

        log.debug("Visitor message sent: sessionId={}, messageId={}", session.getId(), savedMessage.getId());

        return messageDTO;
    }

    @Transactional(readOnly = true)
    public List<LiveChatMessageDTO> getMessagesForVisitor(String sessionToken, Instant since) {
        LiveChatSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionToken));

        List<LiveChatMessage> messages;
        if (since != null) {
            messages = messageRepository.findBySessionIdSince(session.getId(), since);
        } else {
            messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());
        }

        return messages.stream()
                .map(this::buildMessageDTO)
                .toList();
    }

    @Transactional
    public void endChatByVisitor(String sessionToken) {
        LiveChatSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionToken));

        Instant now = Instant.now();

        // Close session with closedAt timestamp
        session.setStatus(LiveChatSessionStatus.CLOSED.name());
        session.setClosedAt(now);
        session.setUpdatedAt(now);
        sessionRepository.save(session);

        addSystemMessage(session.getId(), "Visitor ended the chat");

        // Publish SESSION_CLOSED event with messages snapshot
        String visitorName = visitorRepository.findById(session.getVisitorId())
                .map(LiveChatVisitor::getName)
                .orElse(null);
        String visitorEmail = visitorRepository.findById(session.getVisitorId())
                .map(LiveChatVisitor::getEmail)
                .orElse(null);

        publishEvent(LiveChatEventDTO.builder()
                .eventType("SESSION_CLOSED")
                .tenantId(session.getTenantId() != null ? session.getTenantId().toString() : null)
                .sessionId(session.getId())
                .sessionToken(session.getSessionToken())
                .visitorName(visitorName)
                .visitorEmail(visitorEmail)
                .assignedAgentId(session.getAssignedAgentId())
                .agentName(session.getAssignedAgentId() != null ? resolveAgentName(session.getAssignedAgentId()) : null)
                .sourceService(session.getSourceService())
                .pageUrl(session.getPageUrl())
                .messagesJson(buildMessagesJson(session.getId()))
                .timestamp(now)
                .build());

        // Notify agent via WS if assigned
        if (session.getAssignedAgentId() != null) {
            ChatUser agent = resolveAgentChatUser(session.getAssignedAgentId());
            if (agent != null && agent.getExternalUserId() != null) {
                Map<String, Object> closeEvent = Map.of(
                        "type", "session_closed",
                        "sessionId", session.getId(),
                        "sessionToken", sessionToken,
                        "closedBy", "visitor"
                );
                messagingTemplate.convertAndSendToUser(
                        agent.getExternalUserId().toString(),
                        "/queue/livechat",
                        closeEvent
                );
            }
        }

        // Broadcast to session topic
        messagingTemplate.convertAndSend(
                "/topic/livechat.session." + sessionToken,
                (Object) Map.of("type", "session_closed", "closedBy", "visitor")
        );

        log.info("Live chat ended by visitor: sessionId={}", session.getId());
    }

    @Transactional
    public void rateChatByVisitor(String sessionToken, Integer rating, String feedback) {
        LiveChatSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionToken));

        if (!LiveChatSessionStatus.CLOSED.name().equals(session.getStatus())) {
            throw new IllegalStateException("Can only rate closed sessions");
        }

        Instant now = Instant.now();
        sessionRepository.updateRating(session.getId(), rating, feedback, now);

        // Publish SESSION_RATED event
        String visitorName = visitorRepository.findById(session.getVisitorId())
                .map(LiveChatVisitor::getName)
                .orElse(null);
        String agentName = session.getAssignedAgentId() != null
                ? resolveAgentName(session.getAssignedAgentId())
                : null;

        publishEvent(LiveChatEventDTO.builder()
                .eventType("SESSION_RATED")
                .tenantId(session.getTenantId() != null ? session.getTenantId().toString() : null)
                .sessionId(session.getId())
                .sessionToken(session.getSessionToken())
                .visitorName(visitorName)
                .assignedAgentId(session.getAssignedAgentId())
                .agentName(agentName)
                .rating(rating)
                .feedback(feedback)
                .sourceService(session.getSourceService())
                .timestamp(now)
                .build());

        log.info("Live chat rated: sessionId={}, rating={}", session.getId(), rating);
    }

    // ==================== Agent (Authenticated) ====================

    @Transactional(readOnly = true)
    public List<LiveChatSessionDTO> getWaitingSessions(String sourceService) {
        UUID tenantId = TenantContext.getCurrentTenant();
        List<LiveChatSession> sessions = sessionRepository.findByTenantIdAndStatus(tenantId, LiveChatSessionStatus.WAITING.name(), sourceService);
        return sessions.stream()
                .map(this::buildSessionDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LiveChatSessionDTO> getActiveSessions(String sourceService) {
        UUID tenantId = TenantContext.getCurrentTenant();
        List<LiveChatSession> sessions = sessionRepository.findActiveByTenantId(tenantId, sourceService);
        return sessions.stream()
                .map(this::buildSessionDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LiveChatSessionDTO> getAgentSessions(Long agentChatUserId, String sourceService) {
        UUID tenantId = TenantContext.getCurrentTenant();
        List<LiveChatSession> sessions = sessionRepository.findByTenantIdAndAssignedAgentId(tenantId, agentChatUserId, sourceService);
        return sessions.stream()
                .map(this::buildSessionDTO)
                .toList();
    }

    @Transactional
    public LiveChatSessionDTO acceptChat(Long sessionId, Long agentChatUserId) {
        LiveChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        if (!LiveChatSessionStatus.WAITING.name().equals(session.getStatus())) {
            throw new IllegalStateException("Can only accept sessions in WAITING status, current: " + session.getStatus());
        }

        Instant now = Instant.now();
        ChatUser agent = resolveAgentChatUser(agentChatUserId);
        String agentName = agent != null ? agent.getName() : "Agent";

        // Update session: assign agent, set ACTIVE
        sessionRepository.updateAssignedAgent(sessionId, agentChatUserId, now, now);
        sessionRepository.updateStatus(sessionId, LiveChatSessionStatus.ACTIVE.name(), now);

        // Add system message
        addSystemMessage(sessionId, "Agent " + agentName + " joined the chat");

        // Reload session after updates
        session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session disappeared after update: " + sessionId));

        LiveChatSessionDTO sessionDTO = buildSessionDTO(session);

        // Publish SESSION_ACCEPTED event
        String visitorName = visitorRepository.findById(session.getVisitorId())
                .map(LiveChatVisitor::getName)
                .orElse(null);
        String visitorEmail = visitorRepository.findById(session.getVisitorId())
                .map(LiveChatVisitor::getEmail)
                .orElse(null);

        publishEvent(LiveChatEventDTO.builder()
                .eventType("SESSION_ACCEPTED")
                .tenantId(session.getTenantId() != null ? session.getTenantId().toString() : null)
                .sessionId(session.getId())
                .sessionToken(session.getSessionToken())
                .visitorName(visitorName)
                .visitorEmail(visitorEmail)
                .assignedAgentId(agentChatUserId)
                .agentName(agentName)
                .sourceService(session.getSourceService())
                .timestamp(now)
                .build());

        // Notify visitor via WS: session topic
        messagingTemplate.convertAndSend(
                "/topic/livechat.session." + session.getSessionToken(),
                (Object) Map.of(
                        "type", "agent_joined",
                        "agentName", agentName,
                        "sessionId", sessionId
                )
        );

        // Remove from waiting queue via WS
        messagingTemplate.convertAndSend(
                "/topic/livechat.waiting." + session.getTenantId(),
                (Object) Map.of(
                        "type", "session_accepted",
                        "sessionId", sessionId
                )
        );

        log.info("Live chat accepted: sessionId={}, agentId={}, agentName={}", sessionId, agentChatUserId, agentName);

        return sessionDTO;
    }

    @Transactional
    public LiveChatMessageDTO sendAgentMessage(Long sessionId, SendLiveChatMessageRequest request, Long agentChatUserId) {
        LiveChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        if (!LiveChatSessionStatus.ACTIVE.name().equals(session.getStatus())) {
            throw new IllegalStateException("Can only send messages to ACTIVE sessions, current: " + session.getStatus());
        }

        if (!agentChatUserId.equals(session.getAssignedAgentId())) {
            throw new IllegalArgumentException("Agent is not assigned to this session");
        }

        Instant now = Instant.now();
        String agentName = resolveAgentName(agentChatUserId);

        LiveChatMessage message = LiveChatMessage.builder()
                .sessionId(sessionId)
                .senderType(LiveChatSenderType.AGENT.name())
                .senderId(agentChatUserId)
                .content(request.getContent())
                .messageType(request.getMessageType() != null ? request.getMessageType() : "TEXT")
                .attachmentUrl(request.getAttachmentUrl())
                .attachmentName(request.getAttachmentName())
                .isRead(false)
                .createdAt(now)
                .build();

        LiveChatMessage savedMessage = messageRepository.save(message);

        // Increment message count and update last activity
        sessionRepository.incrementMessageCount(sessionId, now, now);

        LiveChatMessageDTO messageDTO = LiveChatMessageDTO.fromEntity(savedMessage, agentName);

        // Broadcast to session topic (visitor receives this)
        messagingTemplate.convertAndSend(
                "/topic/livechat.session." + session.getSessionToken(),
                messageDTO
        );

        // Route to external channel if applicable
        routeToExternalChannel(session, request.getContent());

        log.debug("Agent message sent: sessionId={}, agentId={}, messageId={}", sessionId, agentChatUserId, savedMessage.getId());

        return messageDTO;
    }

    private void routeToExternalChannel(LiveChatSession session, String content) {
        String channel = session.getExternalChannel();
        if (channel == null || "LIVECHAT".equals(channel)) {
            return;
        }

        String tenantId = session.getTenantId() != null ? session.getTenantId().toString() : null;
        if (tenantId == null) return;

        try {
            if ("WHATSAPP".equals(channel) && session.getExternalContactPhone() != null) {
                whatsAppServiceClient.sendText(tenantId, tenantId,
                        Map.of("recipientPhone", session.getExternalContactPhone(),
                               "content", content,
                               "messageType", "TEXT"));
                log.debug("Routed message to WhatsApp: phone={}", session.getExternalContactPhone());
            } else if ("EMAIL".equals(channel) && session.getExternalContactEmail() != null) {
                emailServiceClient.send(tenantId, tenantId,
                        Map.of("recipientEmail", session.getExternalContactEmail(),
                               "contentText", content,
                               "subject", "Re: Chat"));
                log.debug("Routed message to Email: email={}", session.getExternalContactEmail());
            }
        } catch (Exception e) {
            log.error("Failed to route message to {} channel: {}", channel, e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<LiveChatMessageDTO> getMessagesForAgent(Long sessionId) {
        sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        List<LiveChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        return messages.stream()
                .map(this::buildMessageDTO)
                .toList();
    }

    @Transactional
    public LiveChatSessionDTO transferChat(Long sessionId, Long newAgentChatUserId, Long currentAgentChatUserId) {
        LiveChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        if (!LiveChatSessionStatus.ACTIVE.name().equals(session.getStatus())) {
            throw new IllegalStateException("Can only transfer ACTIVE sessions, current: " + session.getStatus());
        }

        if (!currentAgentChatUserId.equals(session.getAssignedAgentId())) {
            throw new IllegalArgumentException("Current agent is not assigned to this session");
        }

        Instant now = Instant.now();
        String oldAgentName = resolveAgentName(currentAgentChatUserId);
        String newAgentName = resolveAgentName(newAgentChatUserId);

        // Update assigned agent
        sessionRepository.updateAssignedAgent(sessionId, newAgentChatUserId, session.getAgentJoinedAt(), now);

        // Add system message
        addSystemMessage(sessionId, "Chat transferred from " + oldAgentName + " to " + newAgentName);

        // Reload session
        session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session disappeared after update: " + sessionId));

        LiveChatSessionDTO sessionDTO = buildSessionDTO(session);

        // Notify new agent via WS
        ChatUser newAgent = resolveAgentChatUser(newAgentChatUserId);
        if (newAgent != null && newAgent.getExternalUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    newAgent.getExternalUserId().toString(),
                    "/queue/livechat",
                    Map.of(
                            "type", "chat_transferred",
                            "session", sessionDTO
                    )
            );
        }

        // Notify old agent via WS
        ChatUser oldAgent = resolveAgentChatUser(currentAgentChatUserId);
        if (oldAgent != null && oldAgent.getExternalUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    oldAgent.getExternalUserId().toString(),
                    "/queue/livechat",
                    Map.of(
                            "type", "chat_transferred_away",
                            "sessionId", sessionId,
                            "newAgentName", newAgentName
                    )
            );
        }

        // Notify visitor via WS
        messagingTemplate.convertAndSend(
                "/topic/livechat.session." + session.getSessionToken(),
                (Object) Map.of(
                        "type", "agent_changed",
                        "agentName", newAgentName
                )
        );

        log.info("Live chat transferred: sessionId={}, from={} to={}", sessionId, oldAgentName, newAgentName);

        return sessionDTO;
    }

    @Transactional
    public void closeChat(Long sessionId, Long agentChatUserId) {
        LiveChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        Instant now = Instant.now();

        // Close session
        session.setStatus(LiveChatSessionStatus.CLOSED.name());
        session.setClosedAt(now);
        session.setUpdatedAt(now);
        sessionRepository.save(session);

        addSystemMessage(sessionId, "Agent closed the chat");

        // Publish SESSION_CLOSED event with messages snapshot
        String visitorName = visitorRepository.findById(session.getVisitorId())
                .map(LiveChatVisitor::getName)
                .orElse(null);
        String visitorEmail = visitorRepository.findById(session.getVisitorId())
                .map(LiveChatVisitor::getEmail)
                .orElse(null);
        String agentName = resolveAgentName(agentChatUserId);

        publishEvent(LiveChatEventDTO.builder()
                .eventType("SESSION_CLOSED")
                .tenantId(session.getTenantId() != null ? session.getTenantId().toString() : null)
                .sessionId(session.getId())
                .sessionToken(session.getSessionToken())
                .visitorName(visitorName)
                .visitorEmail(visitorEmail)
                .assignedAgentId(agentChatUserId)
                .agentName(agentName)
                .sourceService(session.getSourceService())
                .pageUrl(session.getPageUrl())
                .messagesJson(buildMessagesJson(session.getId()))
                .timestamp(now)
                .build());

        // Notify visitor via WS
        messagingTemplate.convertAndSend(
                "/topic/livechat.session." + session.getSessionToken(),
                (Object) Map.of("type", "session_closed", "closedBy", "agent")
        );

        log.info("Live chat closed by agent: sessionId={}, agentId={}", sessionId, agentChatUserId);
    }

    // ==================== WebSocket Agent Shortcut ====================

    @Transactional
    public void sendAgentMessageViaWs(Long sessionId, SendLiveChatMessageRequest request, String userId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        Long agentId = chatUserRepository.findByExternalUserIdAndTenantId(UUID.fromString(userId), tenantId)
                .orElseThrow(() -> new RuntimeException("Agent not found in chat service"))
                .getId();

        sendAgentMessage(sessionId, request, agentId);
    }

    // ==================== Typing Indicators ====================

    public void notifyVisitorTyping(String sessionToken) {
        LiveChatSession session = sessionRepository.findBySessionToken(sessionToken).orElse(null);
        if (session == null) return;

        // Notify assigned agent if any
        if (session.getAssignedAgentId() != null) {
            ChatUser agent = resolveAgentChatUser(session.getAssignedAgentId());
            if (agent != null && agent.getExternalUserId() != null) {
                messagingTemplate.convertAndSendToUser(
                        agent.getExternalUserId().toString(),
                        "/queue/livechat",
                        Map.of(
                                "type", "visitor_typing",
                                "sessionId", session.getId(),
                                "sessionToken", sessionToken,
                                "typing", true
                        )
                );
            }
        }

        // Broadcast to session topic
        messagingTemplate.convertAndSend(
                "/topic/livechat.session." + sessionToken,
                (Object) Map.of("type", "visitor_typing", "sessionId", session.getId(), "typing", true)
        );
    }

    public void notifyAgentTyping(Long sessionId) {
        LiveChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) return;

        // Notify visitor via session topic
        messagingTemplate.convertAndSend(
                "/topic/livechat.session." + session.getSessionToken(),
                (Object) Map.of("type", "agent_typing", "sessionId", sessionId, "typing", true)
        );
    }

    // ==================== Admin ====================

    @Transactional(readOnly = true)
    public List<LiveChatSessionDTO> getAllSessions(String sourceService) {
        UUID tenantId = TenantContext.getCurrentTenant();
        List<LiveChatSession> sessions = sessionRepository.findAllByTenantId(tenantId, sourceService);
        return sessions.stream()
                .map(this::buildSessionDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public LiveChatSessionDTO getSession(Long sessionId) {
        LiveChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        return buildSessionDTO(session);
    }

    @Transactional(readOnly = true)
    public LiveChatStatsDTO getChatStats() {
        UUID tenantId = TenantContext.getCurrentTenant();
        List<LiveChatSession> allSessions = sessionRepository.findAllByTenantId(tenantId);

        long totalSessions = allSessions.size();
        long activeSessions = allSessions.stream()
                .filter(s -> LiveChatSessionStatus.ACTIVE.name().equals(s.getStatus()))
                .count();
        long waitingSessions = allSessions.stream()
                .filter(s -> LiveChatSessionStatus.WAITING.name().equals(s.getStatus()))
                .count();
        long closedSessions = allSessions.stream()
                .filter(s -> LiveChatSessionStatus.CLOSED.name().equals(s.getStatus()))
                .count();
        long abandonedSessions = allSessions.stream()
                .filter(s -> LiveChatSessionStatus.ABANDONED.name().equals(s.getStatus()))
                .count();

        // Average rating (only rated sessions)
        Double averageRating = allSessions.stream()
                .filter(s -> s.getRating() != null)
                .mapToInt(LiveChatSession::getRating)
                .average()
                .stream()
                .boxed()
                .findFirst()
                .orElse(null);

        // Average response time (time from creation to agent joining)
        Double averageResponseTimeSeconds = allSessions.stream()
                .filter(s -> s.getAgentJoinedAt() != null && s.getCreatedAt() != null)
                .mapToDouble(s -> (double) ChronoUnit.SECONDS.between(s.getCreatedAt(), s.getAgentJoinedAt()))
                .average()
                .stream()
                .boxed()
                .findFirst()
                .orElse(null);

        return LiveChatStatsDTO.builder()
                .totalSessions(totalSessions)
                .activeSessions(activeSessions)
                .waitingSessions(waitingSessions)
                .closedSessions(closedSessions)
                .abandonedSessions(abandonedSessions)
                .averageRating(averageRating)
                .averageResponseTimeSeconds(averageResponseTimeSeconds)
                .build();
    }

    @Transactional
    public LiveChatWidgetConfigDTO updateWidgetConfig(UUID tenantId, LiveChatWidgetConfigDTO configDTO) {
        return updateWidgetConfig(tenantId, configDTO, null);
    }

    @Transactional
    public LiveChatWidgetConfigDTO updateWidgetConfig(UUID tenantId, LiveChatWidgetConfigDTO configDTO, String sourceService) {
        Instant now = Instant.now();

        Optional<LiveChatWidgetConfig> existing;
        if (sourceService != null && !sourceService.isBlank()) {
            existing = widgetConfigRepository.findByTenantIdAndSourceService(tenantId, sourceService);
        } else {
            existing = widgetConfigRepository.findDefaultByTenantId(tenantId);
        }

        LiveChatWidgetConfig config = existing.orElseGet(() -> LiveChatWidgetConfig.builder()
                .tenantId(tenantId)
                .sourceService(sourceService)
                .createdAt(now)
                .build());

        if (configDTO.getEnabled() != null) config.setEnabled(configDTO.getEnabled());
        if (configDTO.getPrimaryColor() != null) config.setPrimaryColor(configDTO.getPrimaryColor());
        if (configDTO.getHeaderText() != null) config.setHeaderText(configDTO.getHeaderText());
        if (configDTO.getWelcomeMessage() != null) config.setWelcomeMessage(configDTO.getWelcomeMessage());
        if (configDTO.getOfflineMessage() != null) config.setOfflineMessage(configDTO.getOfflineMessage());
        if (configDTO.getPosition() != null) config.setPosition(configDTO.getPosition());
        if (configDTO.getRequireEmail() != null) config.setRequireEmail(configDTO.getRequireEmail());
        if (configDTO.getAutoOpenDelaySeconds() != null) config.setAutoOpenDelaySeconds(configDTO.getAutoOpenDelaySeconds());
        config.setUpdatedAt(now);

        LiveChatWidgetConfig saved = widgetConfigRepository.save(config);
        log.info("Widget config updated: tenantId={}, sourceService={}", tenantId, sourceService);
        return LiveChatWidgetConfigDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<LiveChatWidgetConfigDTO> getAllWidgetConfigs(UUID tenantId) {
        return widgetConfigRepository.findAllByTenantId(tenantId).stream()
                .map(LiveChatWidgetConfigDTO::fromEntity)
                .toList();
    }

    // ==================== External Inbound (Omnichannel) ====================

    @Transactional
    public void handleExternalInbound(String tenantId, String channel, String contactPhone,
                                       String contactEmail, String contactName,
                                       String content, String messageType, Long externalConversationId) {
        UUID tenantUuid = UUID.fromString(tenantId);
        Instant now = Instant.now();

        // Find open session for this external contact
        Optional<LiveChatSession> existingSession;
        if ("WHATSAPP".equals(channel)) {
            existingSession = sessionRepository.findOpenByExternalPhone(tenantUuid, contactPhone);
        } else if ("EMAIL".equals(channel)) {
            existingSession = sessionRepository.findOpenByExternalEmail(tenantUuid, contactEmail);
        } else {
            log.warn("Unknown external channel: {}", channel);
            return;
        }

        LiveChatSession session;
        if (existingSession.isPresent()) {
            session = existingSession.get();
        } else {
            // Create visitor
            String visitorId = channel + "_" + (contactPhone != null ? contactPhone : contactEmail);
            LiveChatVisitor visitor = visitorRepository.findByTenantIdAndVisitorId(tenantUuid, visitorId)
                    .orElseGet(() -> visitorRepository.save(LiveChatVisitor.builder()
                            .tenantId(tenantUuid)
                            .visitorId(visitorId)
                            .name(contactName)
                            .email(contactEmail)
                            .createdAt(now)
                            .updatedAt(now)
                            .build()));

            // Update visitor name if changed
            if (contactName != null && !contactName.equals(visitor.getName())) {
                visitor.setName(contactName);
                visitor.setUpdatedAt(now);
                visitorRepository.save(visitor);
            }

            // Create new session
            String sessionToken = UUID.randomUUID().toString().replace("-", "");
            session = sessionRepository.save(LiveChatSession.builder()
                    .tenantId(tenantUuid)
                    .sessionToken(sessionToken)
                    .visitorId(visitor.getId())
                    .sourceService(channel)
                    .status(LiveChatSessionStatus.WAITING.name())
                    .externalChannel(channel)
                    .externalContactPhone(contactPhone)
                    .externalContactEmail(contactEmail)
                    .externalContactName(contactName)
                    .externalConversationId(externalConversationId)
                    .messageCount(0)
                    .lastActivityAt(now)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());

            // Publish SESSION_STARTED
            publishEvent(LiveChatEventDTO.builder()
                    .eventType("SESSION_STARTED")
                    .tenantId(tenantId)
                    .sessionId(session.getId())
                    .sessionToken(session.getSessionToken())
                    .visitorName(contactName)
                    .sourceService(channel)
                    .timestamp(now)
                    .build());

            // Notify agents
            messagingTemplate.convertAndSend(
                    "/topic/livechat.waiting." + tenantUuid,
                    buildSessionDTO(session)
            );

            log.info("External session created: channel={}, contact={}, tenantId={}", channel,
                    contactPhone != null ? contactPhone : contactEmail, tenantId);
        }

        // Add message to session
        LiveChatMessage msg = LiveChatMessage.builder()
                .sessionId(session.getId())
                .senderType(LiveChatSenderType.VISITOR.name())
                .senderId(session.getVisitorId())
                .content(content)
                .messageType(messageType != null ? messageType : "TEXT")
                .isRead(false)
                .createdAt(now)
                .build();
        messageRepository.save(msg);
        sessionRepository.incrementMessageCount(session.getId(), now, now);

        // Notify agent if assigned
        if (session.getAssignedAgentId() != null) {
            ChatUser agent = resolveAgentChatUser(session.getAssignedAgentId());
            if (agent != null && agent.getExternalUserId() != null) {
                LiveChatMessageDTO messageDTO = LiveChatMessageDTO.fromEntity(msg, contactName);
                messagingTemplate.convertAndSendToUser(
                        agent.getExternalUserId().toString(),
                        "/queue/livechat",
                        messageDTO
                );
            }
        }

        // Broadcast to session topic
        messagingTemplate.convertAndSend(
                "/topic/livechat.session." + session.getSessionToken(),
                LiveChatMessageDTO.fromEntity(msg, contactName)
        );
    }

    // ==================== Scheduled ====================

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void handleInactiveSessions() {
        // Use the longest timeout to fetch candidates, then filter per channel
        int maxTimeout = Math.max(livechatTimeoutMinutes, Math.max(whatsappTimeoutMinutes, emailTimeoutMinutes));
        Instant cutoff = Instant.now().minus(maxTimeout, ChronoUnit.MINUTES);
        List<LiveChatSession> inactiveSessions = sessionRepository.findInactiveSessions(cutoff);

        if (inactiveSessions.isEmpty()) {
            return;
        }

        Instant now = Instant.now();

        // Filter by per-channel timeout
        inactiveSessions = inactiveSessions.stream()
                .filter(s -> {
                    int timeout = getTimeoutMinutes(s.getExternalChannel());
                    Instant channelCutoff = now.minus(timeout, ChronoUnit.MINUTES);
                    return s.getLastActivityAt() != null && s.getLastActivityAt().isBefore(channelCutoff);
                })
                .toList();

        if (inactiveSessions.isEmpty()) return;

        log.info("Processing {} inactive live chat sessions", inactiveSessions.size());

        for (LiveChatSession session : inactiveSessions) {
            try {
                session.setStatus(LiveChatSessionStatus.ABANDONED.name());
                session.setClosedAt(now);
                session.setUpdatedAt(now);
                sessionRepository.save(session);

                addSystemMessage(session.getId(), "Session ended due to inactivity");

                // Publish SESSION_ABANDONED event
                String visitorName = visitorRepository.findById(session.getVisitorId())
                        .map(LiveChatVisitor::getName)
                        .orElse(null);
                String abandVisitorEmail = visitorRepository.findById(session.getVisitorId())
                        .map(LiveChatVisitor::getEmail)
                        .orElse(null);

                publishEvent(LiveChatEventDTO.builder()
                        .eventType("SESSION_ABANDONED")
                        .tenantId(session.getTenantId() != null ? session.getTenantId().toString() : null)
                        .sessionId(session.getId())
                        .sessionToken(session.getSessionToken())
                        .visitorName(visitorName)
                        .visitorEmail(abandVisitorEmail)
                        .sourceService(session.getSourceService())
                        .assignedAgentId(session.getAssignedAgentId())
                        .agentName(session.getAssignedAgentId() != null ? resolveAgentName(session.getAssignedAgentId()) : null)
                        .pageUrl(session.getPageUrl())
                        .messagesJson(buildMessagesJson(session.getId()))
                        .timestamp(now)
                        .build());

                // Notify via WS
                messagingTemplate.convertAndSend(
                        "/topic/livechat.session." + session.getSessionToken(),
                        (Object) Map.of("type", "session_abandoned", "reason", "inactivity")
                );

                // Notify assigned agent if any
                if (session.getAssignedAgentId() != null) {
                    ChatUser agent = resolveAgentChatUser(session.getAssignedAgentId());
                    if (agent != null && agent.getExternalUserId() != null) {
                        messagingTemplate.convertAndSendToUser(
                                agent.getExternalUserId().toString(),
                                "/queue/livechat",
                                Map.of(
                                        "type", "session_abandoned",
                                        "sessionId", session.getId(),
                                        "reason", "inactivity"
                                )
                        );
                    }
                }

                log.info("Session abandoned due to inactivity: sessionId={}", session.getId());
            } catch (Exception e) {
                log.error("Failed to process inactive session {}: {}", session.getId(), e.getMessage(), e);
            }
        }
    }

    private int getTimeoutMinutes(String externalChannel) {
        if (externalChannel == null) return livechatTimeoutMinutes;
        return switch (externalChannel) {
            case "WHATSAPP" -> whatsappTimeoutMinutes;
            case "EMAIL" -> emailTimeoutMinutes;
            default -> livechatTimeoutMinutes;
        };
    }

    // ==================== Helper Methods ====================

    private LiveChatSessionDTO buildSessionDTO(LiveChatSession session) {
        String visitorName = null;
        String visitorEmail = null;
        if (session.getVisitorId() != null) {
            LiveChatVisitor visitor = visitorRepository.findById(session.getVisitorId()).orElse(null);
            if (visitor != null) {
                visitorName = visitor.getName();
                visitorEmail = visitor.getEmail();
            }
        }

        String agentName = null;
        if (session.getAssignedAgentId() != null) {
            agentName = resolveAgentName(session.getAssignedAgentId());
        }

        return LiveChatSessionDTO.fromEntity(session, visitorName, visitorEmail, agentName);
    }

    private LiveChatMessageDTO buildMessageDTO(LiveChatMessage message) {
        String senderName = null;
        if (message.getSenderType() != null) {
            switch (message.getSenderType()) {
                case "VISITOR" -> {
                    if (message.getSenderId() != null) {
                        senderName = visitorRepository.findById(message.getSenderId())
                                .map(LiveChatVisitor::getName)
                                .orElse("Visitor");
                    }
                }
                case "AGENT" -> {
                    if (message.getSenderId() != null) {
                        senderName = resolveAgentName(message.getSenderId());
                    }
                }
                case "SYSTEM" -> senderName = "System";
            }
        }
        return LiveChatMessageDTO.fromEntity(message, senderName);
    }

    private void publishEvent(LiveChatEventDTO event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(LIVECHAT_EVENTS_TOPIC, event.getSessionToken(), json);
            log.debug("Published livechat event: type={}, sessionId={}", event.getEventType(), event.getSessionId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize livechat event: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to publish livechat event to Kafka: {}", e.getMessage(), e);
        }
    }

    private void addSystemMessage(Long sessionId, String content) {
        LiveChatMessage systemMessage = LiveChatMessage.builder()
                .sessionId(sessionId)
                .senderType(LiveChatSenderType.SYSTEM.name())
                .content(content)
                .messageType("SYSTEM")
                .isRead(true)
                .createdAt(Instant.now())
                .build();
        messageRepository.save(systemMessage);
    }

    private ChatUser resolveAgentChatUser(Long agentChatUserId) {
        return chatUserRepository.findById(agentChatUserId).orElse(null);
    }

    private String resolveAgentName(Long agentChatUserId) {
        ChatUser agent = resolveAgentChatUser(agentChatUserId);
        return agent != null ? agent.getName() : "Agent";
    }

    /**
     * Build a JSON snapshot of all messages for a session.
     * Used to enrich SESSION_CLOSED/SESSION_ABANDONED events so consumers
     * (e.g., helpdesk-service) can cache messages locally without Feign calls.
     */
    private String buildMessagesJson(Long sessionId) {
        try {
            List<LiveChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
            List<Map<String, Object>> messageMaps = messages.stream()
                    .map(msg -> {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id", msg.getId());
                        map.put("senderType", msg.getSenderType());
                        map.put("senderName", resolveSenderName(msg));
                        map.put("content", msg.getContent());
                        map.put("messageType", msg.getMessageType());
                        map.put("createdAt", msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : null);
                        return map;
                    })
                    .toList();
            return objectMapper.writeValueAsString(messageMaps);
        } catch (Exception e) {
            log.error("Failed to build messages JSON for session {}: {}", sessionId, e.getMessage(), e);
            return "[]";
        }
    }

    private String resolveSenderName(LiveChatMessage message) {
        if (message.getSenderType() == null) return null;
        return switch (message.getSenderType()) {
            case "VISITOR" -> message.getSenderId() != null
                    ? visitorRepository.findById(message.getSenderId())
                        .map(LiveChatVisitor::getName).orElse("Visitor")
                    : "Visitor";
            case "AGENT" -> message.getSenderId() != null
                    ? resolveAgentName(message.getSenderId())
                    : "Agent";
            case "SYSTEM" -> "System";
            default -> null;
        };
    }
}
