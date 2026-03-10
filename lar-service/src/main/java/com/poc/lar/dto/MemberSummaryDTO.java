package com.poc.lar.dto;

import java.util.UUID;

public record MemberSummaryDTO(
    UUID memberId,
    String nickname,
    String roleType,
    Integer pointsBalance,
    Integer pendingChores
) {}
