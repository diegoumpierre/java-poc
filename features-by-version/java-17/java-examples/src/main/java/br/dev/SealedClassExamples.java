package br.dev;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Real-world examples of Sealed Classes in Java 17+
 * Demonstrates practical use cases in enterprise applications
 */
public class SealedClassExamples {

    public static void main(String[] args) {
        // API Response handling
        demonstrateApiResponses();

        // Payment processing
        demonstratePaymentProcessing();

        // User role management
        demonstrateUserRoles();

        // Notification system
        demonstrateNotificationSystem();

        // Blog post workflow
        demonstrateBlogPostWorkflow();

        // Database operations
        demonstrateDatabaseOperations();

        // Event sourcing
        demonstrateEventSourcing();

        // Order management
        demonstrateOrderManagement();

        // File processing
        demonstrateFileProcessing();

        // Configuration management
        demonstrateConfigurationManagement();
    }

    private static void demonstrateApiResponses() {
        System.out.println("=== API Response Examples ===");

        ApiResponse<String> success = new Success<>("Data retrieved successfully", "Hello World");
        ApiResponse<String> error = new Error<>("Database connection failed", 500);
        ApiResponse<String> loading = new Loading<>("Fetching data...");

        processApiResponse(success);
        processApiResponse(error);
        processApiResponse(loading);
        System.out.println();
    }

    private static void processApiResponse(ApiResponse<String> response) {
        switch (response) {
            case Success<String> s -> System.out.println("✓ Success: " + s.data() + " - " + s.message());
            case Error<String> e -> System.out.println("✗ Error [" + e.code() + "]: " + e.message());
            case Loading<String> l -> System.out.println("⏳ Loading: " + l.message());
        }
    }

    private static void demonstratePaymentProcessing() {
        System.out.println("=== Payment Processing Examples ===");

        Payment creditCard = new CreditCardPayment("1234-5678-9012-3456", "123", 100.0);
        Payment paypal = new PayPalPayment("user@example.com", 75.0);
        Payment bankTransfer = new BankTransferPayment("123456789", "BANK001", 200.0);

        processPayment(creditCard);
        processPayment(paypal);
        processPayment(bankTransfer);
        System.out.println();
    }

    private static void processPayment(Payment payment) {
        String result = switch (payment) {
            case CreditCardPayment cc -> "Processing credit card payment of $" + cc.amount() +
                                       " using card ending in " + cc.cardNumber().substring(cc.cardNumber().length() - 4);
            case PayPalPayment pp -> "Processing PayPal payment of $" + pp.amount() + " for " + pp.email();
            case BankTransferPayment bt -> "Processing bank transfer of $" + bt.amount() +
                                         " from account " + bt.accountNumber();
        };
        System.out.println(result);
    }

    private static void demonstrateUserRoles() {
        System.out.println("=== User Role Management Examples ===");

        UserRole admin = new Admin("admin123", List.of("CREATE", "READ", "UPDATE", "DELETE", "MANAGE_USERS"));
        UserRole editor = new Editor("editor456", List.of("blog", "news", "reviews"));
        UserRole viewer = new Viewer("viewer789", LocalDateTime.now().plusDays(30));

        checkPermissions(admin, "DELETE");
        checkPermissions(editor, "MANAGE_USERS");
        checkPermissions(viewer, "READ");
        System.out.println();
    }

    private static void checkPermissions(UserRole role, String action) {
        boolean hasPermission = switch (role) {
            case Admin a -> a.permissions().contains(action);
            case Editor e -> action.equals("CREATE") || action.equals("READ") || action.equals("UPDATE");
            case Viewer v -> action.equals("READ") && v.accessExpiresAt().isAfter(LocalDateTime.now());
        };

        System.out.println(role.getClass().getSimpleName() + " " + role.userId() +
                         " " + (hasPermission ? "CAN" : "CANNOT") + " perform " + action);
    }

    private static void demonstrateNotificationSystem() {
        System.out.println("=== Notification System Examples ===");

        Notification email = new EmailNotification("user@example.com", "Welcome!", "Thanks for joining us!");
        Notification sms = new SmsNotification("+1234567890", "Your verification code is 123456");
        Notification push = new PushNotification("device123", "New message", Map.of("badge", "5"));

        sendNotification(email);
        sendNotification(sms);
        sendNotification(push);
        System.out.println();
    }

    private static void sendNotification(Notification notification) {
        String result = switch (notification) {
            case EmailNotification e -> "📧 Sending email to " + e.recipient() + ": " + e.subject();
            case SmsNotification s -> "📱 Sending SMS to " + s.phoneNumber() + ": " + s.message();
            case PushNotification p -> "🔔 Sending push to device " + p.deviceId() + ": " + p.title();
        };
        System.out.println(result);
    }

