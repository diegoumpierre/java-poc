package com.poc.auth.controller;

import com.poc.auth.model.response.SystemStatusResponse;
import com.poc.auth.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "System", description = "System management APIs")
public class SystemController {

    private final SystemService systemService;

    /**
     * GET /api/system/services/status
     * Check status of all backend services
     */
    @GetMapping("/services/status")
    @Operation(summary = "Check services status", description = "Get status of all backend services by checking their health endpoints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SystemStatusResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<SystemStatusResponse> getServicesStatus() {
        log.info("Checking services status");
        SystemStatusResponse status = systemService.checkServicesStatus();
        return ResponseEntity.ok(status);
    }

    /**
     * POST /api/system/services/stop
     * Stop all backend services
     */
    @PostMapping("/services/stop")
    @Operation(summary = "Stop all services", description = "Stop all backend services using the stop-all-services.sh script")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Services stopped successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "500", description = "Failed to stop services")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> stopAllServices() {
        log.info("Stopping all services");
        try {
            boolean success = systemService.stopAllServices();
            if (success) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "All services are being stopped"
                ));
            } else {
                return ResponseEntity.status(500).body(Map.of(
                        "success", false,
                        "message", "Failed to stop services"
                ));
            }
        } catch (Exception e) {
            log.error("Error stopping services", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error: " + e.getMessage()
            ));
        }
    }
}
