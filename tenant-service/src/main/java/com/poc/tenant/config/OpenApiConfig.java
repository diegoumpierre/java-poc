package com.poc.tenant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Tenant Service API")
                .description("API for managing tenants, hierarchy, and menus")
                .version("1.0.0")
                .contact(new Contact()
                    .name("101 Softwares")
                    .email("contact@example.com")))
            .servers(List.of(
                new Server().url("http://localhost:8094").description("Local"),
                new Server().url("http://localhost:3001/api/tenants").description("Via Next.js (tenant-service)")
            ));
    }
}
