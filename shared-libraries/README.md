# 101 Shared Libraries

Shared components for 101 Softwares microservices.

## Components

### Multi-Tenancy (`com.poc.shared.tenant`)

| Class | Description |
|-------|-------------|
| `TenantContext` | Thread-local storage for tenant/user/membership IDs |
| `TenantFilter` | Extracts tenant context from HTTP headers (X-Tenant-Id, X-User-Id, X-Membership-Id) |
| `TenantAware` | Annotation to mark methods that require tenant context |
| `TenantAspect` | AOP aspect that validates tenant context for @TenantAware methods |

### JDBC Converters (`com.poc.shared.jdbc`)

| Class | Description |
|-------|-------------|
| `UuidConverters` | UUID <-> String conversion for MySQL CHAR(36) |
| `BooleanConverters` | Boolean <-> Integer conversion for MySQL TINYINT(1) |
| `JdbcConvertersConfig` | Helper to get all standard converters |

### Exceptions (`com.poc.shared.exception`)

| Class | Description |
|-------|-------------|
| `TenantRequiredException` | Thrown when tenant context is required but not present (HTTP 403) |

## Usage

### 1. Add Dependency

```xml
<dependency>
    <groupId>com.poc</groupId>
    <artifactId>shared-libraries</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Enable Component Scanning

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.poc.yourservice",
    "com.poc.shared"
})
public class YourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourServiceApplication.class, args);
    }
}
```

### 3. Configure JDBC Converters

```java
@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(JdbcConvertersConfig.getStandardConverters());
    }
}
```

### 4. Use TenantAware Annotation

```java
@Service
@TenantAware  // All methods require tenant context
public class MyService {

    public void doSomething() {
        UUID tenantId = TenantContext.getCurrentTenant();
        // Use tenantId in queries
    }

    @TenantAware(required = false)  // Optional tenant context
    public void doSomethingOptional() {
        if (TenantContext.hasTenantContext()) {
            // Tenant-specific logic
        }
    }
}
```

## Building

```bash
cd shared-libraries
mvn clean install
```

## Version History

- **1.0.0** - Initial release with tenant context and JDBC converters
