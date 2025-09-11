package br.dev;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Advanced real-world examples of Sealed Classes in Java 17+
 * Demonstrates complex enterprise patterns and microservices scenarios
 */
public class AdvancedSealedClassExamples {

    public static void main(String[] args) {
        // Circuit breaker pattern
        demonstrateCircuitBreaker();

        // Authentication and authorization
        demonstrateAuthentication();

        // Cache operations
        demonstrateCacheOperations();

        // Message queue processing
        demonstrateMessageProcessing();

        // Health check monitoring
        demonstrateHealthChecks();

        // Financial transactions
        demonstrateFinancialTransactions();

        // Deployment strategies
        demonstrateDeploymentStrategies();
    }

    private static void demonstrateCircuitBreaker() {
        System.out.println("=== Circuit Breaker Pattern Examples ===");

        CircuitBreakerState closed = new Closed(0, LocalDateTime.now());
        CircuitBreakerState open = new Open(LocalDateTime.now().plusMinutes(5), "Too many failures");
        CircuitBreakerState halfOpen = new HalfOpen(LocalDateTime.now(), 3);

        processCircuitBreakerState(closed);
        processCircuitBreakerState(open);
        processCircuitBreakerState(halfOpen);
        System.out.println();
    }

    private static void processCircuitBreakerState(CircuitBreakerState state) {
        String action = switch (state) {
            case Closed c -> "🟢 Circuit CLOSED - " + c.failureCount() + " failures, allowing requests";
            case Open o -> "🔴 Circuit OPEN - Blocked until " + o.retryAfter() + " (reason: " + o.reason() + ")";
            case HalfOpen ho -> "🟡 Circuit HALF-OPEN - Testing with " + ho.testRequestsRemaining() + " test requests";
        };
        System.out.println(action);
    }

    private static void demonstrateAuthentication() {
        System.out.println("=== Authentication Examples ===");

        AuthResult success = new AuthSuccess("user123", List.of("ADMIN", "USER"), "jwt-token-123");
        AuthResult invalidCreds = new InvalidCredentials("user456", "Invalid password");
        AuthResult locked = new AccountLocked("user789", LocalDateTime.now().plusHours(24));
        AuthResult mfaRequired = new MfaRequired("user000", "SMS", "+1234567890");
        AuthResult expired = new SessionExpired("session123", LocalDateTime.now().minusHours(8));

        handleAuthResult(success);
        handleAuthResult(invalidCreds);
        handleAuthResult(locked);
        handleAuthResult(mfaRequired);
        handleAuthResult(expired);
        System.out.println();
    }

    private static void handleAuthResult(AuthResult result) {
        String message = switch (result) {
            case AuthSuccess as -> "✅ User " + as.userId() + " authenticated with roles: " + as.roles();
            case InvalidCredentials ic -> "❌ Authentication failed for " + ic.userId() + ": " + ic.reason();
            case AccountLocked al -> "🔒 Account " + al.userId() + " locked until " + al.unlockAt();
            case MfaRequired mfa -> "🔐 MFA required for " + mfa.userId() + " via " + mfa.method() + " to " + mfa.destination();
            case SessionExpired se -> "⏰ Session " + se.sessionId() + " expired at " + se.expiredAt();
        };
        System.out.println(message);
    }

    private static void demonstrateCacheOperations() {
        System.out.println("=== Cache Operations Examples ===");

        CacheResult<String> hit = new CacheHit<>("user:123", "John Doe", Duration.ofMinutes(30));
        CacheResult<String> miss = new CacheMiss<>("user:456");
        CacheResult<String> expired = new CacheExpired<>("user:789", LocalDateTime.now().minusMinutes(5));
        CacheResult<String> error = new CacheError<>("user:000", "Redis connection timeout");

        processCacheResult(hit);
        processCacheResult(miss);
        processCacheResult(expired);
        processCacheResult(error);
        System.out.println();
    }

    private static void processCacheResult(CacheResult<String> result) {
        String output = switch (result) {
            case CacheHit<String> ch -> "🎯 Cache HIT for '" + ch.key() + "': " + ch.value() + " (TTL: " + ch.ttl().toMinutes() + "m)";
            case CacheMiss<String> cm -> "❌ Cache MISS for '" + cm.key() + "'";
            case CacheExpired<String> ce -> "⏰ Cache EXPIRED for '" + ce.key() + "' at " + ce.expiredAt();
            case CacheError<String> cerr -> "💥 Cache ERROR for '" + cerr.key() + "': " + cerr.error();
        };
        System.out.println(output);
    }

