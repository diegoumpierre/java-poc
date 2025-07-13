package br.dev.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class KafkaProducerServiceTest {

    @Test
    void testSendMessage() {
        // Arrange
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaProducerService service = new KafkaProducerService(kafkaTemplate);

        String topic = "test-topic";
        String message = "Hello Kafka!";

        // Act
        service.send(topic, message);

        // Assert
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), messageCaptor.capture());

        assertEquals(topic, topicCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
    }
}