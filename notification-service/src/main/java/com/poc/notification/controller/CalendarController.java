package com.poc.notification.controller;

import com.poc.notification.dto.CalendarEventRequest;
import com.poc.notification.dto.CalendarEventResponse;
import com.poc.notification.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Calendar event management endpoints")
@Slf4j
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    @Operation(summary = "Get all calendar events for the current user")
    public ResponseEntity<List<CalendarEventResponse>> getAllEvents(
            @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/calendar - userId: {}", userId);
        return ResponseEntity.ok(calendarService.getAllEvents(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a calendar event by ID")
    public ResponseEntity<CalendarEventResponse> getEventById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/calendar/{} - userId: {}", id, userId);
        return ResponseEntity.ok(calendarService.getEventById(id, userId));
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Get calendar events by tag")
    public ResponseEntity<List<CalendarEventResponse>> getEventsByTag(
            @PathVariable String tag,
            @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/calendar/tag/{} - userId: {}", tag, userId);
        return ResponseEntity.ok(calendarService.getEventsByTag(tag, userId));
    }

    @PostMapping
    @Operation(summary = "Create a new calendar event")
    public ResponseEntity<CalendarEventResponse> createEvent(
            @RequestBody @Valid CalendarEventRequest request,
            @RequestHeader("X-User-Id") String userId) {
        log.info("POST /api/calendar - userId: {}, title: {}", userId, request.getTitle());
        CalendarEventResponse response = calendarService.createEvent(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a calendar event")
    public ResponseEntity<CalendarEventResponse> updateEvent(
            @PathVariable Long id,
            @RequestBody @Valid CalendarEventRequest request,
            @RequestHeader("X-User-Id") String userId) {
        log.info("PUT /api/calendar/{} - userId: {}", id, userId);
        return ResponseEntity.ok(calendarService.updateEvent(id, request, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a calendar event")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        log.info("DELETE /api/calendar/{} - userId: {}", id, userId);
        calendarService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }
}
