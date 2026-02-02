package com.poc.tenant.health;

import com.poc.tenant.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component("kafkaHealth")
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class KafkaHealthIndicator implements HealthIndicator {

    @Value("${spring.kafka.bootstrap-servers:127.0.0.1:9092}")
    private String bootstrapServers;

    private final OutboxEventRepository outboxRepository;

    @Override
    public Health health() {
        try (AdminClient adminClient = AdminClient.create(
                Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000,
                        AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 5000))) {

            adminClient.listTopics().names().get(5, TimeUnit.SECONDS);

            long pendingEvents = outboxRepository.countPending();
            long failedEvents = outboxRepository.countFailed();

            Health.Builder builder = Health.up()
                    .withDetail("bootstrapServers", bootstrapServers)
                    .withDetail("outbox.pending", pendingEvents)
                    .withDetail("outbox.failed", failedEvents);

            if (failedEvents > 0) {
                builder.withDetail("outbox.warning", failedEvents + " events failed permanently");
            }

            return builder.build();

        } catch (Exception e) {
            log.warn("[KafkaHealth] Kafka health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("bootstrapServers", bootstrapServers)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
