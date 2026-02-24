package com.poc.notification.repository;

import com.poc.notification.domain.CalendarEvent;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarEventRepository extends CrudRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByTenantIdAndUserIdOrderByStartTimeAsc(String tenantId, String userId);

    List<CalendarEvent> findByTenantIdAndUserIdAndTagOrderByStartTimeAsc(String tenantId, String userId, String tag);

    Optional<CalendarEvent> findByIdAndTenantId(Long id, String tenantId);

    @Query("SELECT * FROM NOTF_CALENDAR_EVENT WHERE TENANT_ID = :tenantId AND USER_ID = :userId " +
           "AND START_TIME >= :start AND END_TIME <= :end ORDER BY START_TIME ASC")
    List<CalendarEvent> findByDateRange(
            @Param("tenantId") String tenantId,
            @Param("userId") String userId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}
