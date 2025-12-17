package com.poc.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Value("${circuit-breaker.failure-rate-threshold:50}")
    private float failureRateThreshold;

    @Value("${circuit-breaker.wait-duration-in-open-state:30}")
    private int waitDurationInOpenState;

    @Value("${circuit-breaker.sliding-window-size:10}")
    private int slidingWindowSize;

    @Value("${circuit-breaker.permitted-calls-in-half-open:3}")
    private int permittedCallsInHalfOpen;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .slidingWindowSize(slidingWindowSize)
                .permittedNumberOfCallsInHalfOpenState(permittedCallsInHalfOpen)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .build();

        return CircuitBreakerRegistry.of(config);
    }
}