    private static void demonstrateBlogPostWorkflow() {
        System.out.println("=== Blog Post Workflow Examples ===");

        PostStatus draft = new Draft("author123", LocalDateTime.now());
        PostStatus review = new UnderReview("author123", "editor456", LocalDateTime.now().minusHours(2));
        PostStatus published = new Published("post789", LocalDateTime.now().minusDays(1), 1250);
        PostStatus archived = new Archived("old-post", LocalDateTime.now().minusYears(1), "Content outdated");

        handlePostStatus(draft);
        handlePostStatus(review);
        handlePostStatus(published);
        handlePostStatus(archived);
    }

    private static void handlePostStatus(PostStatus status) {
        String info = switch (status) {
            case Draft d -> "📝 Draft by " + d.authorId() + " created at " + d.createdAt();
            case UnderReview ur -> "👀 Under review by " + ur.reviewerId() + " since " + ur.submittedAt();
            case Published p -> "🌐 Published post " + p.postId() + " with " + p.viewCount() + " views";
            case Archived a -> "📁 Archived post " + a.postId() + " - Reason: " + a.reason();
        };
        System.out.println(info);
    }

    private static void demonstrateDatabaseOperations() {
        System.out.println("=== Database Operations Examples ===");

        DatabaseResult<String> success = new QuerySuccess<>(List.of("User1", "User2", "User3"), 15L);
        DatabaseResult<String> connectionError = new ConnectionError<>("Connection timeout after 30s");
        DatabaseResult<String> syntaxError = new SqlError<>("Syntax error near 'SELET'", "SELECT * FROM users");
        DatabaseResult<String> noData = new NoDataFound<>("users", "active = true");

        processDatabaseResult(success);
        processDatabaseResult(connectionError);
        processDatabaseResult(syntaxError);
        processDatabaseResult(noData);
        System.out.println();
    }

    private static void processDatabaseResult(DatabaseResult<String> result) {
        String output = switch (result) {
            case QuerySuccess<String> qs -> "✓ Found " + qs.data().size() + " records in " + qs.executionTimeMs() + "ms";
            case ConnectionError<String> ce -> "❌ Connection failed: " + ce.message();
            case SqlError<String> se -> "⚠️ SQL Error in query '" + se.query() + "': " + se.message();
            case NoDataFound<String> nd -> "ℹ️ No data found in table '" + nd.tableName() + "' for condition: " + nd.condition();
        };
        System.out.println(output);
    }

    private static void demonstrateEventSourcing() {
        System.out.println("=== Event Sourcing Examples ===");

        DomainEvent userCreated = new UserCreated("user123", "john@example.com", LocalDateTime.now());
        DomainEvent userUpdated = new UserUpdated("user123", "name", "John Doe", LocalDateTime.now());
        DomainEvent userDeleted = new UserDeleted("user123", "GDPR_REQUEST", LocalDateTime.now());
        DomainEvent postPublished = new PostPublished("post456", "user123", "My First Post", LocalDateTime.now());

        processEvent(userCreated);
        processEvent(userUpdated);
        processEvent(userDeleted);
        processEvent(postPublished);
        System.out.println();
    }

    private static void processEvent(DomainEvent event) {
        String description = switch (event) {
            case UserCreated uc -> "👤 User created: " + uc.email() + " at " + uc.timestamp();
            case UserUpdated uu -> "✏️ User " + uu.userId() + " updated field '" + uu.field() + "' to '" + uu.newValue() + "'";
            case UserDeleted ud -> "🗑️ User " + ud.userId() + " deleted (reason: " + ud.reason() + ")";
            case PostPublished pp -> "📝 Post '" + pp.title() + "' published by user " + pp.authorId();
        };
        System.out.println(description);
    }

    private static void demonstrateOrderManagement() {
        System.out.println("=== Order Management Examples ===");

        OrderStatus pending = new PendingOrder("ORD001", LocalDateTime.now(), 299.99);
        OrderStatus processing = new ProcessingOrder("ORD002", "WH001", LocalDateTime.now().minusHours(1));
        OrderStatus shipped = new ShippedOrder("ORD003", "TRK123456", "FedEx", LocalDateTime.now().minusDays(1));
        OrderStatus delivered = new DeliveredOrder("ORD004", LocalDateTime.now().minusDays(3), "John Doe");
        OrderStatus cancelled = new CancelledOrder("ORD005", LocalDateTime.now().minusHours(2), "Customer request");

        handleOrderStatus(pending);
        handleOrderStatus(processing);
        handleOrderStatus(shipped);
        handleOrderStatus(delivered);
        handleOrderStatus(cancelled);
        System.out.println();
    }

