package com.poc.notification.service;

import com.poc.notification.domain.RateLimitTracker;
import com.poc.notification.domain.TenantConfig;
import com.poc.notification.dto.RateLimitStatusDTO;
import com.poc.notification.repository.RateLimitTrackerRepository;
import com.poc.notification.repository.TenantConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final RateLimitTrackerRepository trackerRepository;
    private final TenantConfigRepository configRepository;

    @Transactional
    public boolean canSend(String tenantId, String configType) {
        TenantConfig config = configRepository.findByTenantIdAndConfigType(tenantId, configType).orElse(null);
        if (config == null) return false;

        RateLimitTracker tracker = getOrCreateTracker(tenantId, configType);
        Instant now = Instant.now();

        // Check throttle
        if (Boolean.TRUE.equals(tracker.getIsThrottled()) && tracker.getThrottledUntil() != null) {
            if (now.isBefore(tracker.getThrottledUntil())) {
                return false;
            }
            tracker.setIsThrottled(false);
            tracker.setThrottledUntil(null);
        }

        // Reset windows if expired
        resetWindowsIfNeeded(tracker, now);

        // Check limits
        if (tracker.getMinuteCount() >= config.getMaxEmailsPerMinute()) return false;
        if (tracker.getHourCount() >= config.getMaxEmailsPerHour()) return false;
        if (tracker.getDayCount() >= config.getMaxEmailsPerDay()) return false;

        trackerRepository.save(tracker);
        return true;
    }

    @Transactional
    public void recordSend(String tenantId, String configType) {
        RateLimitTracker tracker = getOrCreateTracker(tenantId, configType);
        Instant now = Instant.now();

        resetWindowsIfNeeded(tracker, now);

        tracker.setMinuteCount(tracker.getMinuteCount() + 1);
        tracker.setHourCount(tracker.getHourCount() + 1);
        tracker.setDayCount(tracker.getDayCount() + 1);
        tracker.setUpdatedAt(now);

        trackerRepository.save(tracker);
    }

    @Transactional
    public void handleSmtpError(String tenantId, String configType, Exception e) {
        String message = e.getMessage() != null ? e.getMessage() : "";
        Instant now = Instant.now();
        RateLimitTracker tracker = getOrCreateTracker(tenantId, configType);

        if (message.contains("421") || message.contains("429")) {
            tracker.setIsThrottled(true);
            tracker.setThrottledUntil(now.plus(5, ChronoUnit.MINUTES));
            log.warn("SMTP rate limit for tenant {} type {}, throttling for 5 minutes", tenantId, configType);
        } else if (message.contains("552")) {
            tracker.setIsThrottled(true);
            tracker.setThrottledUntil(now.plus(1, ChronoUnit.HOURS));
            log.warn("SMTP quota exceeded for tenant {} type {}, throttling for 1 hour", tenantId, configType);
        }

        tracker.setUpdatedAt(now);
        trackerRepository.save(tracker);
    }

    @Transactional(readOnly = true)
    public RateLimitStatusDTO getStatus(String tenantId, String configType) {
        TenantConfig config = configRepository.findByTenantIdAndConfigType(tenantId, configType).orElse(null);
        if (config == null) {
            return RateLimitStatusDTO.builder().canSend(false).build();
        }

        RateLimitTracker tracker = trackerRepository.findByTenantIdAndConfigType(tenantId, configType).orElse(null);
        if (tracker == null) {
            return RateLimitStatusDTO.builder()
                    .minuteCount(0).minuteLimit(config.getMaxEmailsPerMinute())
                    .hourCount(0).hourLimit(config.getMaxEmailsPerHour())
                    .dayCount(0).dayLimit(config.getMaxEmailsPerDay())
                    .isThrottled(false)
                    .canSend(true)
                    .build();
        }

        return RateLimitStatusDTO.builder()
                .minuteCount(tracker.getMinuteCount())
                .minuteLimit(config.getMaxEmailsPerMinute())
                .hourCount(tracker.getHourCount())
                .hourLimit(config.getMaxEmailsPerHour())
                .dayCount(tracker.getDayCount())
                .dayLimit(config.getMaxEmailsPerDay())
                .isThrottled(tracker.getIsThrottled())
                .throttledUntil(tracker.getThrottledUntil())
                .canSend(canSend(tenantId, configType))
                .build();
    }

    /**
     * Legacy method for template email rate limiting.
     * Used by NotificationService for backward compatibility.
     */
    public void checkRateLimit(String userId) {
        // Template emails from Kafka don't go through DB rate limiting.
        // They use the NOTIFICATION config of the platform tenant.
        // For now, allow all template emails (they're system-generated).
    }

    private RateLimitTracker getOrCreateTracker(String tenantId, String configType) {
        return trackerRepository.findByTenantIdAndConfigType(tenantId, configType)
                .orElseGet(() -> {
                    Instant now = Instant.now();
                    return trackerRepository.save(RateLimitTracker.builder()
                            .tenantId(tenantId)
                            .configType(configType)
                            .minuteWindowStart(now)
                            .hourWindowStart(now)
                            .dayWindowStart(now)
                            .updatedAt(now)
                            .build());
                });
    }

    private void resetWindowsIfNeeded(RateLimitTracker tracker, Instant now) {
        if (tracker.getMinuteWindowStart() == null || now.isAfter(tracker.getMinuteWindowStart().plus(1, ChronoUnit.MINUTES))) {
            tracker.setMinuteCount(0);
            tracker.setMinuteWindowStart(now);
        }
        if (tracker.getHourWindowStart() == null || now.isAfter(tracker.getHourWindowStart().plus(1, ChronoUnit.HOURS))) {
            tracker.setHourCount(0);
            tracker.setHourWindowStart(now);
        }
        if (tracker.getDayWindowStart() == null || now.isAfter(tracker.getDayWindowStart().plus(1, ChronoUnit.DAYS))) {
            tracker.setDayCount(0);
            tracker.setDayWindowStart(now);
        }
    }

    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String message) {
            super(message);
        }
    }
}
