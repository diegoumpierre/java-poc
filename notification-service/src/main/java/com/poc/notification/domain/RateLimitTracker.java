package com.poc.notification.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("NOTF_RATE_LIMIT_TRACKER")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitTracker {

    @Id
    @Column("ID")
    private Long id;

    @Column("TENANT_ID")
    private String tenantId;

    @Column("CONFIG_TYPE")
    private String configType;

    @Column("MINUTE_COUNT")
    @Builder.Default
    private Integer minuteCount = 0;

    @Column("MINUTE_WINDOW_START")
    private Instant minuteWindowStart;

    @Column("HOUR_COUNT")
    @Builder.Default
    private Integer hourCount = 0;

    @Column("HOUR_WINDOW_START")
    private Instant hourWindowStart;

    @Column("DAY_COUNT")
    @Builder.Default
    private Integer dayCount = 0;

    @Column("DAY_WINDOW_START")
    private Instant dayWindowStart;

    @Column("IS_THROTTLED")
    @Builder.Default
    private Boolean isThrottled = false;

    @Column("THROTTLED_UNTIL")
    private Instant throttledUntil;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