    private static void demonstrateMessageProcessing() {
        System.out.println("=== Message Queue Processing Examples ===");

        MessageProcessingResult success = new ProcessingSuccess("msg-001", Duration.ofMillis(150));
        MessageProcessingResult retry = new ProcessingRetry("msg-002", 3, "Temporary service unavailable");
        MessageProcessingResult deadLetter = new DeadLetter("msg-003", "Max retries exceeded", 5);
        MessageProcessingResult poison = new PoisonMessage("msg-004", "Invalid JSON format");

        handleMessageResult(success);
        handleMessageResult(retry);
        handleMessageResult(deadLetter);
        handleMessageResult(poison);
        System.out.println();
    }

    private static void handleMessageResult(MessageProcessingResult result) {
        String output = switch (result) {
            case ProcessingSuccess ps -> "✅ Message " + ps.messageId() + " processed in " + ps.processingTime().toMillis() + "ms";
            case ProcessingRetry pr -> "🔄 Message " + pr.messageId() + " retry " + pr.retryCount() + ": " + pr.reason();
            case DeadLetter dl -> "💀 Message " + dl.messageId() + " moved to DLQ after " + dl.totalRetries() + " retries: " + dl.reason();
            case PoisonMessage pm -> "☠️ Poison message " + pm.messageId() + " quarantined: " + pm.error();
        };
        System.out.println(output);
    }

    private static void demonstrateHealthChecks() {
        System.out.println("=== Health Check Examples ===");

        HealthStatus healthy = new Healthy("database", Duration.ofMillis(45));
        HealthStatus degraded = new Degraded("cache", "High response time", Duration.ofSeconds(2));
        HealthStatus unhealthy = new Unhealthy("payment-service", "Connection refused");
        HealthStatus unknown = new Unknown("external-api", "Timeout during health check");

        reportHealthStatus(healthy);
        reportHealthStatus(degraded);
        reportHealthStatus(unhealthy);
        reportHealthStatus(unknown);
        System.out.println();
    }

    private static void reportHealthStatus(HealthStatus status) {
        String report = switch (status) {
            case Healthy h -> "🟢 " + h.serviceName() + " is HEALTHY (response: " + h.responseTime().toMillis() + "ms)";
            case Degraded d -> "🟡 " + d.serviceName() + " is DEGRADED: " + d.issue() + " (response: " + d.responseTime().toMillis() + "ms)";
            case Unhealthy u -> "🔴 " + u.serviceName() + " is UNHEALTHY: " + u.error();
            case Unknown unk -> "⚪ " + unk.serviceName() + " status UNKNOWN: " + unk.reason();
        };
        System.out.println(report);
    }

    private static void demonstrateFinancialTransactions() {
        System.out.println("=== Financial Transaction Examples ===");

        TransactionResult success = new TransactionSuccess("txn-001", new BigDecimal("100.50"), "USD");
        TransactionResult insufficientFunds = new InsufficientFunds("txn-002", new BigDecimal("1000.00"), new BigDecimal("50.25"));
        TransactionResult fraudDetected = new FraudDetected("txn-003", "Unusual spending pattern", 85);
        TransactionResult limitExceeded = new LimitExceeded("txn-004", new BigDecimal("5000.00"), new BigDecimal("2500.00"));

        processTransaction(success);
        processTransaction(insufficientFunds);
        processTransaction(fraudDetected);
        processTransaction(limitExceeded);
        System.out.println();
    }

    private static void processTransaction(TransactionResult result) {
        String output = switch (result) {
            case TransactionSuccess ts -> "✅ Transaction " + ts.transactionId() + " completed: " + ts.currency() + " " + ts.amount();
            case InsufficientFunds if_ -> "💳 Transaction " + if_.transactionId() + " declined: Need " + if_.requestedAmount() + ", available " + if_.availableBalance();
            case FraudDetected fd -> "🚨 Transaction " + fd.transactionId() + " blocked for fraud (risk: " + fd.riskScore() + "%): " + fd.reason();
            case LimitExceeded le -> "⚠️ Transaction " + le.transactionId() + " exceeds limit: " + le.requestedAmount() + " > " + le.dailyLimit();
        };
        System.out.println(output);
    }

    private static void demonstrateDeploymentStrategies() {
        System.out.println("=== Deployment Strategy Examples ===");

        DeploymentStrategy blueGreen = new BlueGreenDeployment("prod-v2.1", "blue", "green", 100);
        DeploymentStrategy canary = new CanaryDeployment("prod-v2.1", 5, Map.of("region", "us-west", "userType", "premium"));
        DeploymentStrategy rolling = new RollingDeployment("prod-v2.1", 3, 10, Duration.ofMinutes(2));
        DeploymentStrategy recreate = new RecreateDeployment("prod-v2.1", Duration.ofMinutes(5));

        executeDeployment(blueGreen);
        executeDeployment(canary);
        executeDeployment(rolling);
        executeDeployment(recreate);
    }

