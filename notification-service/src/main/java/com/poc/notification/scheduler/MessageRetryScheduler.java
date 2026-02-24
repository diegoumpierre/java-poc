package com.poc.notification.scheduler;

import com.poc.notification.domain.ConfigType;
import com.poc.notification.domain.EmailHistory;
import com.poc.notification.repository.EmailHistoryRepository;
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
public class MessageRetryScheduler {

    private final EmailHistoryRepository emailHistoryRepository;
    private final RateLimitService rateLimitService;
    private final DirectEmailService directEmailService;

    @Scheduled(fixedDelayString = "${app.scheduler.direct-retry-rate:300000}")
    public void retryFailedMessages() {
        List<EmailHistory> retryable = emailHistoryRepository.findRetryableDirectMessages();
        if (retryable.isEmpty()) return;

        log.info("Retrying {} failed direct emails", retryable.size());

        for (EmailHistory msg : retryable) {
            String configType = msg.getConfigType() != null ? msg.getConfigType() : ConfigType.ATENDIMENTO.name();

            if (!rateLimitService.canSend(msg.getTenantId(), configType)) {
                continue;
            }
            directEmailService.sendEmail(msg);
        }
    }
}
