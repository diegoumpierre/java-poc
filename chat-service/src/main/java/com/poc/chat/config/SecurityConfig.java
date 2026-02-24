package com.poc.chat.config;

import com.poc.chat.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    @Value("${app.security.enabled:false}")
    private boolean securityEnabled;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Security enabled: {}", securityEnabled);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (securityEnabled) {
            log.info("Configuring security with JWT authentication");
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/error",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/api-docs/**",
                            "/v3/api-docs/**",
                            "/actuator/health",
                            "/actuator/info",
                            "/ws/**",
                            "/api/chat/livechat/widget/**",
                            "/livechat-widget.js"
                    ).permitAll()
                    .anyRequest().authenticated()
            );
        } else {
            log.warn("Security is DISABLED - all requests are permitted without authentication");
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