    private static void handleOrderStatus(OrderStatus status) {
        String info = switch (status) {
            case PendingOrder po -> "⏳ Order " + po.orderId() + " pending payment ($" + po.amount() + ")";
            case ProcessingOrder pr -> "🏭 Order " + pr.orderId() + " being processed at warehouse " + pr.warehouseId();
            case ShippedOrder so -> "🚚 Order " + so.orderId() + " shipped via " + so.carrier() + " (tracking: " + so.trackingNumber() + ")";
            case DeliveredOrder do_ -> "✅ Order " + do_.orderId() + " delivered to " + do_.recipientName();
            case CancelledOrder co -> "❌ Order " + co.orderId() + " cancelled: " + co.reason();
        };
        System.out.println(info);
    }

    private static void demonstrateFileProcessing() {
        System.out.println("=== File Processing Examples ===");

        FileOperationResult csvResult = new FileSuccess("data.csv", 1024L, "CSV", 150);
        FileOperationResult pdfResult = new FileSuccess("report.pdf", 2048L, "PDF", 1);
        FileOperationResult notFoundResult = new FileNotFound("missing.txt", "/path/to/missing.txt");
        FileOperationResult corruptResult = new FileCorrupted("damaged.zip", "Invalid ZIP header");
        FileOperationResult permissionResult = new FilePermissionDenied("secret.txt", "READ");

        processFileResult(csvResult);
        processFileResult(pdfResult);
        processFileResult(notFoundResult);
        processFileResult(corruptResult);
        processFileResult(permissionResult);
        System.out.println();
    }

    private static void processFileResult(FileOperationResult result) {
        String output = switch (result) {
            case FileSuccess fs -> "✅ Processed " + fs.fileName() + " (" + fs.sizeBytes() + " bytes, " +
                                 fs.recordCount() + " " + fs.type() + " records)";
            case FileNotFound fnf -> "❌ File not found: " + fnf.fileName() + " at " + fnf.path();
            case FileCorrupted fc -> "⚠️ File corrupted: " + fc.fileName() + " - " + fc.error();
            case FilePermissionDenied fpd -> "🔒 Permission denied: Cannot " + fpd.operation() + " file " + fpd.fileName();
        };
        System.out.println(output);
    }

    private static void demonstrateConfigurationManagement() {
        System.out.println("=== Configuration Management Examples ===");

        ConfigValue dbUrl = new StringConfig("database.url", "jdbc:postgresql://localhost:5432/mydb");
        ConfigValue maxConnections = new IntegerConfig("database.max-connections", 20);
        ConfigValue enableCache = new BooleanConfig("cache.enabled", true);
        ConfigValue features = new ListConfig("features.enabled", List.of("auth", "notifications", "analytics"));

        applyConfiguration(dbUrl);
        applyConfiguration(maxConnections);
        applyConfiguration(enableCache);
        applyConfiguration(features);
        System.out.println();
    }

    private static void applyConfiguration(ConfigValue config) {
        String result = switch (config) {
            case StringConfig sc -> "📝 String config '" + sc.key() + "' = '" + sc.value() + "'";
            case IntegerConfig ic -> "🔢 Integer config '" + ic.key() + "' = " + ic.value();
            case BooleanConfig bc -> "☑️ Boolean config '" + bc.key() + "' = " + bc.value();
            case ListConfig lc -> "📋 List config '" + lc.key() + "' = " + lc.values();
        };
        System.out.println(result);
    }
}

// =============== API Response Pattern ===============
sealed interface ApiResponse<T> permits Success, Error, Loading {}

record Success<T>(String message, T data) implements ApiResponse<T> {}
record Error<T>(String message, int code) implements ApiResponse<T> {}
record Loading<T>(String message) implements ApiResponse<T> {}

// =============== Payment Processing Pattern ===============
sealed abstract class Payment permits CreditCardPayment, PayPalPayment, BankTransferPayment {
    public abstract double amount();
}

final class CreditCardPayment extends Payment {
    private final String cardNumber;
    private final String cvv;
    private final double amount;

    public CreditCardPayment(String cardNumber, String cvv, double amount) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.amount = amount;
    }

    public String cardNumber() { return cardNumber; }
    public String cvv() { return cvv; }
    public double amount() { return amount; }
}

