package com.poc.auth.service.impl;

import com.poc.auth.model.response.SystemStatusResponse;
import com.poc.auth.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SystemServiceImpl implements SystemService {

    @Value("${app.project.root:/app}")
    private String projectRoot;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final List<Map<String, String>> SERVICES = List.of(
            Map.of("name", "Auth Service", "url", "http://localhost:8091/actuator/health"),
            Map.of("name", "Kanban Service", "url", "http://localhost:8081/actuator/health"),
            Map.of("name", "Product Service", "url", "http://localhost:8082/actuator/health"),
            Map.of("name", "Project Service", "url", "http://localhost:8083/actuator/health")
    );

    @Override
    public SystemStatusResponse checkServicesStatus() {
        List<SystemStatusResponse.ServiceStatus> serviceStatuses = new ArrayList<>();
        int healthyCount = 0;

        for (Map<String, String> service : SERVICES) {
            String name = service.get("name");
            String url = service.get("url");

            SystemStatusResponse.ServiceStatus status = checkSingleService(name, url);
            serviceStatuses.add(status);

            if ("UP".equals(status.getStatus())) {
                healthyCount++;
            }
        }

        return SystemStatusResponse.builder()
                .totalServices(SERVICES.size())
                .healthyServices(healthyCount)
                .allHealthy(healthyCount == SERVICES.size())
                .services(serviceStatuses)
                .build();
    }

    private SystemStatusResponse.ServiceStatus checkSingleService(String name, String url) {
        long startTime = System.currentTimeMillis();

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Map.class
            );

            long responseTime = System.currentTimeMillis() - startTime;

            Map<String, Object> body = response.getBody();
            String status = body != null && "UP".equals(body.get("status")) ? "UP" : "DOWN";

            return SystemStatusResponse.ServiceStatus.builder()
                    .name(name)
                    .url(url)
                    .status(status)
                    .responseTime(responseTime)
                    .build();

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("Error checking service {}: {}", name, e.getMessage());

            return SystemStatusResponse.ServiceStatus.builder()
                    .name(name)
                    .url(url)
                    .status("DOWN")
                    .responseTime(responseTime)
                    .error(e.getMessage())
                    .build();
        }
    }

    @Override
    public boolean stopAllServices() {
        try {
            String scriptPath = projectRoot + "/dev-resources/scripts/utils/stop-all-services.sh";
            log.info("Executing stop script: {}", scriptPath);

            ProcessBuilder processBuilder = new ProcessBuilder("bash", scriptPath);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Log output
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("Script output: {}", line);
                }
            }

            int exitCode = process.waitFor();
            log.info("Stop script finished with exit code: {}", exitCode);

            return exitCode == 0;

        } catch (Exception e) {
            log.error("Error executing stop script", e);
            return false;
        }
    }
}
