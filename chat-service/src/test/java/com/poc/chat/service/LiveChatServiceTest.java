package com.poc.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.chat.BaseUnitTest;
import com.poc.chat.domain.*;
import com.poc.chat.dto.livechat.*;
import com.poc.chat.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LiveChatServiceTest extends BaseUnitTest {

    @Mock
    private LiveChatVisitorRepository visitorRepository;

    @Mock
    private LiveChatSessionRepository sessionRepository;

    @Mock
    private LiveChatMessageRepository messageRepository;

    @Mock
    private LiveChatWidgetConfigRepository widgetConfigRepository;

    @Mock
    private ChatUserRepository chatUserRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LiveChatService liveChatService;

    // ==================== startChat ====================

    @Test
    void startChat_shouldCreateNewVisitorAndSession() throws JsonProcessingException {
        UUID tenantId = TEST_TENANT_ID;
        StartLiveChatRequest request = StartLiveChatRequest.builder()
                .visitorId("visitor-abc")
                .visitorName("John Doe")
                .visitorEmail("john@example.com")
                .initialMessage("Hello, I need help!")
                .pageUrl("https://example.com/page")
                .queueId("default")
                .sourceService("HELPDESK")
                .build();

        LiveChatVisitor savedVisitor = buildVisitor(1L, tenantId, "visitor-abc", "John Doe", "john@example.com");

        when(visitorRepository.findByTenantIdAndVisitorId(tenantId, "visitor-abc"))
                .thenReturn(Optional.empty());
        when(visitorRepository.save(any(LiveChatVisitor.class))).thenReturn(savedVisitor);

        LiveChatSession savedSession = buildSession(10L, tenantId, 1L, "WAITING", "token123");
        when(sessionRepository.save(any(LiveChatSession.class))).thenReturn(savedSession);

        when(widgetConfigRepository.findByTenantId(tenantId)).thenReturn(Optional.empty());
        when(messageRepository.save(any(LiveChatMessage.class))).thenAnswer(invocation -> {
            LiveChatMessage msg = invocation.getArgument(0);
            msg.setId(100L);
            return msg;
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        LiveChatSessionDTO result = liveChatService.startChat(tenantId, request, "127.0.0.1", "Mozilla/5.0");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getVisitorName()).isEqualTo("John Doe");
        assertThat(result.getVisitorEmail()).isEqualTo("john@example.com");
        assertThat(result.getStatus()).isEqualTo("WAITING");

        // Verify new visitor was saved
        verify(visitorRepository).save(any(LiveChatVisitor.class));
        // Verify session was saved
        verify(sessionRepository).save(any(LiveChatSession.class));
        // Verify welcome system message + initial visitor message = 2 message saves
        verify(messageRepository, times(2)).save(any(LiveChatMessage.class));
        // Verify message count was incremented for the initial message
        verify(sessionRepository).incrementMessageCount(eq(10L), any(Instant.class), any(Instant.class));
        // Verify Kafka event was published
        verify(kafkaTemplate).send(eq("livechat-events"), anyString(), eq("{}"));
        // Verify WS notification to waiting queue
        verify(messagingTemplate).convertAndSend(eq("/topic/livechat.waiting." + tenantId), any(LiveChatSessionDTO.class));
    }

    @Test
    void startChat_shouldReuseExistingVisitor() throws JsonProcessingException {
        UUID tenantId = TEST_TENANT_ID;
        StartLiveChatRequest request = StartLiveChatRequest.builder()
                .visitorId("visitor-existing")
                .visitorName("Jane Doe")
                .visitorEmail("jane@example.com")
                .build();

        LiveChatVisitor existingVisitor = buildVisitor(5L, tenantId, "visitor-existing", "Jane Doe", "jane@example.com");

        when(visitorRepository.findByTenantIdAndVisitorId(tenantId, "visitor-existing"))
                .thenReturn(Optional.of(existingVisitor));

        LiveChatSession savedSession = buildSession(20L, tenantId, 5L, "WAITING", "token456");
        when(sessionRepository.save(any(LiveChatSession.class))).thenReturn(savedSession);

        when(widgetConfigRepository.findByTenantId(tenantId)).thenReturn(Optional.empty());
        when(messageRepository.save(any(LiveChatMessage.class))).thenAnswer(invocation -> {
            LiveChatMessage msg = invocation.getArgument(0);
            msg.setId(200L);
            return msg;
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        LiveChatSessionDTO result = liveChatService.startChat(tenantId, request, "10.0.0.1", "Chrome");

        assertThat(result).isNotNull();
        assertThat(result.getVisitorId()).isEqualTo(5L);

        // Verify the existing visitor was NOT re-saved (name and email match, no update needed)
        verify(visitorRepository, never()).save(any(LiveChatVisitor.class));
        // Verify session was still created
        verify(sessionRepository).save(any(LiveChatSession.class));
    }

    // ==================== resumeChat ====================

    @Test
    void resumeChat_shouldReturnSessionDTO() {
        String sessionToken = "valid-token-123";
        LiveChatSession session = buildSession(30L, TEST_TENANT_ID, 1L, "ACTIVE", sessionToken);
        session.setAssignedAgentId(10L);

        LiveChatVisitor visitor = buildVisitor(1L, TEST_TENANT_ID, "visitor-1", "Alice", "alice@example.com");
        ChatUser agent = buildChatUser(10L, UUID.randomUUID(), "Agent Smith");

        when(sessionRepository.findBySessionToken(sessionToken)).thenReturn(Optional.of(session));
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(chatUserRepository.findById(10L)).thenReturn(Optional.of(agent));

        LiveChatSessionDTO result = liveChatService.resumeChat(sessionToken);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(30L);
        assertThat(result.getVisitorName()).isEqualTo("Alice");
        assertThat(result.getVisitorEmail()).isEqualTo("alice@example.com");
        assertThat(result.getAgentName()).isEqualTo("Agent Smith");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void resumeChat_shouldThrow_whenTokenInvalid() {
        String invalidToken = "invalid-token";
        when(sessionRepository.findBySessionToken(invalidToken)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> liveChatService.resumeChat(invalidToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Session not found: " + invalidToken);
    }

    // ==================== sendVisitorMessage ====================

    @Test
    void sendVisitorMessage_shouldSaveAndNotify() {
        String sessionToken = "active-session-token";
        LiveChatSession session = buildSession(40L, TEST_TENANT_ID, 1L, "ACTIVE", sessionToken);
        session.setAssignedAgentId(10L);

        ChatUser agent = buildChatUser(10L, UUID.randomUUID(), "Agent Bob");
        LiveChatVisitor visitor = buildVisitor(1L, TEST_TENANT_ID, "visitor-1", "Visitor Tom", "tom@example.com");

        SendLiveChatMessageRequest request = SendLiveChatMessageRequest.builder()
                .content("I have a question")
                .messageType("TEXT")
                .build();

        when(sessionRepository.findBySessionToken(sessionToken)).thenReturn(Optional.of(session));
        when(chatUserRepository.findById(10L)).thenReturn(Optional.of(agent));
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(messageRepository.save(any(LiveChatMessage.class))).thenAnswer(invocation -> {
            LiveChatMessage msg = invocation.getArgument(0);
            msg.setId(300L);
            return msg;
        });

        LiveChatMessageDTO result = liveChatService.sendVisitorMessage(sessionToken, request);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("I have a question");
        assertThat(result.getSenderType()).isEqualTo("VISITOR");
        assertThat(result.getSenderName()).isEqualTo("Visitor Tom");

        // Verify message saved
        verify(messageRepository).save(any(LiveChatMessage.class));
        // Verify message count incremented
        verify(sessionRepository).incrementMessageCount(eq(40L), any(Instant.class), any(Instant.class));
        // Verify WS notification to assigned agent
        verify(messagingTemplate).convertAndSendToUser(
                eq(agent.getExternalUserId().toString()),
                eq("/queue/livechat"),
                any(LiveChatMessageDTO.class)
        );
        // Verify broadcast to session topic
        verify(messagingTemplate).convertAndSend(
                eq("/topic/livechat.session." + sessionToken),
                any(LiveChatMessageDTO.class)
        );
    }

    @Test
    void sendVisitorMessage_shouldThrow_whenSessionClosed() {
        String sessionToken = "closed-session-token";
        LiveChatSession session = buildSession(41L, TEST_TENANT_ID, 1L, "CLOSED", sessionToken);

        SendLiveChatMessageRequest request = SendLiveChatMessageRequest.builder()
                .content("Can I send this?")
                .build();

        when(sessionRepository.findBySessionToken(sessionToken)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> liveChatService.sendVisitorMessage(sessionToken, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot send messages to a CLOSED session");
    }

    // ==================== acceptChat ====================

    @Test
    void acceptChat_shouldAssignAgentAndSetActive() throws JsonProcessingException {
        Long sessionId = 50L;
        Long agentChatUserId = 10L;

        LiveChatSession waitingSession = buildSession(sessionId, TEST_TENANT_ID, 1L, "WAITING", "token-waiting");
        LiveChatSession activeSession = buildSession(sessionId, TEST_TENANT_ID, 1L, "ACTIVE", "token-waiting");
        activeSession.setAssignedAgentId(agentChatUserId);

        ChatUser agent = buildChatUser(agentChatUserId, UUID.randomUUID(), "Agent Clark");
        LiveChatVisitor visitor = buildVisitor(1L, TEST_TENANT_ID, "visitor-1", "Visitor Amy", "amy@example.com");

        when(sessionRepository.findById(sessionId))
                .thenReturn(Optional.of(waitingSession))
                .thenReturn(Optional.of(activeSession));
        when(chatUserRepository.findById(agentChatUserId)).thenReturn(Optional.of(agent));
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(messageRepository.save(any(LiveChatMessage.class))).thenAnswer(invocation -> {
            LiveChatMessage msg = invocation.getArgument(0);
            msg.setId(400L);
            return msg;
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        LiveChatSessionDTO result = liveChatService.acceptChat(sessionId, agentChatUserId);

        assertThat(result).isNotNull();
        assertThat(result.getAssignedAgentId()).isEqualTo(agentChatUserId);
        assertThat(result.getAgentName()).isEqualTo("Agent Clark");

        // Verify agent was assigned and status updated
        verify(sessionRepository).updateAssignedAgent(eq(sessionId), eq(agentChatUserId), any(Instant.class), any(Instant.class));
        verify(sessionRepository).updateStatus(eq(sessionId), eq("ACTIVE"), any(Instant.class));
        // Verify system message about agent joining
        ArgumentCaptor<LiveChatMessage> msgCaptor = ArgumentCaptor.forClass(LiveChatMessage.class);
        verify(messageRepository).save(msgCaptor.capture());
        assertThat(msgCaptor.getValue().getContent()).contains("Agent Clark joined the chat");
        // Verify Kafka event
        verify(kafkaTemplate).send(eq("livechat-events"), anyString(), eq("{}"));
        // Verify WS notifications (session topic + waiting topic)
        verify(messagingTemplate).convertAndSend(eq("/topic/livechat.session.token-waiting"), any(Object.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/livechat.waiting." + TEST_TENANT_ID), any(Object.class));
    }

    @Test
    void acceptChat_shouldThrow_whenNotWaiting() {
        Long sessionId = 51L;
        Long agentChatUserId = 10L;

        LiveChatSession activeSession = buildSession(sessionId, TEST_TENANT_ID, 1L, "ACTIVE", "token-active");
        activeSession.setAssignedAgentId(99L);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(activeSession));

        assertThatThrownBy(() -> liveChatService.acceptChat(sessionId, agentChatUserId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Can only accept sessions in WAITING status, current: ACTIVE");
    }

    // ==================== sendAgentMessage ====================

    @Test
    void sendAgentMessage_shouldSaveAndBroadcast() {
        Long sessionId = 60L;
        Long agentChatUserId = 10L;
        String sessionToken = "token-agent-msg";

        LiveChatSession session = buildSession(sessionId, TEST_TENANT_ID, 1L, "ACTIVE", sessionToken);
        session.setAssignedAgentId(agentChatUserId);

        ChatUser agent = buildChatUser(agentChatUserId, UUID.randomUUID(), "Agent Diana");

        SendLiveChatMessageRequest request = SendLiveChatMessageRequest.builder()
                .content("How can I help you?")
                .messageType("TEXT")
                .build();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(chatUserRepository.findById(agentChatUserId)).thenReturn(Optional.of(agent));
        when(messageRepository.save(any(LiveChatMessage.class))).thenAnswer(invocation -> {
            LiveChatMessage msg = invocation.getArgument(0);
            msg.setId(500L);
            return msg;
        });

        LiveChatMessageDTO result = liveChatService.sendAgentMessage(sessionId, request, agentChatUserId);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("How can I help you?");
        assertThat(result.getSenderType()).isEqualTo("AGENT");
        assertThat(result.getSenderName()).isEqualTo("Agent Diana");

        // Verify message saved
        verify(messageRepository).save(any(LiveChatMessage.class));
        // Verify message count incremented
        verify(sessionRepository).incrementMessageCount(eq(sessionId), any(Instant.class), any(Instant.class));
        // Verify broadcast to session topic
        verify(messagingTemplate).convertAndSend(
                eq("/topic/livechat.session." + sessionToken),
                any(LiveChatMessageDTO.class)
        );
    }

    @Test
    void sendAgentMessage_shouldThrow_whenWrongAgent() {
        Long sessionId = 61L;
        Long wrongAgentId = 99L;

        LiveChatSession session = buildSession(sessionId, TEST_TENANT_ID, 1L, "ACTIVE", "token-wrong-agent");
        session.setAssignedAgentId(10L);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        SendLiveChatMessageRequest request = SendLiveChatMessageRequest.builder()
                .content("Hello")
                .build();

        assertThatThrownBy(() -> liveChatService.sendAgentMessage(sessionId, request, wrongAgentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Agent is not assigned to this session");
    }

    // ==================== transferChat ====================

    @Test
    void transferChat_shouldChangeAgent() {
        Long sessionId = 70L;
        Long currentAgentId = 10L;
        Long newAgentId = 20L;
        String sessionToken = "token-transfer";

        LiveChatSession session = buildSession(sessionId, TEST_TENANT_ID, 1L, "ACTIVE", sessionToken);
        session.setAssignedAgentId(currentAgentId);
        session.setAgentJoinedAt(Instant.now().minus(10, ChronoUnit.MINUTES));

        LiveChatSession updatedSession = buildSession(sessionId, TEST_TENANT_ID, 1L, "ACTIVE", sessionToken);
        updatedSession.setAssignedAgentId(newAgentId);

        ChatUser oldAgent = buildChatUser(currentAgentId, UUID.randomUUID(), "Agent Old");
        ChatUser newAgent = buildChatUser(newAgentId, UUID.randomUUID(), "Agent New");
        LiveChatVisitor visitor = buildVisitor(1L, TEST_TENANT_ID, "visitor-1", "Visitor X", "x@example.com");

        when(sessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session))
                .thenReturn(Optional.of(updatedSession));
        when(chatUserRepository.findById(currentAgentId)).thenReturn(Optional.of(oldAgent));
        when(chatUserRepository.findById(newAgentId)).thenReturn(Optional.of(newAgent));
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(messageRepository.save(any(LiveChatMessage.class))).thenAnswer(invocation -> {
            LiveChatMessage msg = invocation.getArgument(0);
            msg.setId(600L);
            return msg;
        });

        LiveChatSessionDTO result = liveChatService.transferChat(sessionId, newAgentId, currentAgentId);

        assertThat(result).isNotNull();
        assertThat(result.getAssignedAgentId()).isEqualTo(newAgentId);

        // Verify agent was updated
        verify(sessionRepository).updateAssignedAgent(eq(sessionId), eq(newAgentId), any(Instant.class), any(Instant.class));
        // Verify system message about transfer
        ArgumentCaptor<LiveChatMessage> msgCaptor = ArgumentCaptor.forClass(LiveChatMessage.class);
        verify(messageRepository).save(msgCaptor.capture());
        assertThat(msgCaptor.getValue().getContent()).contains("Chat transferred from Agent Old to Agent New");
        // Verify WS notification to new agent
        verify(messagingTemplate).convertAndSendToUser(
                eq(newAgent.getExternalUserId().toString()),
                eq("/queue/livechat"),
                anyMap()
        );
        // Verify WS notification to old agent
        verify(messagingTemplate).convertAndSendToUser(
                eq(oldAgent.getExternalUserId().toString()),
                eq("/queue/livechat"),
                anyMap()
        );
        // Verify visitor notification via session topic
        verify(messagingTemplate).convertAndSend(
                eq("/topic/livechat.session." + sessionToken),
                any(Object.class)
        );
    }

    // ==================== closeChat ====================

    @Test
    void closeChat_shouldSetClosedStatus() throws JsonProcessingException {
        Long sessionId = 80L;
        Long agentChatUserId = 10L;
        String sessionToken = "token-close";

        LiveChatSession session = buildSession(sessionId, TEST_TENANT_ID, 1L, "ACTIVE", sessionToken);
        session.setAssignedAgentId(agentChatUserId);

        LiveChatVisitor visitor = buildVisitor(1L, TEST_TENANT_ID, "visitor-1", "Visitor Close", "close@example.com");
        ChatUser agent = buildChatUser(agentChatUserId, UUID.randomUUID(), "Agent Closer");

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(LiveChatSession.class))).thenReturn(session);
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(chatUserRepository.findById(agentChatUserId)).thenReturn(Optional.of(agent));
        when(messageRepository.save(any(LiveChatMessage.class))).thenAnswer(invocation -> {
            LiveChatMessage msg = invocation.getArgument(0);
            msg.setId(700L);
            return msg;
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        liveChatService.closeChat(sessionId, agentChatUserId);

        // Verify session status was set to CLOSED
        ArgumentCaptor<LiveChatSession> sessionCaptor = ArgumentCaptor.forClass(LiveChatSession.class);
        verify(sessionRepository).save(sessionCaptor.capture());
        assertThat(sessionCaptor.getValue().getStatus()).isEqualTo("CLOSED");
        assertThat(sessionCaptor.getValue().getClosedAt()).isNotNull();

        // Verify system message
        ArgumentCaptor<LiveChatMessage> msgCaptor = ArgumentCaptor.forClass(LiveChatMessage.class);
        verify(messageRepository).save(msgCaptor.capture());
        assertThat(msgCaptor.getValue().getContent()).isEqualTo("Agent closed the chat");

        // Verify Kafka event published
        verify(kafkaTemplate).send(eq("livechat-events"), anyString(), eq("{}"));

        // Verify visitor WS notification
        verify(messagingTemplate).convertAndSend(
                eq("/topic/livechat.session." + sessionToken),
                any(Object.class)
        );
    }

    // ==================== endChatByVisitor ====================

    @Test
    void endChatByVisitor_shouldCloseAndNotify() throws JsonProcessingException {
        String sessionToken = "token-visitor-end";
        Long sessionId = 90L;
        Long agentId = 10L;

        LiveChatSession session = buildSession(sessionId, TEST_TENANT_ID, 1L, "ACTIVE", sessionToken);
        session.setAssignedAgentId(agentId);

        LiveChatVisitor visitor = buildVisitor(1L, TEST_TENANT_ID, "visitor-1", "Visitor End", "end@example.com");
        ChatUser agent = buildChatUser(agentId, UUID.randomUUID(), "Agent End");

        when(sessionRepository.findBySessionToken(sessionToken)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(LiveChatSession.class))).thenReturn(session);
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(chatUserRepository.findById(agentId)).thenReturn(Optional.of(agent));
        when(messageRepository.save(any(LiveChatMessage.class))).thenAnswer(invocation -> {
            LiveChatMessage msg = invocation.getArgument(0);
            msg.setId(800L);
            return msg;
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        liveChatService.endChatByVisitor(sessionToken);

        // Verify session was saved with CLOSED status
        ArgumentCaptor<LiveChatSession> sessionCaptor = ArgumentCaptor.forClass(LiveChatSession.class);
        verify(sessionRepository).save(sessionCaptor.capture());
        assertThat(sessionCaptor.getValue().getStatus()).isEqualTo("CLOSED");
        assertThat(sessionCaptor.getValue().getClosedAt()).isNotNull();

        // Verify system message
        ArgumentCaptor<LiveChatMessage> msgCaptor = ArgumentCaptor.forClass(LiveChatMessage.class);
        verify(messageRepository).save(msgCaptor.capture());
        assertThat(msgCaptor.getValue().getContent()).isEqualTo("Visitor ended the chat");

        // Verify Kafka event published
        verify(kafkaTemplate).send(eq("livechat-events"), anyString(), eq("{}"));

        // Verify agent WS notification
        verify(messagingTemplate).convertAndSendToUser(
                eq(agent.getExternalUserId().toString()),
                eq("/queue/livechat"),
                anyMap()
        );

        // Verify session topic broadcast
        verify(messagingTemplate).convertAndSend(
                eq("/topic/livechat.session." + sessionToken),
                any(Object.class)
        );
    }

    // ==================== rateChatByVisitor ====================

    @Test
    void rateChatByVisitor_shouldSaveRating() throws JsonProcessingException {
        String sessionToken = "token-rate";
        Long sessionId = 100L;
        Long agentId = 10L;

        LiveChatSession session = buildSession(sessionId, TEST_TENANT_ID, 1L, "CLOSED", sessionToken);
        session.setAssignedAgentId(agentId);

        LiveChatVisitor visitor = buildVisitor(1L, TEST_TENANT_ID, "visitor-1", "Visitor Rate", "rate@example.com");
        ChatUser agent = buildChatUser(agentId, UUID.randomUUID(), "Agent Rate");

        when(sessionRepository.findBySessionToken(sessionToken)).thenReturn(Optional.of(session));
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(chatUserRepository.findById(agentId)).thenReturn(Optional.of(agent));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        liveChatService.rateChatByVisitor(sessionToken, 5, "Excellent service!");

        // Verify rating was saved
        verify(sessionRepository).updateRating(eq(sessionId), eq(5), eq("Excellent service!"), any(Instant.class));

        // Verify Kafka event published
        verify(kafkaTemplate).send(eq("livechat-events"), anyString(), eq("{}"));
    }

    @Test
    void rateChatByVisitor_shouldThrow_whenNotClosed() {
        String sessionToken = "token-rate-active";

        LiveChatSession session = buildSession(101L, TEST_TENANT_ID, 1L, "ACTIVE", sessionToken);

        when(sessionRepository.findBySessionToken(sessionToken)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> liveChatService.rateChatByVisitor(sessionToken, 5, "Great"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Can only rate closed sessions");
    }

    // ==================== getWidgetConfig ====================

    @Test
    void getWidgetConfig_shouldReturnDefault_whenNotFound() {
        UUID tenantId = TEST_TENANT_ID;
        when(widgetConfigRepository.findByTenantId(tenantId)).thenReturn(Optional.empty());

        LiveChatWidgetConfigDTO result = liveChatService.getWidgetConfig(tenantId);

        assertThat(result).isNotNull();
        assertThat(result.getTenantId()).isEqualTo(tenantId.toString());
        assertThat(result.getEnabled()).isTrue();
        assertThat(result.getPrimaryColor()).isEqualTo("#4F46E5");
        assertThat(result.getHeaderText()).isEqualTo("Chat with us");
        assertThat(result.getWelcomeMessage()).isEqualTo("Hello! How can we help you?");
        assertThat(result.getOfflineMessage()).isEqualTo("We are currently offline. Please leave a message.");
        assertThat(result.getPosition()).isEqualTo("BOTTOM_RIGHT");
        assertThat(result.getRequireEmail()).isFalse();
    }

    // ==================== handleInactiveSessions ====================

    @Test
    void handleInactiveSessions_shouldMarkAbandoned() throws JsonProcessingException {
        Long sessionId = 110L;
        Long agentId = 10L;
        String sessionToken = "token-inactive";

        LiveChatSession inactiveSession = buildSession(sessionId, TEST_TENANT_ID, 1L, "ACTIVE", sessionToken);
        inactiveSession.setAssignedAgentId(agentId);
        inactiveSession.setLastActivityAt(Instant.now().minus(60, ChronoUnit.MINUTES));

        LiveChatVisitor visitor = buildVisitor(1L, TEST_TENANT_ID, "visitor-1", "Visitor Inactive", "inactive@example.com");
        ChatUser agent = buildChatUser(agentId, UUID.randomUUID(), "Agent Inactive");

        when(sessionRepository.findInactiveSessions(any(Instant.class)))
                .thenReturn(List.of(inactiveSession));
        when(sessionRepository.save(any(LiveChatSession.class))).thenReturn(inactiveSession);
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(chatUserRepository.findById(agentId)).thenReturn(Optional.of(agent));
        when(messageRepository.save(any(LiveChatMessage.class))).thenAnswer(invocation -> {
            LiveChatMessage msg = invocation.getArgument(0);
            msg.setId(900L);
            return msg;
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        liveChatService.handleInactiveSessions();

        // Verify session was saved with ABANDONED status
        ArgumentCaptor<LiveChatSession> sessionCaptor = ArgumentCaptor.forClass(LiveChatSession.class);
        verify(sessionRepository).save(sessionCaptor.capture());
        assertThat(sessionCaptor.getValue().getStatus()).isEqualTo("ABANDONED");
        assertThat(sessionCaptor.getValue().getClosedAt()).isNotNull();

        // Verify system message
        ArgumentCaptor<LiveChatMessage> msgCaptor = ArgumentCaptor.forClass(LiveChatMessage.class);
        verify(messageRepository).save(msgCaptor.capture());
        assertThat(msgCaptor.getValue().getContent()).isEqualTo("Session ended due to inactivity");

        // Verify Kafka event published with SESSION_ABANDONED type
        verify(kafkaTemplate).send(eq("livechat-events"), anyString(), eq("{}"));

        // Verify session topic WS notification
        verify(messagingTemplate).convertAndSend(
                eq("/topic/livechat.session." + sessionToken),
                any(Object.class)
        );

        // Verify agent was notified via WS
        verify(messagingTemplate).convertAndSendToUser(
                eq(agent.getExternalUserId().toString()),
                eq("/queue/livechat"),
                anyMap()
        );
    }

    // ==================== Helper Methods ====================

    private LiveChatVisitor buildVisitor(Long id, UUID tenantId, String visitorId, String name, String email) {
        return LiveChatVisitor.builder()
                .id(id)
                .tenantId(tenantId)
                .visitorId(visitorId)
                .name(name)
                .email(email)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private LiveChatSession buildSession(Long id, UUID tenantId, Long visitorId, String status, String sessionToken) {
        return LiveChatSession.builder()
                .id(id)
                .tenantId(tenantId)
                .visitorId(visitorId)
                .status(status)
                .sessionToken(sessionToken)
                .sourceService("HELPDESK")
                .messageCount(0)
                .lastActivityAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private LiveChatMessage buildMessage(Long id, Long sessionId, String senderType, Long senderId, String content) {
        return LiveChatMessage.builder()
                .id(id)
                .sessionId(sessionId)
                .senderType(senderType)
                .senderId(senderId)
                .content(content)
                .messageType("TEXT")
                .isRead(false)
                .createdAt(Instant.now())
                .build();
    }

    private ChatUser buildChatUser(Long id, UUID externalUserId, String name) {
        return ChatUser.builder()
                .id(id)
                .externalUserId(externalUserId)
                .tenantId(TEST_TENANT_ID)
                .name(name)
                .email(name.toLowerCase().replace(" ", ".") + "@test.com")
                .status("ONLINE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private LiveChatWidgetConfig buildWidgetConfig(Long id, UUID tenantId) {
        return LiveChatWidgetConfig.builder()
                .id(id)
                .tenantId(tenantId)
                .enabled(true)
                .primaryColor("#4F46E5")
                .headerText("Chat with us")
                .welcomeMessage("Hello! How can we help you?")
                .offlineMessage("We are currently offline. Please leave a message.")
                .position("BOTTOM_RIGHT")
                .requireEmail(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
