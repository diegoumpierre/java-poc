package com.poc.lar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Lar Service API")
                .version("1.0.0")
                .description("101 Lar - Family management platform")
                .contact(new Contact()
                    .name("101 Softwares")
                    .email("support@example.com")))
            .addSecurityItem(new SecurityRequirement().addList("X-User-Id"))
            .addSecurityItem(new SecurityRequirement().addList("X-Tenant-Id"))
            .components(new Components()
                .addSecuritySchemes("X-User-Id",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-User-Id"))
                .addSecuritySchemes("X-Tenant-Id",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-Tenant-Id")));
    }
}
