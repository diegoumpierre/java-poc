package com.poc.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntitlementResponse {
    private UUID id;
    private UUID tenantId;
    private UUID subscriptionId;
    private UUID featureId;
    private String featureCode;
    private String featureName;
    private Boolean enabled;
    private Integer usageLimit;
    private Integer currentUsage;
    private Instant validFrom;
    private Instant validUntil;
    private Instant createdAt;
}
