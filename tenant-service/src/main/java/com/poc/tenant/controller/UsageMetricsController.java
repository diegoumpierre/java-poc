package com.poc.tenant.controller;

import com.poc.tenant.domain.UsageMetric;
import com.poc.tenant.service.UsageMetricsService;
import com.poc.tenant.service.UsageMetricsService.*;
import com.poc.shared.security.RequiresPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/usage")
@RequiredArgsConstructor
@Slf4j
public class UsageMetricsController {

    private final UsageMetricsService usageMetricsService;

    @GetMapping("/dashboard")
    public ResponseEntity<UsageDashboard> getUsageDashboard(
            @RequestHeader("X-Tenant-Id") UUID tenantId) {
        return ResponseEntity.ok(usageMetricsService.getUsageDashboard(tenantId));
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<UsageMetric>> getTenantMetrics(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<UsageMetric> metrics;
        if (startDate != null && endDate != null) {
            metrics = usageMetricsService.getTenantMetricsByDateRange(tenantId, startDate, endDate);
        } else {
            metrics = usageMetricsService.getTenantMetrics(tenantId);
        }
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/metrics/{metricType}")
    public ResponseEntity<List<UsageMetric>> getTenantMetricsByType(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable String metricType) {
        return ResponseEntity.ok(usageMetricsService.getTenantMetricsByType(tenantId, metricType.toUpperCase()));
    }

    @GetMapping("/metrics/{metricType}/sum")
    public ResponseEntity<MetricAggregation> getMetricSum(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable String metricType,
            @RequestParam(defaultValue = "30") int days) {
        LocalDate since = LocalDate.now().minusDays(days);
        BigDecimal sum = usageMetricsService.sumMetricSince(tenantId, metricType.toUpperCase(), since);
        return ResponseEntity.ok(new MetricAggregation(metricType.toUpperCase(), "SUM", sum, since, LocalDate.now()));
    }

    @GetMapping("/metrics/{metricType}/avg")
    public ResponseEntity<MetricAggregation> getMetricAvg(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable String metricType,
            @RequestParam(defaultValue = "30") int days) {
        LocalDate since = LocalDate.now().minusDays(days);
        BigDecimal avg = usageMetricsService.avgMetricSince(tenantId, metricType.toUpperCase(), since);
        return ResponseEntity.ok(new MetricAggregation(metricType.toUpperCase(), "AVG", avg, since, LocalDate.now()));
    }

    @PostMapping("/metrics")
    @RequiresPermission("BILLING_MANAGE")
    public ResponseEntity<UsageMetric> recordMetric(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody RecordMetricRequest request) {
        UsageMetric metric = usageMetricsService.recordMetric(
                tenantId,
                request.metricType().toUpperCase(),
                request.value(),
                request.periodStart() != null ? request.periodStart() : LocalDate.now(),
                request.periodEnd() != null ? request.periodEnd() : LocalDate.now()
        );
        return ResponseEntity.ok(metric);
    }

    @PostMapping("/metrics/{metricType}/increment")
    @RequiresPermission("BILLING_MANAGE")
    public ResponseEntity<Void> incrementMetric(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable String metricType,
            @RequestParam(defaultValue = "1") BigDecimal amount) {
        usageMetricsService.incrementMetric(tenantId, metricType.toUpperCase(), amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/system")
    public ResponseEntity<SystemUsageSummary> getSystemUsageSummary() {
        return ResponseEntity.ok(usageMetricsService.getSystemUsageSummary());
    }

    public record RecordMetricRequest(String metricType, BigDecimal value,
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd,
                                       UUID productId, String metadata) {}

    public record MetricAggregation(String metricType, String aggregationType, BigDecimal value,
                                     LocalDate periodStart, LocalDate periodEnd) {}
}
