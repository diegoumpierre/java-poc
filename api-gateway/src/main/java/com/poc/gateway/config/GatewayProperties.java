package com.poc.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    private Map<String, RouteConfig> routes = new LinkedHashMap<>();

    @Data
    public static class RouteConfig {
        private String url;
        private List<String> paths = List.of();
        private List<String> publicPaths = List.of();
        private Map<String, Integer> rateLimitedPaths = new HashMap<>();
        private List<String> requiredFeatures = List.of();
        private String stripPrefix;
    }
}
