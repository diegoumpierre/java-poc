package com.poc.lar.dto;

import java.util.UUID;

public record GamificationSummaryDTO(
    UUID memberId,
    String nickname,
    Integer totalPointsEarned,
    Integer totalPointsSpent,
    Integer currentBalance,
    Integer completedChores
) {}
