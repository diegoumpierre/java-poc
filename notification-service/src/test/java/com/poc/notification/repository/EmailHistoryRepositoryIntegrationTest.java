package com.poc.notification.repository;

import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.EmailStatus;
import com.poc.notification.domain.EmailTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("EmailHistoryRepository Integration Tests")
class EmailHistoryRepositoryIntegrationTest {

    @Autowired
    private EmailHistoryRepository repository;

    private EmailHistory createEmailHistory(String status, String userId) {
        return EmailHistory.builder()
                .messageId(UUID.randomUUID().toString())
                .userId(userId)
                .recipient("test@example.com")
                .subject("Test Subject")
                .template(EmailTemplate.VERIFICATION.name())
                .variables("{\"userName\": \"Test\"}")
                .configType("NOTIFICATION")
                .status(status)
                .retryCount(0)
                .createdAt(Instant.now())
                .build();
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should save and find email history by message ID")
    void shouldSaveAndFindByMessageId() {
        // Arrange
        EmailHistory history = createEmailHistory(EmailStatus.QUEUED.name(), "user-1");
        repository.save(history);

        // Act
        Optional<EmailHistory> found = repository.findByMessageId(history.getMessageId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(history.getRecipient(), found.get().getRecipient());
        assertEquals(history.getTemplate(), found.get().getTemplate());
        assertEquals(history.getStatus(), found.get().getStatus());
    }

    @Test
    @DisplayName("Should return empty when message ID not found")
    void shouldReturnEmptyWhenMessageIdNotFound() {
        // Act
        Optional<EmailHistory> found = repository.findByMessageId("non-existent-id");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should find emails by user ID ordered by created at desc")
    void shouldFindByUserIdOrderedByCreatedAtDesc() {
        // Arrange
        String userId = "user-1";
        EmailHistory history1 = createEmailHistory(EmailStatus.SENT.name(), userId);
        history1.setCreatedAt(Instant.now().minus(2, ChronoUnit.HOURS));
        repository.save(history1);

        EmailHistory history2 = createEmailHistory(EmailStatus.QUEUED.name(), userId);
        history2.setCreatedAt(Instant.now().minus(1, ChronoUnit.HOURS));
        repository.save(history2);

        EmailHistory history3 = createEmailHistory(EmailStatus.PENDING.name(), userId);
        history3.setCreatedAt(Instant.now());
        repository.save(history3);

        // Different user - should not be included
        EmailHistory otherUserHistory = createEmailHistory(EmailStatus.SENT.name(), "user-2");
        repository.save(otherUserHistory);

        // Act
        List<EmailHistory> found = repository.findByUserIdOrderByCreatedAtDesc(userId);

        // Assert
        assertEquals(3, found.size());
        // Most recent first
        assertEquals(history3.getMessageId(), found.get(0).getMessageId());
        assertEquals(history2.getMessageId(), found.get(1).getMessageId());
        assertEquals(history1.getMessageId(), found.get(2).getMessageId());
    }

    @Test
    @DisplayName("Should find scheduled emails that are due")
    void shouldFindScheduledEmailsThatAreDue() {
        // Arrange
        // Email scheduled in the past (should be found)
        EmailHistory dueEmail = createEmailHistory(EmailStatus.PENDING.name(), "user-1");
        dueEmail.setScheduledAt(Instant.now().minus(1, ChronoUnit.HOURS));
        repository.save(dueEmail);

        // Email scheduled in the future (should not be found)
        EmailHistory futureEmail = createEmailHistory(EmailStatus.PENDING.name(), "user-1");
        futureEmail.setScheduledAt(Instant.now().plus(1, ChronoUnit.HOURS));
        repository.save(futureEmail);

        // Email with different status (should not be found)
        EmailHistory sentEmail = createEmailHistory(EmailStatus.SENT.name(), "user-1");
        sentEmail.setScheduledAt(Instant.now().minus(1, ChronoUnit.HOURS));
        repository.save(sentEmail);

        // Act
        List<EmailHistory> found = repository.findByStatusAndScheduledAtBefore(
                EmailStatus.PENDING.name(),
                Instant.now()
        );

        // Assert
        assertEquals(1, found.size());
        assertEquals(dueEmail.getMessageId(), found.get(0).getMessageId());
    }

    @Test
    @DisplayName("Should find failed template emails with retry count less than max")
    void shouldFindFailedTemplateEmailsWithRetryCountLessThanMax() {
        // Arrange
        // Failed NOTIFICATION email with 0 retries (should be found)
        EmailHistory failedEmail1 = createEmailHistory(EmailStatus.FAILED.name(), "user-1");
        failedEmail1.setConfigType("NOTIFICATION");
        failedEmail1.setRetryCount(0);
        repository.save(failedEmail1);

        // Failed NOTIFICATION email with 2 retries (should be found if max is 3)
        EmailHistory failedEmail2 = createEmailHistory(EmailStatus.FAILED.name(), "user-1");
        failedEmail2.setConfigType("NOTIFICATION");
        failedEmail2.setRetryCount(2);
        repository.save(failedEmail2);

        // Failed NOTIFICATION email with 3 retries (should not be found if max is 3)
        EmailHistory maxRetriesEmail = createEmailHistory(EmailStatus.FAILED.name(), "user-1");
        maxRetriesEmail.setConfigType("NOTIFICATION");
        maxRetriesEmail.setRetryCount(3);
        repository.save(maxRetriesEmail);

        // Failed ATENDIMENTO email (should not be found - different config type)
        EmailHistory atendimentoEmail = createEmailHistory(EmailStatus.FAILED.name(), "user-1");
        atendimentoEmail.setConfigType("ATENDIMENTO");
        atendimentoEmail.setRetryCount(0);
        repository.save(atendimentoEmail);

        // Dead email (should not be found)
        EmailHistory deadEmail = createEmailHistory(EmailStatus.DEAD.name(), "user-1");
        deadEmail.setConfigType("NOTIFICATION");
        deadEmail.setRetryCount(0);
        repository.save(deadEmail);

        // Act
        List<EmailHistory> found = repository.findTemplateByStatusAndRetryCountLessThan(
                EmailStatus.FAILED.name(),
                3
        );

        // Assert
        assertEquals(2, found.size());
    }

    @Test
    @DisplayName("Should find emails by tenant and user")
    void shouldFindByTenantAndUser() {
        // Arrange
        String tenantId = "tenant-1";
        String userId = "user-1";

        EmailHistory email1 = createEmailHistory(EmailStatus.SENT.name(), userId);
        email1.setTenantId(tenantId);
        repository.save(email1);

        EmailHistory email2 = createEmailHistory(EmailStatus.QUEUED.name(), userId);
        email2.setTenantId(tenantId);
        repository.save(email2);

        // Different tenant
        EmailHistory otherTenant = createEmailHistory(EmailStatus.SENT.name(), userId);
        otherTenant.setTenantId("tenant-2");
        repository.save(otherTenant);

        // Act
        List<EmailHistory> found = repository.findByUserIdAndTenantIdOrderByCreatedAtDesc(userId, tenantId);

        // Assert
        assertEquals(2, found.size());
    }

    @Test
    @DisplayName("Should update email history")
    void shouldUpdateEmailHistory() {
        // Arrange
        EmailHistory history = createEmailHistory(EmailStatus.QUEUED.name(), "user-1");
        history = repository.save(history);

        // Act
        history.setStatus(EmailStatus.SENT.name());
        history.setSentAt(Instant.now());
        history.setUpdatedAt(Instant.now());
        repository.save(history);

        // Assert
        Optional<EmailHistory> found = repository.findByMessageId(history.getMessageId());
        assertTrue(found.isPresent());
        assertEquals(EmailStatus.SENT.name(), found.get().getStatus());
        assertNotNull(found.get().getSentAt());
    }

    @Test
    @DisplayName("Should find queued direct messages")
    void shouldFindQueuedDirectMessages() {
        // Arrange
        EmailHistory directQueued = createEmailHistory(EmailStatus.QUEUED.name(), "user-1");
        directQueued.setConfigType("ATENDIMENTO");
        repository.save(directQueued);

        // NOTIFICATION type should not be found
        EmailHistory notifQueued = createEmailHistory(EmailStatus.QUEUED.name(), "user-1");
        notifQueued.setConfigType("NOTIFICATION");
        repository.save(notifQueued);

        // Act
        List<EmailHistory> found = repository.findQueuedDirectMessages();

        // Assert
        assertEquals(1, found.size());
        assertEquals("ATENDIMENTO", found.get(0).getConfigType());
    }
}
