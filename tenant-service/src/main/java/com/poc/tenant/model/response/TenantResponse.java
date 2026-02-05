package com.poc.tenant.model.response;

import com.poc.tenant.tenant.domain.Tenant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {
    private UUID id;
    private String name;
    private String slug;
    private String tenantType;
    private String status;
    private String subscriptionStatus;
    private UUID parentTenantId;
    private Instant trialEndsAt;
    private Instant createdAt;
    private Instant updatedAt;
    private String billingModel;
    private BigDecimal commissionRate;
    private BigDecimal wholesaleDiscount;
    private UUID financeWalletId;
    private UUID kanbanBoardId;
    private String provisioningStatus;

    public static TenantResponse from(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .slug(tenant.getSlug())
                .tenantType(tenant.getTenantType())
                .status(tenant.getStatus())
                .subscriptionStatus(tenant.getSubscriptionStatus())
                .parentTenantId(tenant.getParentTenantId())
                .trialEndsAt(tenant.getTrialEndsAt())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .billingModel(tenant.getBillingModel())
                .commissionRate(tenant.getCommissionRate())
                .wholesaleDiscount(tenant.getWholesaleDiscount())
                .financeWalletId(tenant.getFinanceWalletId())
                .kanbanBoardId(tenant.getKanbanBoardId())
                .provisioningStatus(tenant.getProvisioningStatus())
                .build();
    }
}
