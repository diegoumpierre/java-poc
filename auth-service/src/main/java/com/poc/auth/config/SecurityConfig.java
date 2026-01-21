package com.poc.auth.config;

import com.poc.auth.security.JwtAuthenticationEntryPoint;
import com.poc.auth.security.JwtAuthenticationFilter;
import com.poc.auth.security.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;

    @Value("${cors.enabled:false}")
    private boolean corsEnabled;

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;

    @Autowired(required = false)
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Security enabled: {}, CORS enabled: {}", securityEnabled, corsEnabled);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Configure CORS - enabled in development for direct service access (Swagger, testing)
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
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(unauthorizedHandler))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/api/auth/**",
                                    "/api/profile/avatar/**",
                                    "/api/webhooks/**",
                                    "/error",
                                    "/h2-console/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/api-docs/**",
                                    "/v3/api-docs/**",
                                    "/actuator/**"
                            ).permitAll()
                            .anyRequest().authenticated()
                    );

            http.authenticationProvider(authenticationProvider());
            http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        } else {
            log.warn("Security is DISABLED - all requests are permitted without authentication");
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            // Even with security disabled, we need JWT processing for user context
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        // For H2 console
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
