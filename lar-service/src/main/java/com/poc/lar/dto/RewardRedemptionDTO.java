package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RewardRedemptionDTO(
    UUID id,
    UUID rewardId,
    String rewardName,
    UUID memberId,
    String memberNickname,
    Integer pointsSpent,
    String status,
    UUID approvedBy,
    LocalDateTime redeemedAt,
    LocalDateTime createdAt
) {}
