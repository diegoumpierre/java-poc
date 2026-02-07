package com.poc.tenant.repository;

import com.poc.tenant.domain.UsageMetric;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsageMetricRepository extends CrudRepository<UsageMetric, UUID> {

    @Query("SELECT * FROM TNT_MTR_USAGE_METRICS WHERE TENANT_ID = :tenantId ORDER BY PERIOD_START DESC")
    List<UsageMetric> findByTenantIdOrderByPeriodStartDesc(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM TNT_MTR_USAGE_METRICS WHERE TENANT_ID = :tenantId AND METRIC_TYPE = :metricType ORDER BY PERIOD_START DESC")
    List<UsageMetric> findByTenantIdAndMetricTypeOrderByPeriodStartDesc(@Param("tenantId") UUID tenantId, @Param("metricType") String metricType);

    @Query("SELECT * FROM TNT_MTR_USAGE_METRICS WHERE TENANT_ID = :tenantId AND PERIOD_START >= :startDate AND PERIOD_END <= :endDate ORDER BY PERIOD_START DESC")
    List<UsageMetric> findByTenantIdAndDateRange(@Param("tenantId") UUID tenantId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT * FROM TNT_MTR_USAGE_METRICS WHERE TENANT_ID = :tenantId AND METRIC_TYPE = :metricType AND PERIOD_START = :periodStart AND PERIOD_END = :periodEnd LIMIT 1")
    Optional<UsageMetric> findByTenantIdAndMetricTypeAndPeriod(@Param("tenantId") UUID tenantId, @Param("metricType") String metricType, @Param("periodStart") LocalDate periodStart, @Param("periodEnd") LocalDate periodEnd);

    @Query("SELECT COALESCE(SUM(METRIC_VALUE), 0) FROM TNT_MTR_USAGE_METRICS WHERE TENANT_ID = :tenantId AND METRIC_TYPE = :metricType AND PERIOD_START >= :since")
    BigDecimal sumByTenantIdAndMetricTypeSince(@Param("tenantId") UUID tenantId, @Param("metricType") String metricType, @Param("since") LocalDate since);

    @Query("SELECT * FROM TNT_MTR_USAGE_METRICS WHERE TENANT_ID = :tenantId AND METRIC_TYPE = :metricType ORDER BY PERIOD_END DESC LIMIT 1")
    Optional<UsageMetric> findLatestByTenantIdAndMetricType(@Param("tenantId") UUID tenantId, @Param("metricType") String metricType);

    @Query("SELECT COALESCE(AVG(METRIC_VALUE), 0) FROM TNT_MTR_USAGE_METRICS WHERE TENANT_ID = :tenantId AND METRIC_TYPE = :metricType AND PERIOD_START >= :since")
    BigDecimal avgByTenantIdAndMetricTypeSince(@Param("tenantId") UUID tenantId, @Param("metricType") String metricType, @Param("since") LocalDate since);

    @Query("SELECT * FROM TNT_MTR_USAGE_METRICS WHERE TENANT_ID = :tenantId AND PERIOD_TYPE = 'DAILY' ORDER BY PERIOD_START DESC LIMIT :limit")
    List<UsageMetric> findDailyMetrics(@Param("tenantId") UUID tenantId, @Param("limit") int limit);

    @Query("SELECT * FROM TNT_MTR_USAGE_METRICS WHERE METRIC_TYPE = :metricType ORDER BY PERIOD_START DESC")
    List<UsageMetric> findByMetricTypeOrderByPeriodStartDesc(@Param("metricType") String metricType);

    @Query("SELECT COALESCE(SUM(METRIC_VALUE), 0) FROM TNT_MTR_USAGE_METRICS WHERE METRIC_TYPE = :metricType AND PERIOD_START >= :since")
    BigDecimal sumAllTenantsByMetricTypeSince(@Param("metricType") String metricType, @Param("since") LocalDate since);

    @Query("SELECT COUNT(DISTINCT TENANT_ID) FROM TNT_MTR_USAGE_METRICS WHERE PERIOD_START >= :since")
    long countDistinctTenantsWithUsageSince(@Param("since") LocalDate since);

    @Modifying
    @Query("UPDATE TNT_MTR_USAGE_METRICS SET METRIC_VALUE = METRIC_VALUE + :increment, UPDATED_AT = NOW() WHERE TENANT_ID = :tenantId AND METRIC_TYPE = :metricType AND PERIOD_START = :periodStart")
    int incrementMetricValue(@Param("tenantId") UUID tenantId, @Param("metricType") String metricType, @Param("periodStart") LocalDate periodStart, @Param("increment") BigDecimal increment);

    @Modifying
    @Query("DELETE FROM TNT_MTR_USAGE_METRICS WHERE PERIOD_END < :before")
    int deleteOldMetrics(@Param("before") LocalDate before);
}
