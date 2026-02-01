package com.poc.tenant.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantProvisioningResult {

    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETE = "COMPLETE";
    public static final String STATUS_PARTIAL = "PARTIAL";

    private TenantResponse tenant;
    private String provisioningStatus;
    private UUID adminUserId;
    private UUID membershipId;
    private UUID subscriptionId;

    @Builder.Default
    private List<ProvisioningStep> steps = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProvisioningStep {
        private String name;
        private String status;
        private String error;

        public static ProvisioningStep success(String name) {
            return ProvisioningStep.builder()
                    .name(name)
                    .status("SUCCESS")
                    .build();
        }

        public static ProvisioningStep failed(String name, String error) {
            return ProvisioningStep.builder()
                    .name(name)
                    .status("FAILED")
                    .error(error)
                    .build();
        }

        public static ProvisioningStep pending(String name) {
            return ProvisioningStep.builder()
                    .name(name)
                    .status("PENDING")
                    .build();
        }
    }
}
