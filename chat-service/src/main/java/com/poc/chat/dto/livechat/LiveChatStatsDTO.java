package com.poc.chat.dto.livechat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveChatStatsDTO {

    private long totalSessions;
    private long activeSessions;
    private long waitingSessions;
    private long closedSessions;
    private long abandonedSessions;
    private Double averageRating;
    private Double averageResponseTimeSeconds;
}
