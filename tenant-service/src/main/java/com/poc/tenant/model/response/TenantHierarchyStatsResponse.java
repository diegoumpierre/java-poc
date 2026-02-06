package com.poc.tenant.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantHierarchyStatsResponse {
    private long totalTenants;
    private long totalPartners;
    private long totalClients;
    private long activePartners;
    private long activeClients;
    private long suspendedTenants;
    private long tenantsOnTrial;
}
