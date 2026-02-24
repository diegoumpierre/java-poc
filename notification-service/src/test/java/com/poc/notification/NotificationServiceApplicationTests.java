package com.poc.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"test-notification-email-queue"})
@DisplayName("Notification Service Application Tests")
class NotificationServiceApplicationTests {

    @Test
    @DisplayName("Context should load successfully")
    void contextLoads() {
        // Application context loads successfully with H2 + Liquibase + EmbeddedKafka
    }
}
