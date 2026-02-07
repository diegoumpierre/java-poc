package com.poc.tenant.service;

import com.poc.tenant.domain.UsageMetric;
import com.poc.tenant.repository.UsageMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageMetricsService {

    private final UsageMetricRepository usageMetricRepository;

    @Transactional
    public UsageMetric recordMetric(UUID tenantId, String metricType, BigDecimal value,
                                    LocalDate periodStart, LocalDate periodEnd) {
        Optional<UsageMetric> existing = usageMetricRepository.findByTenantIdAndMetricTypeAndPeriod(
                tenantId, metricType, periodStart, periodEnd);

        if (existing.isPresent()) {
            UsageMetric metric = existing.get();
            metric.setMetricValue(value);
            metric.setUpdatedAt(java.time.Instant.now());
            metric.markNotNew();
            return usageMetricRepository.save(metric);
        }

        UsageMetric metric = UsageMetric.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .metricType(metricType)
                .metricValue(value)
                .periodType(determinePeriodType(periodStart, periodEnd))
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .createdAt(java.time.Instant.now())
                .build();

        return usageMetricRepository.save(metric);
    }

    @Transactional
    public void recordApiCalls(UUID tenantId, long count) {
        LocalDate today = LocalDate.now();
        recordMetric(tenantId, "API_CALLS", BigDecimal.valueOf(count), today, today);
    }

    @Transactional
    public void recordStorageUsage(UUID tenantId, BigDecimal megabytes) {
        LocalDate today = LocalDate.now();
        recordMetric(tenantId, "STORAGE_MB", megabytes, today, today);
    }

    @Transactional
    public void recordActiveUsers(UUID tenantId, long count) {
        LocalDate today = LocalDate.now();
        recordMetric(tenantId, "ACTIVE_USERS", BigDecimal.valueOf(count), today, today);
    }

    @Transactional
    public void incrementMetric(UUID tenantId, String metricType, BigDecimal increment) {
        LocalDate today = LocalDate.now();
        int updated = usageMetricRepository.incrementMetricValue(tenantId, metricType, today, increment);
        if (updated == 0) {
            recordMetric(tenantId, metricType, increment, today, today);
        }
    }

    @Transactional(readOnly = true)
    public List<UsageMetric> getTenantMetrics(UUID tenantId) {
        return usageMetricRepository.findByTenantIdOrderByPeriodStartDesc(tenantId);
    }

    @Transactional(readOnly = true)
    public List<UsageMetric> getTenantMetricsByType(UUID tenantId, String metricType) {
        return usageMetricRepository.findByTenantIdAndMetricTypeOrderByPeriodStartDesc(tenantId, metricType);
    }

    @Transactional(readOnly = true)
    public List<UsageMetric> getTenantMetricsByDateRange(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        return usageMetricRepository.findByTenantIdAndDateRange(tenantId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Optional<BigDecimal> getLatestMetricValue(UUID tenantId, String metricType) {
        return usageMetricRepository.findLatestByTenantIdAndMetricType(tenantId, metricType)
                .map(UsageMetric::getMetricValue);
    }

    @Transactional(readOnly = true)
    public BigDecimal sumMetricSince(UUID tenantId, String metricType, LocalDate since) {
        return usageMetricRepository.sumByTenantIdAndMetricTypeSince(tenantId, metricType, since);
    }

    @Transactional(readOnly = true)
    public BigDecimal avgMetricSince(UUID tenantId, String metricType, LocalDate since) {
        return usageMetricRepository.avgByTenantIdAndMetricTypeSince(tenantId, metricType, since);
    }

    @Transactional(readOnly = true)
    public UsageDashboard getUsageDashboard(UUID tenantId) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);

        BigDecimal currentApiCalls = sumMetricSince(tenantId, "API_CALLS", monthStart);
        BigDecimal currentStorage = getLatestMetricValue(tenantId, "STORAGE_MB").orElse(BigDecimal.ZERO);
        BigDecimal currentActiveUsers = getLatestMetricValue(tenantId, "ACTIVE_USERS").orElse(BigDecimal.ZERO);

        List<UsageMetric> dailyApiCalls = usageMetricRepository.findDailyMetrics(tenantId, 30);

        Map<String, List<DailyMetric>> trends = new HashMap<>();
        trends.put("API_CALLS", dailyApiCalls.stream()
                .map(m -> new DailyMetric(m.getPeriodStart(), m.getMetricValue()))
                .collect(Collectors.toList()));

        BigDecimal estimatedCost = calculateEstimatedCost(currentApiCalls, currentStorage, currentActiveUsers);

        return new UsageDashboard(
                tenantId,
                java.time.Instant.now(),
                currentMonth.toString(),
                new MetricSummary("API_CALLS", currentApiCalls, "requests"),
                new MetricSummary("STORAGE_MB", currentStorage, "MB"),
                new MetricSummary("ACTIVE_USERS", currentActiveUsers, "users"),
                trends,
                estimatedCost
        );
    }

    @Transactional(readOnly = true)
    public SystemUsageSummary getSystemUsageSummary() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        BigDecimal totalApiCalls = usageMetricRepository.sumAllTenantsByMetricTypeSince("API_CALLS", thirtyDaysAgo);
        BigDecimal totalStorage = usageMetricRepository.sumAllTenantsByMetricTypeSince("STORAGE_MB", thirtyDaysAgo);
        long activeTenants = usageMetricRepository.countDistinctTenantsWithUsageSince(thirtyDaysAgo);

        List<UsageMetric> allApiMetrics = usageMetricRepository.findByMetricTypeOrderByPeriodStartDesc("API_CALLS");
        Map<UUID, BigDecimal> tenantApiUsage = allApiMetrics.stream()
                .filter(m -> m.getPeriodStart().isAfter(thirtyDaysAgo))
                .collect(Collectors.groupingBy(
                        UsageMetric::getTenantId,
                        Collectors.reducing(BigDecimal.ZERO, UsageMetric::getMetricValue, BigDecimal::add)
                ));

        List<TenantUsageSummary> topTenants = tenantApiUsage.entrySet().stream()
                .sorted(Map.Entry.<UUID, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .map(e -> new TenantUsageSummary(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new SystemUsageSummary(
                java.time.Instant.now(),
                30,
                totalApiCalls,
                totalStorage,
                activeTenants,
                topTenants
        );
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldMetrics() {
        LocalDate cutoff = LocalDate.now().minusYears(1);
        int deleted = usageMetricRepository.deleteOldMetrics(cutoff);
        log.info("Cleaned up {} old usage metrics older than {}", deleted, cutoff);
    }

    private String determinePeriodType(LocalDate start, LocalDate end) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(start, end);
        if (days <= 1) return "DAILY";
        if (days <= 7) return "WEEKLY";
        return "MONTHLY";
    }

    private BigDecimal calculateEstimatedCost(BigDecimal apiCalls, BigDecimal storageMb, BigDecimal activeUsers) {
        BigDecimal apiCost = apiCalls.divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(0.001));
        BigDecimal storageCost = storageMb.multiply(BigDecimal.valueOf(0.02));
        BigDecimal userCost = activeUsers.multiply(BigDecimal.valueOf(5));
        return apiCost.add(storageCost).add(userCost).setScale(2, RoundingMode.HALF_UP);
    }

    public record UsageDashboard(UUID tenantId, java.time.Instant generatedAt, String billingPeriod,
                                  MetricSummary apiCalls, MetricSummary storage, MetricSummary activeUsers,
                                  Map<String, List<DailyMetric>> trends, BigDecimal estimatedCost) {}

    public record MetricSummary(String metricType, BigDecimal value, String unit) {}

    public record DailyMetric(LocalDate date, BigDecimal value) {}

    public record SystemUsageSummary(java.time.Instant generatedAt, int periodDays, BigDecimal totalApiCalls,
                                      BigDecimal totalStorageMb, long activeTenants, List<TenantUsageSummary> topTenants) {}

    public record TenantUsageSummary(UUID tenantId, BigDecimal apiCalls) {}
}
