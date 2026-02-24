package com.poc.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String location;

    @NotNull(message = "Start time is required")
    private Instant start;

    @NotNull(message = "End time is required")
    private Instant end;

    private Boolean allDay;

    private String tag;

    private String backgroundColor;

    private String borderColor;

    private String textColor;
}
