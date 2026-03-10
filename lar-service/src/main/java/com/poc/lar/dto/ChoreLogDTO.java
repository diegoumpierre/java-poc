package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChoreLogDTO(
    UUID id,
    UUID choreId,
    String choreName,
    UUID memberId,
    String memberNickname,
    LocalDateTime completedAt,
    UUID verifiedBy,
    LocalDateTime verifiedAt,
    String photoUrl,
    Integer pointsEarned,
    String note,
    LocalDateTime createdAt
) {}
