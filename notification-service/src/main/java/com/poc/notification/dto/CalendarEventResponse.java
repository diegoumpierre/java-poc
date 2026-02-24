package com.poc.notification.dto;

import com.poc.notification.domain.CalendarEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private Instant start;
    private Instant end;
    private Boolean allDay;
    private String tag;
    private String backgroundColor;
    private String borderColor;
    private String textColor;
    private Instant createdAt;
    private Instant updatedAt;

    public static CalendarEventResponse fromEntity(CalendarEvent event) {
        return CalendarEventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .start(event.getStartTime())
                .end(event.getEndTime())
                .allDay(event.getAllDay())
                .tag(event.getTag())
                .backgroundColor(event.getBackgroundColor())
                .borderColor(event.getBorderColor())
                .textColor(event.getTextColor())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
