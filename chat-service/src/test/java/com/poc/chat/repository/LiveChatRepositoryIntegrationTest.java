package com.poc.chat.repository;

import com.poc.chat.BaseIntegrationTest;
import com.poc.chat.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LiveChatRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private LiveChatVisitorRepository visitorRepository;

    @Autowired
    private LiveChatSessionRepository sessionRepository;

    @Autowired
    private LiveChatMessageRepository messageRepository;

    @Autowired
    private LiveChatWidgetConfigRepository widgetConfigRepository;

    @Autowired
    private ChatUserRepository chatUserRepository;

    @BeforeEach
    void setUp() {
        cleanLiveChatTables();
        cleanTable("CHAT_USER");
    }

    // ==================== Visitor Repository Tests ====================

    @Test
    void visitorRepo_shouldSaveAndFindByTenantAndVisitorId() {
        // Given
        String visitorId = "visitor-abc-123";
        LiveChatVisitor visitor = LiveChatVisitor.builder()
                .tenantId(TEST_TENANT_ID)
                .visitorId(visitorId)
                .name("John Doe")
                .email("john@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        visitorRepository.save(visitor);

        // When
        Optional<LiveChatVisitor> found = visitorRepository.findByTenantIdAndVisitorId(TEST_TENANT_ID, visitorId);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
        assertThat(found.get().getVisitorId()).isEqualTo(visitorId);
        assertThat(found.get().getTenantId()).isEqualTo(TEST_TENANT_ID);
    }

    @Test
    void visitorRepo_shouldReturnEmpty_whenDifferentTenant() {
        // Given
        UUID otherTenantId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        String visitorId = "visitor-abc-123";

        LiveChatVisitor visitor = LiveChatVisitor.builder()
                .tenantId(otherTenantId)
                .visitorId(visitorId)
                .name("John Doe")
                .email("john@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        visitorRepository.save(visitor);

        // When
        Optional<LiveChatVisitor> found = visitorRepository.findByTenantIdAndVisitorId(TEST_TENANT_ID, visitorId);

        // Then
        assertThat(found).isEmpty();
    }

    // ==================== Session Repository Tests ====================

    @Test
    void sessionRepo_shouldFindByToken() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-1");
        String token = "abc123def456";

        LiveChatSession session = LiveChatSession.builder()
                .tenantId(TEST_TENANT_ID)
                .sessionToken(token)
                .visitorId(visitor.getId())
                .status(LiveChatSessionStatus.WAITING.name())
                .sourceService("HELPDESK")
                .messageCount(0)
                .lastActivityAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        sessionRepository.save(session);

        // When
        Optional<LiveChatSession> found = sessionRepository.findBySessionToken(token);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getSessionToken()).isEqualTo(token);
        assertThat(found.get().getStatus()).isEqualTo("WAITING");
    }

    @Test
    void sessionRepo_shouldFindByTenantAndStatus() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-2");

        LiveChatSession waitingSession = createSession(visitor.getId(), "token-w1", LiveChatSessionStatus.WAITING.name());
        LiveChatSession activeSession = createSession(visitor.getId(), "token-a1", LiveChatSessionStatus.ACTIVE.name());
        LiveChatSession closedSession = createSession(visitor.getId(), "token-c1", LiveChatSessionStatus.CLOSED.name());

        sessionRepository.save(waitingSession);
        sessionRepository.save(activeSession);
        sessionRepository.save(closedSession);

        // When
        List<LiveChatSession> waitingSessions = sessionRepository.findByTenantIdAndStatus(TEST_TENANT_ID, LiveChatSessionStatus.WAITING.name());

        // Then
        assertThat(waitingSessions).hasSize(1);
        assertThat(waitingSessions.get(0).getSessionToken()).isEqualTo("token-w1");
    }

    @Test
    void sessionRepo_shouldFindInactiveSessions() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-3");
        Instant now = Instant.now();
        Instant oldTime = now.minus(60, ChronoUnit.MINUTES);

        // Create an old inactive session
        LiveChatSession oldSession = createSession(visitor.getId(), "token-old", LiveChatSessionStatus.WAITING.name());
        LiveChatSession savedOld = sessionRepository.save(oldSession);

        // Update LAST_ACTIVITY_AT to old time via direct SQL
        jdbcTemplate.update("UPDATE CHAT_LIVECHAT_SESSION SET LAST_ACTIVITY_AT = ? WHERE ID = ?", oldTime, savedOld.getId());

        // Create a recent active session
        LiveChatSession recentSession = createSession(visitor.getId(), "token-recent", LiveChatSessionStatus.ACTIVE.name());
        sessionRepository.save(recentSession);

        // When - cutoff is 30 minutes ago
        Instant cutoff = now.minus(30, ChronoUnit.MINUTES);
        List<LiveChatSession> inactiveSessions = sessionRepository.findInactiveSessions(cutoff);

        // Then
        assertThat(inactiveSessions).hasSize(1);
        assertThat(inactiveSessions.get(0).getSessionToken()).isEqualTo("token-old");
    }

    @Test
    void sessionRepo_shouldUpdateStatus() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-4");
        LiveChatSession session = createSession(visitor.getId(), "token-status", LiveChatSessionStatus.WAITING.name());
        LiveChatSession saved = sessionRepository.save(session);

        // When
        sessionRepository.updateStatus(saved.getId(), LiveChatSessionStatus.ACTIVE.name(), Instant.now());

        // Then
        Optional<LiveChatSession> found = sessionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void sessionRepo_shouldUpdateAssignedAgent() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-5");
        Long agentChatUserId = createChatUserViaJdbc("Agent Smith", "agent@test.com");

        LiveChatSession session = createSession(visitor.getId(), "token-agent", LiveChatSessionStatus.WAITING.name());
        LiveChatSession saved = sessionRepository.save(session);

        Instant now = Instant.now();

        // When
        sessionRepository.updateAssignedAgent(saved.getId(), agentChatUserId, now, now);

        // Then
        Optional<LiveChatSession> found = sessionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getAssignedAgentId()).isEqualTo(agentChatUserId);
        assertThat(found.get().getAgentJoinedAt()).isNotNull();
    }

    @Test
    void sessionRepo_shouldIncrementMessageCount() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-6");
        LiveChatSession session = createSession(visitor.getId(), "token-count", LiveChatSessionStatus.ACTIVE.name());
        LiveChatSession saved = sessionRepository.save(session);

        assertThat(saved.getMessageCount()).isEqualTo(0);

        // When
        Instant now = Instant.now();
        sessionRepository.incrementMessageCount(saved.getId(), now, now);

        // Then
        Optional<LiveChatSession> found = sessionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getMessageCount()).isEqualTo(1);
    }

    @Test
    void sessionRepo_shouldUpdateRating() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-7");
        LiveChatSession session = createSession(visitor.getId(), "token-rating", LiveChatSessionStatus.CLOSED.name());
        LiveChatSession saved = sessionRepository.save(session);

        // When
        sessionRepository.updateRating(saved.getId(), 5, "Great service!", Instant.now());

        // Then
        Optional<LiveChatSession> found = sessionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getRating()).isEqualTo(5);
        assertThat(found.get().getFeedback()).isEqualTo("Great service!");
    }

    // ==================== Message Repository Tests ====================

    @Test
    void messageRepo_shouldFindBySessionOrdered() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-8");
        LiveChatSession session = createSession(visitor.getId(), "token-msg-order", LiveChatSessionStatus.ACTIVE.name());
        LiveChatSession savedSession = sessionRepository.save(session);

        Instant base = Instant.now();

        LiveChatMessage msg1 = LiveChatMessage.builder()
                .sessionId(savedSession.getId())
                .senderType(LiveChatSenderType.VISITOR.name())
                .senderId(visitor.getId())
                .content("First message")
                .messageType("TEXT")
                .isRead(false)
                .createdAt(base)
                .build();

        LiveChatMessage msg2 = LiveChatMessage.builder()
                .sessionId(savedSession.getId())
                .senderType(LiveChatSenderType.SYSTEM.name())
                .content("System message")
                .messageType("SYSTEM")
                .isRead(true)
                .createdAt(base.plusSeconds(1))
                .build();

        LiveChatMessage msg3 = LiveChatMessage.builder()
                .sessionId(savedSession.getId())
                .senderType(LiveChatSenderType.VISITOR.name())
                .senderId(visitor.getId())
                .content("Third message")
                .messageType("TEXT")
                .isRead(false)
                .createdAt(base.plusSeconds(2))
                .build();

        messageRepository.save(msg1);
        messageRepository.save(msg2);
        messageRepository.save(msg3);

        // When
        List<LiveChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(savedSession.getId());

        // Then
        assertThat(messages).hasSize(3);
        assertThat(messages.get(0).getContent()).isEqualTo("First message");
        assertThat(messages.get(1).getContent()).isEqualTo("System message");
        assertThat(messages.get(2).getContent()).isEqualTo("Third message");
    }

    @Test
    void messageRepo_shouldFindSinceTimestamp() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-9");
        LiveChatSession session = createSession(visitor.getId(), "token-msg-since", LiveChatSessionStatus.ACTIVE.name());
        LiveChatSession savedSession = sessionRepository.save(session);

        Instant cutoff = Instant.now();

        LiveChatMessage oldMessage = LiveChatMessage.builder()
                .sessionId(savedSession.getId())
                .senderType(LiveChatSenderType.VISITOR.name())
                .senderId(visitor.getId())
                .content("Old message")
                .messageType("TEXT")
                .isRead(false)
                .createdAt(cutoff.minusSeconds(60))
                .build();

        LiveChatMessage newMessage = LiveChatMessage.builder()
                .sessionId(savedSession.getId())
                .senderType(LiveChatSenderType.VISITOR.name())
                .senderId(visitor.getId())
                .content("New message")
                .messageType("TEXT")
                .isRead(false)
                .createdAt(cutoff.plusSeconds(60))
                .build();

        messageRepository.save(oldMessage);
        messageRepository.save(newMessage);

        // When
        List<LiveChatMessage> messages = messageRepository.findBySessionIdSince(savedSession.getId(), cutoff);

        // Then
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("New message");
    }

    @Test
    void messageRepo_shouldMarkAsRead() {
        // Given
        LiveChatVisitor visitor = createAndSaveVisitor("visitor-10");
        LiveChatSession session = createSession(visitor.getId(), "token-msg-read", LiveChatSessionStatus.ACTIVE.name());
        LiveChatSession savedSession = sessionRepository.save(session);

        LiveChatMessage unreadMsg1 = LiveChatMessage.builder()
                .sessionId(savedSession.getId())
                .senderType(LiveChatSenderType.VISITOR.name())
                .senderId(visitor.getId())
                .content("Unread visitor message 1")
                .messageType("TEXT")
                .isRead(false)
                .createdAt(Instant.now())
                .build();

        LiveChatMessage unreadMsg2 = LiveChatMessage.builder()
                .sessionId(savedSession.getId())
                .senderType(LiveChatSenderType.VISITOR.name())
                .senderId(visitor.getId())
                .content("Unread visitor message 2")
                .messageType("TEXT")
                .isRead(false)
                .createdAt(Instant.now().plusSeconds(1))
                .build();

        messageRepository.save(unreadMsg1);
        messageRepository.save(unreadMsg2);

        // When
        Instant readAt = Instant.now();
        messageRepository.markAsReadBySenderType(savedSession.getId(), LiveChatSenderType.VISITOR.name(), readAt);

        // Then
        List<LiveChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(savedSession.getId());
        assertThat(messages).hasSize(2);
        assertThat(messages).allSatisfy(msg -> {
            assertThat(msg.getIsRead()).isTrue();
            assertThat(msg.getReadAt()).isNotNull();
        });
    }

    // ==================== Widget Config Repository Tests ====================

    @Test
    void widgetConfigRepo_shouldSaveAndFindByTenant() {
        // Given
        LiveChatWidgetConfig config = LiveChatWidgetConfig.builder()
                .tenantId(TEST_TENANT_ID)
                .enabled(true)
                .primaryColor("#FF5733")
                .headerText("Support Chat")
                .welcomeMessage("Hi there!")
                .offlineMessage("We are offline.")
                .position("BOTTOM_LEFT")
                .requireEmail(true)
                .autoOpenDelaySeconds(5)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        widgetConfigRepository.save(config);

        // When
        Optional<LiveChatWidgetConfig> found = widgetConfigRepository.findByTenantId(TEST_TENANT_ID);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getPrimaryColor()).isEqualTo("#FF5733");
        assertThat(found.get().getHeaderText()).isEqualTo("Support Chat");
        assertThat(found.get().getWelcomeMessage()).isEqualTo("Hi there!");
        assertThat(found.get().getOfflineMessage()).isEqualTo("We are offline.");
        assertThat(found.get().getPosition()).isEqualTo("BOTTOM_LEFT");
        assertThat(found.get().getRequireEmail()).isTrue();
        assertThat(found.get().getAutoOpenDelaySeconds()).isEqualTo(5);
        assertThat(found.get().getEnabled()).isTrue();
    }

    // ==================== Helper Methods ====================

    private LiveChatVisitor createAndSaveVisitor(String visitorId) {
        LiveChatVisitor visitor = LiveChatVisitor.builder()
                .tenantId(TEST_TENANT_ID)
                .visitorId(visitorId)
                .name("Visitor " + visitorId)
                .email(visitorId + "@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return visitorRepository.save(visitor);
    }

    private LiveChatSession createSession(Long visitorId, String token, String status) {
        return LiveChatSession.builder()
                .tenantId(TEST_TENANT_ID)
                .sessionToken(token)
                .visitorId(visitorId)
                .status(status)
                .sourceService("HELPDESK")
                .messageCount(0)
                .lastActivityAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private Long createChatUserViaJdbc(String name, String email) {
        Instant now = Instant.now();
        jdbcTemplate.update(
                "INSERT INTO CHAT_USER (EXTERNAL_USER_ID, TENANT_ID, NAME, EMAIL, STATUS, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?)",
                TEST_USER_ID.toString(), TEST_TENANT_ID.toString(), name, email, "ONLINE", now, now
        );
        return jdbcTemplate.queryForObject(
                "SELECT ID FROM CHAT_USER WHERE EXTERNAL_USER_ID = ?",
                Long.class,
                TEST_USER_ID.toString()
        );
    }
}