final class PayPalPayment extends Payment {
    private final String email;
    private final double amount;

    public PayPalPayment(String email, double amount) {
        this.email = email;
        this.amount = amount;
    }

    public String email() { return email; }
    public double amount() { return amount; }
}

final class BankTransferPayment extends Payment {
    private final String accountNumber;
    private final String bankCode;
    private final double amount;

    public BankTransferPayment(String accountNumber, String bankCode, double amount) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
        this.amount = amount;
    }

    public String accountNumber() { return accountNumber; }
    public String bankCode() { return bankCode; }
    public double amount() { return amount; }
}

// =============== User Role Management Pattern ===============
sealed interface UserRole permits Admin, Editor, Viewer {
    String userId();
}

record Admin(String userId, List<String> permissions) implements UserRole {}
record Editor(String userId, List<String> categories) implements UserRole {}
record Viewer(String userId, LocalDateTime accessExpiresAt) implements UserRole {}

// =============== Notification System Pattern ===============
sealed interface Notification permits EmailNotification, SmsNotification, PushNotification {}

record EmailNotification(String recipient, String subject, String body) implements Notification {}
record SmsNotification(String phoneNumber, String message) implements Notification {}
record PushNotification(String deviceId, String title, Map<String, String> payload) implements Notification {}

// =============== Blog Post Workflow Pattern ===============
sealed interface PostStatus permits Draft, UnderReview, Published, Archived {}

record Draft(String authorId, LocalDateTime createdAt) implements PostStatus {}
record UnderReview(String authorId, String reviewerId, LocalDateTime submittedAt) implements PostStatus {}
record Published(String postId, LocalDateTime publishedAt, int viewCount) implements PostStatus {}
record Archived(String postId, LocalDateTime archivedAt, String reason) implements PostStatus {}

// =============== Database Operations Pattern ===============
sealed interface DatabaseResult<T> permits QuerySuccess, ConnectionError, SqlError, NoDataFound {}

record QuerySuccess<T>(List<T> data, Long executionTimeMs) implements DatabaseResult<T> {}
record ConnectionError<T>(String message) implements DatabaseResult<T> {}
record SqlError<T>(String message, String query) implements DatabaseResult<T> {}
record NoDataFound<T>(String tableName, String condition) implements DatabaseResult<T> {}

// =============== Event Sourcing Pattern ===============
sealed interface DomainEvent permits UserCreated, UserUpdated, UserDeleted, PostPublished {
    LocalDateTime timestamp();
}

record UserCreated(String userId, String email, LocalDateTime timestamp) implements DomainEvent {}
record UserUpdated(String userId, String field, String newValue, LocalDateTime timestamp) implements DomainEvent {}
record UserDeleted(String userId, String reason, LocalDateTime timestamp) implements DomainEvent {}
record PostPublished(String postId, String authorId, String title, LocalDateTime timestamp) implements DomainEvent {}

// =============== Order Management Pattern ===============
sealed interface OrderStatus permits PendingOrder, ProcessingOrder, ShippedOrder, DeliveredOrder, CancelledOrder {}

record PendingOrder(String orderId, LocalDateTime createdAt, double amount) implements OrderStatus {}
record ProcessingOrder(String orderId, String warehouseId, LocalDateTime startedAt) implements OrderStatus {}
record ShippedOrder(String orderId, String trackingNumber, String carrier, LocalDateTime shippedAt) implements OrderStatus {}
record DeliveredOrder(String orderId, LocalDateTime deliveredAt, String recipientName) implements OrderStatus {}
record CancelledOrder(String orderId, LocalDateTime cancelledAt, String reason) implements OrderStatus {}

// =============== File Processing Pattern ===============
sealed interface FileOperationResult permits FileSuccess, FileNotFound, FileCorrupted, FilePermissionDenied {}

record FileSuccess(String fileName, long sizeBytes, String type, int recordCount) implements FileOperationResult {}
record FileNotFound(String fileName, String path) implements FileOperationResult {}
record FileCorrupted(String fileName, String error) implements FileOperationResult {}
record FilePermissionDenied(String fileName, String operation) implements FileOperationResult {}

// =============== Configuration Management Pattern ===============
sealed interface ConfigValue permits StringConfig, IntegerConfig, BooleanConfig, ListConfig {
    String key();
}

record StringConfig(String key, String value) implements ConfigValue {}
record IntegerConfig(String key, int value) implements ConfigValue {}
record BooleanConfig(String key, boolean value) implements ConfigValue {}
record ListConfig(String key, List<String> values) implements ConfigValue {}
