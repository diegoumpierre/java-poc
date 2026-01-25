package com.poc.auth.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatusResponse {
    private boolean allHealthy;
    private int totalServices;
    private int healthyServices;
    private List<ServiceStatus> services;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceStatus {
        private String name;
        private String url;
        private String status; // UP, DOWN
        private Long responseTime; // in milliseconds
        private String error;
    }
}
