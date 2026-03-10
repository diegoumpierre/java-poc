package com.poc.lar.config;

import com.poc.lar.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    @Value("${app.security.enabled:false}")
    private boolean securityEnabled;

    @Value("${cors.enabled:false}")
    private boolean corsEnabled;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired(required = false)
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Security enabled: {}, CORS enabled: {}", securityEnabled, corsEnabled);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (corsEnabled && corsConfigurationSource != null) {
            log.info("CORS enabled for development");
            http.cors(cors -> cors.configurationSource(corsConfigurationSource));
        } else {
            log.info("CORS disabled - API Gateway handles CORS in production");
            http.cors(AbstractHttpConfigurer::disable);
        }

        if (securityEnabled) {
            log.info("Configuring security with JWT authentication");
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/error",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/api-docs/**",
                                    "/v3/api-docs/**",
                                    "/actuator/health",
                                    "/actuator/info",
                                    "/api/lar/emergency/public/**"
                            ).permitAll()
                            .anyRequest().authenticated()
                    );

            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        } else {
            log.warn("Security is DISABLED - all requests are permitted without authentication");
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }
}
