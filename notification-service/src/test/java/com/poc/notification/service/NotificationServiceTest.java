package com.poc.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.EmailStatus;
import com.poc.notification.domain.EmailTemplate;
import com.poc.notification.dto.NotificationRequest;
import com.poc.notification.dto.NotificationResponse;
import com.poc.notification.provider.SmtpEmailProvider;
import com.poc.notification.repository.EmailHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    @Mock
    private EmailHistoryRepository repository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private RateLimitService rateLimitService;

    @Mock
    private TenantConfigService tenantConfigService;

    @Mock
    private SmtpEmailProvider smtpProvider;

    private ObjectMapper objectMapper;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        notificationService = new NotificationService(
                repository,
                kafkaTemplate,
                rateLimitService,
                tenantConfigService,
                smtpProvider,
                objectMapper
        );

        ReflectionTestUtils.setField(notificationService, "emailQueueTopic", "notification-email-queue");
    }

    @Test
    @DisplayName("Should queue email successfully")
    void shouldQueueEmailSuccessfully() {
        // Arrange
        NotificationRequest request = NotificationRequest.builder()
                .to("test@example.com")
                .template(EmailTemplate.VERIFICATION)
                .variables(Map.of("userName", "Test User", "verificationCode", "1234"))
                .build();

        when(repository.save(any(EmailHistory.class))).thenAnswer(invocation -> {
            EmailHistory h = invocation.getArgument(0);
            h.setId(1L);
            return h;
        });

        // Act
        NotificationResponse response = notificationService.queueEmail(request, "user-1");

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getMessageId());
        assertEquals("Email queued successfully", response.getMessage());

        verify(rateLimitService).checkRateLimit("user-1");
        verify(repository).save(any(EmailHistory.class));
        verify(kafkaTemplate).send(eq("notification-email-queue"), anyString(), anyString());
    }

    @Test
    @DisplayName("Should save email with correct status when not scheduled")
    void shouldSaveEmailWithQueuedStatusWhenNotScheduled() {
        // Arrange
        NotificationRequest request = NotificationRequest.builder()
                .to("test@example.com")
                .template(EmailTemplate.WELCOME)
                .build();

        ArgumentCaptor<EmailHistory> historyCaptor = ArgumentCaptor.forClass(EmailHistory.class);
        when(repository.save(historyCaptor.capture())).thenAnswer(invocation -> {
            EmailHistory h = invocation.getArgument(0);
            h.setId(1L);
            return h;
        });

        // Act
        notificationService.queueEmail(request, "user-1");

        // Assert
        EmailHistory saved = historyCaptor.getValue();
        assertEquals(EmailStatus.QUEUED.name(), saved.getStatus());
        assertNull(saved.getScheduledAt());
    }

    @Test
    @DisplayName("Should save email with PENDING status when scheduled")
    void shouldSaveEmailWithPendingStatusWhenScheduled() {
        // Arrange
        Instant scheduledTime = Instant.now().plus(1, ChronoUnit.HOURS);
        NotificationRequest request = NotificationRequest.builder()
                .to("test@example.com")
                .template(EmailTemplate.WELCOME)
                .scheduledAt(scheduledTime)
                .build();

        ArgumentCaptor<EmailHistory> historyCaptor = ArgumentCaptor.forClass(EmailHistory.class);
        when(repository.save(historyCaptor.capture())).thenAnswer(invocation -> {
            EmailHistory h = invocation.getArgument(0);
            h.setId(1L);
            return h;
        });

        // Act
        notificationService.queueEmail(request, "user-1");

        // Assert
        EmailHistory saved = historyCaptor.getValue();
        assertEquals(EmailStatus.PENDING.name(), saved.getStatus());
        assertEquals(scheduledTime, saved.getScheduledAt());

        // Should not send to Kafka when scheduled
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should use default subject from template when not provided")
    void shouldUseDefaultSubjectFromTemplateWhenNotProvided() {
        // Arrange
        NotificationRequest request = NotificationRequest.builder()
                .to("test@example.com")
                .template(EmailTemplate.PASSWORD_RESET)
                .build();

        ArgumentCaptor<EmailHistory> historyCaptor = ArgumentCaptor.forClass(EmailHistory.class);
        when(repository.save(historyCaptor.capture())).thenAnswer(invocation -> {
            EmailHistory h = invocation.getArgument(0);
            h.setId(1L);
            return h;
        });

        // Act
        notificationService.queueEmail(request, "user-1");

        // Assert
        EmailHistory saved = historyCaptor.getValue();
        assertEquals("Password Reset Request", saved.getSubject());
    }

    @Test
    @DisplayName("Should use custom subject when provided")
    void shouldUseCustomSubjectWhenProvided() {
        // Arrange
        NotificationRequest request = NotificationRequest.builder()
                .to("test@example.com")
                .template(EmailTemplate.PASSWORD_RESET)
                .subject("Custom Subject")
                .build();

        ArgumentCaptor<EmailHistory> historyCaptor = ArgumentCaptor.forClass(EmailHistory.class);
        when(repository.save(historyCaptor.capture())).thenAnswer(invocation -> {
            EmailHistory h = invocation.getArgument(0);
            h.setId(1L);
            return h;
        });

        // Act
        notificationService.queueEmail(request, "user-1");

        // Assert
        EmailHistory saved = historyCaptor.getValue();
        assertEquals("Custom Subject", saved.getSubject());
    }

    @Test
    @DisplayName("Should return error when rate limit exceeded")
    void shouldReturnErrorWhenRateLimitExceeded() {
        // Arrange
        NotificationRequest request = NotificationRequest.builder()
                .to("test@example.com")
                .template(EmailTemplate.VERIFICATION)
                .build();

        doThrow(new RateLimitService.RateLimitExceededException("Rate limit exceeded"))
                .when(rateLimitService).checkRateLimit("user-1");

        // Act
        NotificationResponse response = notificationService.queueEmail(request, "user-1");

        // Assert
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Rate limit exceeded"));
        assertNull(response.getMessageId());

        verify(repository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }
}
