package com.poc.notification.service;

import com.poc.shared.tenant.TenantContext;
import com.poc.notification.domain.CalendarEvent;
import com.poc.notification.dto.CalendarEventRequest;
import com.poc.notification.dto.CalendarEventResponse;
import com.poc.notification.repository.CalendarEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CalendarService {

    private final CalendarEventRepository repository;

    public List<CalendarEventResponse> getAllEvents(String userId) {
        String tenantId = getTenantId();
        log.debug("Fetching all calendar events for user {} in tenant {}", userId, tenantId);

        return repository.findByTenantIdAndUserIdOrderByStartTimeAsc(tenantId, userId)
                .stream()
                .map(CalendarEventResponse::fromEntity)
                .toList();
    }

    public CalendarEventResponse getEventById(Long id, String userId) {
        String tenantId = getTenantId();
        log.debug("Fetching calendar event {} for user {} in tenant {}", id, userId, tenantId);

        return repository.findByIdAndTenantId(id, tenantId)
                .filter(event -> event.getUserId().equals(userId))
                .map(CalendarEventResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));
    }

    public List<CalendarEventResponse> getEventsByTag(String tag, String userId) {
        String tenantId = getTenantId();
        log.debug("Fetching calendar events by tag {} for user {} in tenant {}", tag, userId, tenantId);

        return repository.findByTenantIdAndUserIdAndTagOrderByStartTimeAsc(tenantId, userId, tag)
                .stream()
                .map(CalendarEventResponse::fromEntity)
                .toList();
    }

    public List<CalendarEventResponse> getEventsByDateRange(String userId, Instant start, Instant end) {
        String tenantId = getTenantId();
        log.debug("Fetching calendar events in range [{} - {}] for user {} in tenant {}", start, end, userId, tenantId);

        return repository.findByDateRange(tenantId, userId, start, end)
                .stream()
                .map(CalendarEventResponse::fromEntity)
                .toList();
    }

    public CalendarEventResponse createEvent(CalendarEventRequest request, String userId) {
        String tenantId = getTenantId();
        log.info("Creating calendar event '{}' for user {} in tenant {}", request.getTitle(), userId, tenantId);

        CalendarEvent event = CalendarEvent.builder()
                .tenantId(tenantId)
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .startTime(request.getStart())
                .endTime(request.getEnd())
                .allDay(request.getAllDay() != null ? request.getAllDay() : false)
                .tag(request.getTag())
                .backgroundColor(request.getBackgroundColor())
                .borderColor(request.getBorderColor())
                .textColor(request.getTextColor())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        event = repository.save(event);
        log.info("Created calendar event with ID {} for user {}", event.getId(), userId);

        return CalendarEventResponse.fromEntity(event);
    }

    public CalendarEventResponse updateEvent(Long id, CalendarEventRequest request, String userId) {
        String tenantId = getTenantId();
        log.info("Updating calendar event {} for user {} in tenant {}", id, userId, tenantId);

        CalendarEvent event = repository.findByIdAndTenantId(id, tenantId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartTime(request.getStart());
        event.setEndTime(request.getEnd());
        event.setAllDay(request.getAllDay() != null ? request.getAllDay() : false);
        event.setTag(request.getTag());
        event.setBackgroundColor(request.getBackgroundColor());
        event.setBorderColor(request.getBorderColor());
        event.setTextColor(request.getTextColor());
        event.setUpdatedAt(Instant.now());

        event = repository.save(event);
        log.info("Updated calendar event {}", id);

        return CalendarEventResponse.fromEntity(event);
    }

    public void deleteEvent(Long id, String userId) {
        String tenantId = getTenantId();
        log.info("Deleting calendar event {} for user {} in tenant {}", id, userId, tenantId);

        CalendarEvent event = repository.findByIdAndTenantId(id, tenantId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));

        repository.delete(event);
        log.info("Deleted calendar event {}", id);
    }

    private String getTenantId() {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context is required for calendar operations");
        }
        return tenantId.toString();
    }
}
