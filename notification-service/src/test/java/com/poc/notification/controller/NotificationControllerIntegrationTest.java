package com.poc.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.notification.domain.EmailTemplate;
import com.poc.notification.dto.NotificationRequest;
import com.poc.notification.repository.EmailHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"test-notification-email-queue"})
@DisplayName("NotificationController Integration Tests")
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private EmailHistoryRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should list all available templates")
    void shouldListAllAvailableTemplates() throws Exception {
        mockMvc.perform(get("/api/notification/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(
                        "PASSWORD_RESET", "WELCOME", "VERIFICATION", "SIMPLE"
                )));
    }

    @Test
    @DisplayName("Should test SMTP connection")
    void shouldTestSmtpConnection() throws Exception {
        mockMvc.perform(get("/api/notification/test-connection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.connected").exists());
    }

    @Test
    @DisplayName("Should queue email successfully")
    void shouldQueueEmailSuccessfully() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .to("test@example.com")
                .template(EmailTemplate.VERIFICATION)
                .variables(Map.of(
                        "userName", "Test User",
                        "verificationCode", "1234",
                        "expirationMinutes", 15
                ))
                .build();

        mockMvc.perform(post("/api/notification/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "ad15d8e5-fb9a-4590-9362-1f09863de72b")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.messageId").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Email queued successfully"));
    }

    @Test
    @DisplayName("Should return validation error for invalid email")
    void shouldReturnValidationErrorForInvalidEmail() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .to("invalid-email")
                .template(EmailTemplate.VERIFICATION)
                .build();

        mockMvc.perform(post("/api/notification/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "ad15d8e5-fb9a-4590-9362-1f09863de72b")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when template is missing")
    void shouldReturnValidationErrorWhenTemplateMissing() throws Exception {
        String requestJson = "{\"to\":\"test@example.com\"}";

        mockMvc.perform(post("/api/notification/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "ad15d8e5-fb9a-4590-9362-1f09863de72b")
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get email history for user")
    void shouldGetEmailHistoryForUser() throws Exception {
        // First, send some emails
        NotificationRequest request = NotificationRequest.builder()
                .to("test@example.com")
                .template(EmailTemplate.WELCOME)
                .build();

        mockMvc.perform(post("/api/notification/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "ad15d8e5-fb9a-4590-9362-1f09863de72b")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then get history
        mockMvc.perform(get("/api/notification/history")
                        .header("X-User-Id", "ad15d8e5-fb9a-4590-9362-1f09863de72b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].recipient").value("test@example.com"))
                .andExpect(jsonPath("$[0].template").value("WELCOME"));
    }

    @Test
    @DisplayName("Should queue email with custom subject")
    void shouldQueueEmailWithCustomSubject() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .to("test@example.com")
                .template(EmailTemplate.SIMPLE)
                .subject("Custom Subject Line")
                .variables(Map.of("message", "Test message content"))
                .build();

        mockMvc.perform(post("/api/notification/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", "ad15d8e5-fb9a-4590-9362-1f09863de72b")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify subject was saved
        mockMvc.perform(get("/api/notification/history")
                        .header("X-User-Id", "ad15d8e5-fb9a-4590-9362-1f09863de72b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Custom Subject Line"));
    }
}
