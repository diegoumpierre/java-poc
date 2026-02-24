package com.poc.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitStatusDTO {

    private Integer minuteCount;
    private Integer minuteLimit;
    private Integer hourCount;
    private Integer hourLimit;
    private Integer dayCount;
    private Integer dayLimit;
    private Boolean isThrottled;
    private Instant throttledUntil;
    private Boolean canSend;
}
