package com.poc.auth.service;

import com.poc.auth.model.response.SystemStatusResponse;

public interface SystemService {
    /**
     * Check the status of all backend services
     *
     * @return SystemStatusResponse with service status details
     */
    SystemStatusResponse checkServicesStatus();

    /**
     * Stop all backend services using the stop-all-services.sh script
     *
     * @return true if services were stopped successfully, false otherwise
     */
    boolean stopAllServices();
}
