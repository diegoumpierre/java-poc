package com.poc.chat.service;

import com.poc.chat.domain.ChatTenantSettings;
import com.poc.chat.repository.ChatTenantSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageRetentionService {

    private final ChatTenantSettingsRepository settingsRepository;
    private final JdbcClient jdbcClient;

    @Value("${app.retention.enabled:true}")
    private boolean retentionEnabled;

    @Value("${app.retention.batch-size:1000}")
    private int batchSize;

    @Value("${app.retention.default-days:365}")
    private int defaultRetentionDays;

    @Scheduled(cron = "${app.retention.cron:0 0 3 * * *}")
    @Transactional
    public void purgeExpiredMessages() {
        if (!retentionEnabled) {
            log.debug("Message retention is disabled");
            return;
        }

        log.info("Starting message retention purge...");

        List<ChatTenantSettings> tenantSettings = settingsRepository.findAllWithRetention();

        int totalPurged = 0;
        for (ChatTenantSettings settings : tenantSettings) {
            int days = settings.getMessageRetentionDays();
            if (days <= 0) continue;

            Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);
            String tenantId = settings.getTenantId().toString();

            int purged = purgeMessagesForTenant(tenantId, cutoff);
            if (purged > 0) {
                log.info("Purged {} messages for tenant {} (retention: {} days)", purged, tenantId, days);
                totalPurged += purged;
            }
        }

        log.info("Message retention purge complete. Total purged: {}", totalPurged);
    }

    private int purgeMessagesForTenant(String tenantId, Instant cutoff) {
        int totalDeleted = 0;
        int deleted;

        do {
            deleted = jdbcClient.sql("""
                    DELETE FROM CHAT_MESSAGE
                    WHERE ID IN (
                        SELECT ID FROM (
                            SELECT m.ID FROM CHAT_MESSAGE m
                            JOIN CHAT_CHANNEL c ON m.CHANNEL_ID = c.ID
                            WHERE c.TENANT_ID = :tenantId
                            AND m.CREATED_AT < :cutoff
                            LIMIT :batchSize
                        ) AS tmp
                    )
                    """)
                    .param("tenantId", tenantId)
                    .param("cutoff", cutoff)
                    .param("batchSize", batchSize)
                    .update();

            totalDeleted += deleted;
        } while (deleted >= batchSize);

        return totalDeleted;
    }
}
