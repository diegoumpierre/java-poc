package com.poc.auth.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CleanupStatsResponse {

    private long expiredInvites;
    private long resolvedAccessRequests;
    private long expiredTrialTenants;

    public long getTotalCleanableItems() {
        return expiredInvites + resolvedAccessRequests;
    }
}