    private static void executeDeployment(DeploymentStrategy strategy) {
        String description = switch (strategy) {
            case BlueGreenDeployment bg -> "🔵🟢 Blue-Green deployment of " + bg.version() + ": " + bg.trafficPercentage() + "% traffic to " + bg.activeSlot();
            case CanaryDeployment c -> "🐤 Canary deployment of " + c.version() + ": " + c.trafficPercentage() + "% with criteria " + c.targetCriteria();
            case RollingDeployment r -> "🔄 Rolling deployment of " + r.version() + ": " + r.batchSize() + "/" + r.totalInstances() + " instances every " + r.batchInterval().toMinutes() + "m";
            case RecreateDeployment rec -> "🔄 Recreate deployment of " + rec.version() + " with " + rec.downtime().toMinutes() + "m downtime";
        };
        System.out.println(description);
    }
}

// =============== Circuit Breaker Pattern ===============
sealed interface CircuitBreakerState permits Closed, Open, HalfOpen {}

record Closed(int failureCount, LocalDateTime lastFailure) implements CircuitBreakerState {}
record Open(LocalDateTime retryAfter, String reason) implements CircuitBreakerState {}
record HalfOpen(LocalDateTime openedAt, int testRequestsRemaining) implements CircuitBreakerState {}

// =============== Authentication Pattern ===============
sealed interface AuthResult permits AuthSuccess, InvalidCredentials, AccountLocked, MfaRequired, SessionExpired {}

record AuthSuccess(String userId, List<String> roles, String token) implements AuthResult {}
record InvalidCredentials(String userId, String reason) implements AuthResult {}
record AccountLocked(String userId, LocalDateTime unlockAt) implements AuthResult {}
record MfaRequired(String userId, String method, String destination) implements AuthResult {}
record SessionExpired(String sessionId, LocalDateTime expiredAt) implements AuthResult {}

// =============== Cache Operations Pattern ===============
sealed interface CacheResult<T> permits CacheHit, CacheMiss, CacheExpired, CacheError {}

record CacheHit<T>(String key, T value, Duration ttl) implements CacheResult<T> {}
record CacheMiss<T>(String key) implements CacheResult<T> {}
record CacheExpired<T>(String key, LocalDateTime expiredAt) implements CacheResult<T> {}
record CacheError<T>(String key, String error) implements CacheResult<T> {}

// =============== Message Processing Pattern ===============
sealed interface MessageProcessingResult permits ProcessingSuccess, ProcessingRetry, DeadLetter, PoisonMessage {}

record ProcessingSuccess(String messageId, Duration processingTime) implements MessageProcessingResult {}
record ProcessingRetry(String messageId, int retryCount, String reason) implements MessageProcessingResult {}
record DeadLetter(String messageId, String reason, int totalRetries) implements MessageProcessingResult {}
record PoisonMessage(String messageId, String error) implements MessageProcessingResult {}

// =============== Health Check Pattern ===============
sealed interface HealthStatus permits Healthy, Degraded, Unhealthy, Unknown {}

record Healthy(String serviceName, Duration responseTime) implements HealthStatus {}
record Degraded(String serviceName, String issue, Duration responseTime) implements HealthStatus {}
record Unhealthy(String serviceName, String error) implements HealthStatus {}
record Unknown(String serviceName, String reason) implements HealthStatus {}

// =============== Financial Transaction Pattern ===============
sealed interface TransactionResult permits TransactionSuccess, InsufficientFunds, FraudDetected, LimitExceeded {}

record TransactionSuccess(String transactionId, BigDecimal amount, String currency) implements TransactionResult {}
record InsufficientFunds(String transactionId, BigDecimal requestedAmount, BigDecimal availableBalance) implements TransactionResult {}
record FraudDetected(String transactionId, String reason, int riskScore) implements TransactionResult {}
record LimitExceeded(String transactionId, BigDecimal requestedAmount, BigDecimal dailyLimit) implements TransactionResult {}

// =============== Deployment Strategy Pattern ===============
sealed interface DeploymentStrategy permits BlueGreenDeployment, CanaryDeployment, RollingDeployment, RecreateDeployment {}

record BlueGreenDeployment(String version, String activeSlot, String inactiveSlot, int trafficPercentage) implements DeploymentStrategy {}
record CanaryDeployment(String version, int trafficPercentage, Map<String, String> targetCriteria) implements DeploymentStrategy {}
record RollingDeployment(String version, int batchSize, int totalInstances, Duration batchInterval) implements DeploymentStrategy {}
record RecreateDeployment(String version, Duration downtime) implements DeploymentStrategy {}
