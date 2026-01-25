package com.poc.auth.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingStatsResponse {

    // Active counts
    private long totalUsersWithoutTenant;
    private long totalPendingInvites;
    private long totalPendingAccessRequests;
    private long totalTrialTenants;
    private long tenantsExpiringIn7Days;

    // Cleanup counts (items that can be cleaned up)
    private long expiredInvites;
    private long resolvedAccessRequests;
    private long expiredTrialTenants;

    public long getTotalCleanableItems() {
        return expiredInvites + resolvedAccessRequests;
    }
}
