package com.poc.auth.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom metrics for Auth Service
 * Tracks business-specific operations and events
 */
@Component
@Slf4j
public class AuthMetrics {

    private final Counter loginAttemptsTotal;
    private final Counter loginAttemptsSuccess;
    private final Counter loginAttemptsFailed;
    private final Counter registrationTotal;
    private final Counter tokenGenerationTotal;
    private final Counter tokenRefreshTotal;
    private final Counter passwordResetRequests;
    private final Counter emailVerificationSent;
    private final AtomicInteger activeSessions;
    private final Timer loginDuration;

    public AuthMetrics(MeterRegistry registry) {
        // Login attempts
        this.loginAttemptsTotal = Counter.builder("auth.login.attempts")
                .description("Total number of login attempts")
                .tag("service", "auth")
                .register(registry);

        this.loginAttemptsSuccess = Counter.builder("auth.login.success")
                .description("Number of successful login attempts")
                .tag("service", "auth")
                .register(registry);

        this.loginAttemptsFailed = Counter.builder("auth.login.failed")
                .description("Number of failed login attempts")
                .tag("service", "auth")
                .register(registry);

        // Registration
        this.registrationTotal = Counter.builder("auth.registration.total")
                .description("Total number of user registrations")
                .tag("service", "auth")
                .register(registry);

        // Tokens
        this.tokenGenerationTotal = Counter.builder("auth.token.generation")
                .description("Total number of JWT tokens generated")
                .tag("service", "auth")
                .register(registry);

        this.tokenRefreshTotal = Counter.builder("auth.token.refresh")
                .description("Total number of token refresh operations")
                .tag("service", "auth")
                .register(registry);

        // Password reset
        this.passwordResetRequests = Counter.builder("auth.password.reset.requests")
                .description("Total number of password reset requests")
                .tag("service", "auth")
                .register(registry);

        // Email verification
        this.emailVerificationSent = Counter.builder("auth.email.verification.sent")
                .description("Total number of verification emails sent")
                .tag("service", "auth")
                .register(registry);

        // Active sessions (gauge)
        this.activeSessions = new AtomicInteger(0);
        registry.gauge("auth.sessions.active", activeSessions);

        // Login duration
        this.loginDuration = Timer.builder("auth.login.duration")
                .description("Time taken to process login")
                .tag("service", "auth")
                .register(registry);
    }

    // Login metrics
    public void recordLoginAttempt() {
        loginAttemptsTotal.increment();
        log.debug("Login attempt recorded");
    }

    public void recordLoginSuccess() {
        loginAttemptsSuccess.increment();
        log.debug("Successful login recorded");
    }

    public void recordLoginFailure() {
        loginAttemptsFailed.increment();
        log.debug("Failed login recorded");
    }

    public Timer.Sample startLoginTimer() {
        return Timer.start();
    }

    public void recordLoginDuration(Timer.Sample sample) {
        sample.stop(loginDuration);
    }

    // Registration metrics
    public void recordRegistration() {
        registrationTotal.increment();
        log.debug("User registration recorded");
    }

    // Token metrics
    public void recordTokenGeneration() {
        tokenGenerationTotal.increment();
        log.debug("Token generation recorded");
    }

    public void recordTokenRefresh() {
        tokenRefreshTotal.increment();
        log.debug("Token refresh recorded");
    }

    // Password reset metrics
    public void recordPasswordResetRequest() {
        passwordResetRequests.increment();
        log.debug("Password reset request recorded");
    }

    // Email verification metrics
    public void recordEmailVerificationSent() {
        emailVerificationSent.increment();
        log.debug("Email verification sent recorded");
    }

    // Session metrics
    public void incrementActiveSessions() {
        activeSessions.incrementAndGet();
        log.debug("Active sessions: {}", activeSessions.get());
    }

    public void decrementActiveSessions() {
        int current = activeSessions.get();
        if (current > 0) {
            activeSessions.decrementAndGet();
            log.debug("Active sessions: {}", activeSessions.get());
        }
    }

    public int getActiveSessions() {
        return activeSessions.get();
    }
}
