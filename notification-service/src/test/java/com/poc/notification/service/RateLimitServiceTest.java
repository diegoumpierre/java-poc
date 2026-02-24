package com.poc.notification.service;

import com.poc.notification.domain.RateLimitTracker;
import com.poc.notification.domain.TenantConfig;
import com.poc.notification.repository.RateLimitTrackerRepository;
import com.poc.notification.repository.TenantConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitService Unit Tests")
class RateLimitServiceTest {

    @Mock
    private RateLimitTrackerRepository trackerRepository;

    @Mock
    private TenantConfigRepository configRepository;

    private RateLimitService rateLimitService;

    private static final String TENANT_ID = "tenant-1";
    private static final String CONFIG_TYPE = "ATENDIMENTO";

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService(trackerRepository, configRepository);
    }

    private TenantConfig createConfig(int perMinute, int perHour, int perDay) {
        return TenantConfig.builder()
                .tenantId(TENANT_ID)
                .configType(CONFIG_TYPE)
                .maxEmailsPerMinute(perMinute)
                .maxEmailsPerHour(perHour)
                .maxEmailsPerDay(perDay)
                .build();
    }

    private RateLimitTracker createTracker(int minuteCount, int hourCount, int dayCount) {
        Instant now = Instant.now();
        return RateLimitTracker.builder()
                .tenantId(TENANT_ID)
                .configType(CONFIG_TYPE)
                .minuteCount(minuteCount)
                .minuteWindowStart(now)
                .hourCount(hourCount)
                .hourWindowStart(now)
                .dayCount(dayCount)
                .dayWindowStart(now)
                .isThrottled(false)
                .build();
    }

    @Test
    @DisplayName("Should allow sending when within all limits")
    void shouldAllowSendingWhenWithinLimits() {
        TenantConfig config = createConfig(10, 100, 500);
        RateLimitTracker tracker = createTracker(5, 50, 200);

        when(configRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(config));
        when(trackerRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(tracker));

        assertTrue(rateLimitService.canSend(TENANT_ID, CONFIG_TYPE));
    }

    @Test
    @DisplayName("Should deny sending when minute limit reached")
    void shouldDenyWhenMinuteLimitReached() {
        TenantConfig config = createConfig(10, 100, 500);
        RateLimitTracker tracker = createTracker(10, 50, 200);

        when(configRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(config));
        when(trackerRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(tracker));

        assertFalse(rateLimitService.canSend(TENANT_ID, CONFIG_TYPE));
    }

    @Test
    @DisplayName("Should deny sending when hour limit reached")
    void shouldDenyWhenHourLimitReached() {
        TenantConfig config = createConfig(10, 100, 500);
        RateLimitTracker tracker = createTracker(5, 100, 200);

        when(configRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(config));
        when(trackerRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(tracker));

        assertFalse(rateLimitService.canSend(TENANT_ID, CONFIG_TYPE));
    }

    @Test
    @DisplayName("Should deny sending when day limit reached")
    void shouldDenyWhenDayLimitReached() {
        TenantConfig config = createConfig(10, 100, 500);
        RateLimitTracker tracker = createTracker(5, 50, 500);

        when(configRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(config));
        when(trackerRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(tracker));

        assertFalse(rateLimitService.canSend(TENANT_ID, CONFIG_TYPE));
    }

    @Test
    @DisplayName("Should deny when config not found")
    void shouldDenyWhenConfigNotFound() {
        when(configRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.empty());

        assertFalse(rateLimitService.canSend(TENANT_ID, CONFIG_TYPE));
    }

    @Test
    @DisplayName("Should increment counters on recordSend")
    void shouldIncrementCountersOnRecordSend() {
        RateLimitTracker tracker = createTracker(5, 50, 200);

        when(trackerRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(tracker));
        when(trackerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        rateLimitService.recordSend(TENANT_ID, CONFIG_TYPE);

        ArgumentCaptor<RateLimitTracker> captor = ArgumentCaptor.forClass(RateLimitTracker.class);
        verify(trackerRepository).save(captor.capture());

        RateLimitTracker saved = captor.getValue();
        assertEquals(6, saved.getMinuteCount());
        assertEquals(51, saved.getHourCount());
        assertEquals(201, saved.getDayCount());
    }

    @Test
    @DisplayName("Should throttle on SMTP 421 error")
    void shouldThrottleOnSmtp421Error() {
        RateLimitTracker tracker = createTracker(5, 50, 200);

        when(trackerRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(tracker));
        when(trackerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        rateLimitService.handleSmtpError(TENANT_ID, CONFIG_TYPE,
                new RuntimeException("421 Too many connections"));

        ArgumentCaptor<RateLimitTracker> captor = ArgumentCaptor.forClass(RateLimitTracker.class);
        verify(trackerRepository).save(captor.capture());

        RateLimitTracker saved = captor.getValue();
        assertTrue(saved.getIsThrottled());
        assertNotNull(saved.getThrottledUntil());
    }

    @Test
    @DisplayName("Should deny when throttled")
    void shouldDenyWhenThrottled() {
        TenantConfig config = createConfig(10, 100, 500);
        RateLimitTracker tracker = createTracker(0, 0, 0);
        tracker.setIsThrottled(true);
        tracker.setThrottledUntil(Instant.now().plus(5, ChronoUnit.MINUTES));

        when(configRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(config));
        when(trackerRepository.findByTenantIdAndConfigType(TENANT_ID, CONFIG_TYPE))
                .thenReturn(Optional.of(tracker));

        assertFalse(rateLimitService.canSend(TENANT_ID, CONFIG_TYPE));
    }

    @Test
    @DisplayName("Legacy checkRateLimit should allow all template emails")
    void legacyCheckRateLimitShouldAllowAll() {
        // checkRateLimit is a no-op for template emails (system-generated)
        assertDoesNotThrow(() -> rateLimitService.checkRateLimit("user-1"));
        assertDoesNotThrow(() -> rateLimitService.checkRateLimit(null));

        // Can call many times without exception
        for (int i = 0; i < 100; i++) {
            assertDoesNotThrow(() -> rateLimitService.checkRateLimit("user-1"));
        }
    }
}
