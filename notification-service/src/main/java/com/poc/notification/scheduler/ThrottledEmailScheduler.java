package com.poc.notification.scheduler;

import com.poc.notification.domain.ConfigType;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.domain.TenantConfig;
import com.poc.notification.repository.EmailHistoryRepository;
import com.poc.notification.repository.TenantConfigRepository;
import com.poc.notification.service.DirectEmailService;
import com.poc.notification.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThrottledEmailScheduler {

    private final EmailHistoryRepository emailHistoryRepository;
    private final TenantConfigRepository configRepository;
    private final RateLimitService rateLimitService;
    private final DirectEmailService directEmailService;

    @Scheduled(fixedDelayString = "${app.scheduler.throttled-email-rate:10000}")
    public void processQueuedEmails() {
        List<EmailHistory> queued = emailHistoryRepository.findQueuedDirectMessages();
        if (queued.isEmpty()) return;

        log.debug("Processing {} queued direct emails", queued.size());

        for (EmailHistory msg : queued) {
            String configType = msg.getConfigType() != null ? msg.getConfigType() : ConfigType.ATENDIMENTO.name();

            if (!rateLimitService.canSend(msg.getTenantId(), configType)) {
                continue;
            }

            TenantConfig config = configRepository.findByTenantIdAndConfigType(msg.getTenantId(), configType).orElse(null);
            if (config == null) continue;

            directEmailService.sendEmail(msg);

            if (config.getCooldownSeconds() != null && config.getCooldownSeconds() > 0) {
                try {
                    Thread.sleep(config.getCooldownSeconds() * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
