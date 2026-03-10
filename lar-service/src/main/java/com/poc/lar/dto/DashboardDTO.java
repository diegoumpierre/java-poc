package com.poc.lar.dto;

import java.util.List;

public record DashboardDTO(
    Integer pendingOutings,
    Integer activeOutings,
    Integer todayChores,
    Integer completedChoresThisWeek,
    Integer upcomingAppointments,
    Integer overduePayments,
    Integer expiringDocuments,
    List<MemberSummaryDTO> familyMembers
) {}
