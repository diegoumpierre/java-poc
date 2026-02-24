package com.poc.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("NOTF_CALENDAR_EVENT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private String tenantId;

    @Column("USER_ID")
    private String userId;

    @Column("TITLE")
    private String title;

    @Column("DESCRIPTION")
    private String description;

    @Column("LOCATION")
    private String location;

    @Column("START_TIME")
    private Instant startTime;

    @Column("END_TIME")
    private Instant endTime;

    @Column("ALL_DAY")
    @Builder.Default
    private Boolean allDay = false;

    @Column("TAG")
    private String tag;

    @Column("BACKGROUND_COLOR")
    private String backgroundColor;

    @Column("BORDER_COLOR")
    private String borderColor;

    @Column("TEXT_COLOR")
    private String textColor;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
