package com.poc.notification.repository;

import com.poc.notification.domain.RateLimitTracker;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RateLimitTrackerRepository extends CrudRepository<RateLimitTracker, Long> {

    @Query("SELECT * FROM NOTF_RATE_LIMIT_TRACKER WHERE TENANT_ID = :tenantId AND CONFIG_TYPE = :configType")
    Optional<RateLimitTracker> findByTenantIdAndConfigType(
            @Param("tenantId") String tenantId,
            @Param("configType") String configType);
}
