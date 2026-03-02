package com.poc.chat.service;

import com.poc.chat.BaseIntegrationTest;
import com.poc.chat.domain.*;
import com.poc.chat.dto.livechat.*;
import com.poc.chat.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LiveChatLifecycleIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private LiveChatService liveChatService;

    @Autowired
    private LiveChatVisitorRepository visitorRepository;

    @Autowired
    private LiveChatSessionRepository sessionRepository;

    @Autowired
    private LiveChatMessageRepository messageRepository;

    @Autowired
    private LiveChatWidgetConfigRepository widgetConfigRepository;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    private Long agentChatUserId;

    @BeforeEach
    void setUp() {
        cleanLiveChatTables();
        cleanTable("CHAT_USER");

        // Create a test CHAT_USER for agent operations
        Instant now = Instant.now();
        jdbcTemplate.update(
                "INSERT INTO CHAT_USER (EXTERNAL_USER_ID, TENANT_ID, NAME, EMAIL, STATUS, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?)",
                TEST_USER_ID.toString(), TEST_TENANT_ID.toString(), "Test Agent", "agent@test.com", "ONLINE", now, now
        );
        agentChatUserId = jdbcTemplate.queryForObject(
                "SELECT ID FROM CHAT_USER WHERE EXTERNAL_USER_ID = ?",
                Long.class,
                TEST_USER_ID.toString()
        );
    }

    @Nested
    class SessionLifecycle {

        @Test
        void fullLifecycle_startToCloseWithRating() {
            // 1. Start chat
            StartLiveChatRequest startRequest = StartLiveChatRequest.builder()
                    .visitorId("lifecycle-visitor-1")
                    .visitorName("Alice")
                    .visitorEmail("alice@example.com")
                    .initialMessage("Hello, I need help!")
                    .pageUrl("https://example.com/support")
                    .build();

            LiveChatSessionDTO sessionDTO = liveChatService.startChat(TEST_TENANT_ID, startRequest, "127.0.0.1", "TestBrowser/1.0");

            assertThat(sessionDTO).isNotNull();
            assertThat(sessionDTO.getStatus()).isEqualTo("WAITING");
            assertThat(sessionDTO.getSessionToken()).isNotNull();

            Long sessionId = sessionDTO.getId();
            String sessionToken = sessionDTO.getSessionToken();

            // 2. Accept chat (agent accepts)
            LiveChatSessionDTO acceptedDTO = liveChatService.acceptChat(sessionId, agentChatUserId);

            assertThat(acceptedDTO.getStatus()).isEqualTo("ACTIVE");
            assertThat(acceptedDTO.getAssignedAgentId()).isEqualTo(agentChatUserId);

            // 3. Agent sends message
            SendLiveChatMessageRequest agentMsg = SendLiveChatMessageRequest.builder()
                    .content("Hi Alice, how can I help?")
                    .build();
            LiveChatMessageDTO agentMessageDTO = liveChatService.sendAgentMessage(sessionId, agentMsg, agentChatUserId);

            assertThat(agentMessageDTO).isNotNull();
            assertThat(agentMessageDTO.getContent()).isEqualTo("Hi Alice, how can I help?");
            assertThat(agentMessageDTO.getSenderType()).isEqualTo("AGENT");

            // 4. Visitor sends message
            SendLiveChatMessageRequest visitorMsg = SendLiveChatMessageRequest.builder()
                    .content("I have an issue with billing")
                    .build();
            LiveChatMessageDTO visitorMessageDTO = liveChatService.sendVisitorMessage(sessionToken, visitorMsg);

            assertThat(visitorMessageDTO).isNotNull();
            assertThat(visitorMessageDTO.getContent()).isEqualTo("I have an issue with billing");
            assertThat(visitorMessageDTO.getSenderType()).isEqualTo("VISITOR");

            // 5. Close chat
            liveChatService.closeChat(sessionId, agentChatUserId);

            Optional<LiveChatSession> closedSession = sessionRepository.findById(sessionId);
            assertThat(closedSession).isPresent();
            assertThat(closedSession.get().getStatus()).isEqualTo("CLOSED");
            assertThat(closedSession.get().getClosedAt()).isNotNull();

            // 6. Rate chat
            liveChatService.rateChatByVisitor(sessionToken, 5, "Excellent support!");

            Optional<LiveChatSession> ratedSession = sessionRepository.findById(sessionId);
            assertThat(ratedSession).isPresent();
            assertThat(ratedSession.get().getRating()).isEqualTo(5);
            assertThat(ratedSession.get().getFeedback()).isEqualTo("Excellent support!");
        }

        @Test
        void startChat_shouldCreateVisitorSessionAndMessages() {
            // Given
            StartLiveChatRequest request = StartLiveChatRequest.builder()
                    .visitorId("new-visitor-001")
                    .visitorName("Bob")
                    .visitorEmail("bob@example.com")
                    .initialMessage("I need help with my order")
                    .pageUrl("https://example.com/orders")
                    .sourceService("HELPDESK")
                    .build();

            // When
            LiveChatSessionDTO sessionDTO = liveChatService.startChat(TEST_TENANT_ID, request, "192.168.1.1", "Mozilla/5.0");

            // Then - verify session
            assertThat(sessionDTO).isNotNull();
            assertThat(sessionDTO.getId()).isNotNull();
            assertThat(sessionDTO.getSessionToken()).isNotNull().hasSize(32);
            assertThat(sessionDTO.getStatus()).isEqualTo("WAITING");
            assertThat(sessionDTO.getVisitorName()).isEqualTo("Bob");
            assertThat(sessionDTO.getVisitorEmail()).isEqualTo("bob@example.com");

            // Then - verify visitor was created
            Optional<LiveChatVisitor> visitor = visitorRepository.findByTenantIdAndVisitorId(TEST_TENANT_ID, "new-visitor-001");
            assertThat(visitor).isPresent();
            assertThat(visitor.get().getName()).isEqualTo("Bob");
            assertThat(visitor.get().getEmail()).isEqualTo("bob@example.com");

            // Then - verify messages (welcome message + initial message)
            List<LiveChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionDTO.getId());
            assertThat(messages).hasSize(2);

            // Welcome system message
            assertThat(messages.get(0).getSenderType()).isEqualTo("SYSTEM");
            assertThat(messages.get(0).getContent()).isNotBlank();

            // Initial visitor message
            assertThat(messages.get(1).getSenderType()).isEqualTo("VISITOR");
            assertThat(messages.get(1).getContent()).isEqualTo("I need help with my order");
        }

        @Test
        void acceptAndSendMessages_shouldWork() {
            // Given - start a chat first
            StartLiveChatRequest startRequest = StartLiveChatRequest.builder()
                    .visitorId("accept-visitor")
                    .visitorName("Charlie")
                    .visitorEmail("charlie@example.com")
                    .build();

            LiveChatSessionDTO sessionDTO = liveChatService.startChat(TEST_TENANT_ID, startRequest, "10.0.0.1", "TestAgent");
            Long sessionId = sessionDTO.getId();

            // When - accept the chat
            LiveChatSessionDTO acceptedDTO = liveChatService.acceptChat(sessionId, agentChatUserId);

            // Then - verify acceptance
            assertThat(acceptedDTO.getStatus()).isEqualTo("ACTIVE");
            assertThat(acceptedDTO.getAssignedAgentId()).isEqualTo(agentChatUserId);
            assertThat(acceptedDTO.getAgentName()).isEqualTo("Test Agent");

            // When - agent sends a message
            SendLiveChatMessageRequest agentMsg = SendLiveChatMessageRequest.builder()
                    .content("Hello Charlie, I can help you.")
                    .build();
            LiveChatMessageDTO messageDTO = liveChatService.sendAgentMessage(sessionId, agentMsg, agentChatUserId);

            // Then
            assertThat(messageDTO.getContent()).isEqualTo("Hello Charlie, I can help you.");
            assertThat(messageDTO.getSenderType()).isEqualTo("AGENT");
            assertThat(messageDTO.getSenderId()).isEqualTo(agentChatUserId);

            // Verify message count incremented
            Optional<LiveChatSession> session = sessionRepository.findById(sessionId);
            assertThat(session).isPresent();
            assertThat(session.get().getMessageCount()).isGreaterThan(0);

            // Verify messages include system "agent joined" message
            List<LiveChatMessage> allMessages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
            boolean hasAgentJoinedMsg = allMessages.stream()
                    .anyMatch(m -> "SYSTEM".equals(m.getSenderType()) && m.getContent().contains("joined the chat"));
            assertThat(hasAgentJoinedMsg).isTrue();
        }
    }

    @Nested
    class InactivityHandling {

        @Test
        void handleInactiveSessions_shouldMarkAbandoned() {
            // Given - create a session
            StartLiveChatRequest request = StartLiveChatRequest.builder()
                    .visitorId("inactive-visitor")
                    .visitorName("Dave")
                    .visitorEmail("dave@example.com")
                    .build();

            LiveChatSessionDTO sessionDTO = liveChatService.startChat(TEST_TENANT_ID, request, "10.0.0.2", "TestAgent");
            Long sessionId = sessionDTO.getId();

            // Simulate inactivity: set LAST_ACTIVITY_AT to more than 30 minutes ago
            Instant oldTime = Instant.now().minus(60, ChronoUnit.MINUTES);
            jdbcTemplate.update(
                    "UPDATE CHAT_LIVECHAT_SESSION SET LAST_ACTIVITY_AT = ? WHERE ID = ?",
                    oldTime, sessionId
            );

            // When
            liveChatService.handleInactiveSessions();

            // Then
            Optional<LiveChatSession> session = sessionRepository.findById(sessionId);
            assertThat(session).isPresent();
            assertThat(session.get().getStatus()).isEqualTo("ABANDONED");
            assertThat(session.get().getClosedAt()).isNotNull();

            // Verify system message about inactivity
            List<LiveChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
            boolean hasInactivityMsg = messages.stream()
                    .anyMatch(m -> "SYSTEM".equals(m.getSenderType()) && m.getContent().contains("inactivity"));
            assertThat(hasInactivityMsg).isTrue();
        }
    }

    @Nested
    class WidgetConfig {

        @Test
        void updateWidgetConfig_shouldPersist() {
            // Given
            LiveChatWidgetConfigDTO configDTO = LiveChatWidgetConfigDTO.builder()
                    .enabled(true)
                    .primaryColor("#FF0000")
                    .headerText("Need Help?")
                    .welcomeMessage("Welcome! We are here to help.")
                    .offlineMessage("Sorry, we are offline.")
                    .position("BOTTOM_LEFT")
                    .requireEmail(true)
                    .autoOpenDelaySeconds(10)
                    .build();

            // When
            LiveChatWidgetConfigDTO savedConfig = liveChatService.updateWidgetConfig(TEST_TENANT_ID, configDTO);

            // Then
            assertThat(savedConfig).isNotNull();
            assertThat(savedConfig.getId()).isNotNull();
            assertThat(savedConfig.getPrimaryColor()).isEqualTo("#FF0000");
            assertThat(savedConfig.getHeaderText()).isEqualTo("Need Help?");
            assertThat(savedConfig.getWelcomeMessage()).isEqualTo("Welcome! We are here to help.");
            assertThat(savedConfig.getOfflineMessage()).isEqualTo("Sorry, we are offline.");
            assertThat(savedConfig.getPosition()).isEqualTo("BOTTOM_LEFT");
            assertThat(savedConfig.getRequireEmail()).isTrue();
            assertThat(savedConfig.getAutoOpenDelaySeconds()).isEqualTo(10);

            // Verify persisted in DB
            Optional<LiveChatWidgetConfig> fromDb = widgetConfigRepository.findByTenantId(TEST_TENANT_ID);
            assertThat(fromDb).isPresent();
            assertThat(fromDb.get().getPrimaryColor()).isEqualTo("#FF0000");
            assertThat(fromDb.get().getHeaderText()).isEqualTo("Need Help?");
        }

        @Test
        void getWidgetConfig_shouldReturnDefaultsIfNotFound() {
            // Given - no config saved for this tenant

            // When
            LiveChatWidgetConfigDTO config = liveChatService.getWidgetConfig(TEST_TENANT_ID);

            // Then
            assertThat(config).isNotNull();
            assertThat(config.getTenantId()).isEqualTo(TEST_TENANT_ID.toString());
            assertThat(config.getEnabled()).isTrue();
            assertThat(config.getPrimaryColor()).isEqualTo("#4F46E5");
            assertThat(config.getHeaderText()).isEqualTo("Chat with us");
            assertThat(config.getWelcomeMessage()).isEqualTo("Hello! How can we help you?");
            assertThat(config.getOfflineMessage()).isEqualTo("We are currently offline. Please leave a message.");
            assertThat(config.getPosition()).isEqualTo("BOTTOM_RIGHT");
            assertThat(config.getRequireEmail()).isFalse();
        }
    }
}
