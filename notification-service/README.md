# 101 Notification Service

Centralized email notification service for the 101 Softwares platform. Handles email queuing, sending, retries, and scheduling.

## Overview

| Property | Value |
|----------|-------|
| **Port** | 8086 |
| **Database** | 101_softwares (table: EMAIL_HISTORY) |
| **Message Queue** | Kafka |
| **Multi-Tenant** | Yes |

## Features

- **Email Queuing** - Async email processing via Kafka
- **Template Engine** - Thymeleaf-based email templates
- **Retry Mechanism** - Exponential backoff for failed emails
- **Scheduled Emails** - Future-dated email delivery
- **Rate Limiting** - Configurable per-user limits
- **Email History** - Full tracking and audit trail
- **Multi-Tenant** - Tenant isolation via TenantContext

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Runtime |
| Spring Boot | 3.4.1 | Framework |
| Spring Data JDBC | 3.4.x | Data Access |
| Spring Mail | 3.4.x | SMTP Client |
| Spring Kafka | 3.4.x | Message Queue |
| Spring Thymeleaf | 3.4.x | Email Templates |
| MySQL Connector | 8.x | Database Driver |
| Liquibase | 4.x | Migrations |
| Lombok | 1.18.x | Boilerplate Reduction |
| SpringDoc OpenAPI | 2.8.0 | API Documentation |

## API Endpoints

### Public
```
GET  /api/notification/templates        List available templates
GET  /api/notification/test-connection  Test SMTP connection
```

### Authenticated
```
POST /api/notification/send             Queue email for sending
GET  /api/notification/history          Get email history
```

## Email Templates

| Template | File | Variables |
|----------|------|-----------|
| PASSWORD_RESET | password-reset.html | userName, resetToken, expirationMinutes |
| WELCOME | welcome.html | userName, loginLink |
| VERIFICATION | verification.html | userName, verificationCode, expirationMinutes |
| SIMPLE | simple.html | userName, message |

## Email Status Flow

```
PENDING → QUEUED → SENDING → SENT
                      ↓
                   FAILED → (retry) → QUEUED
                      ↓
                   DEAD (max retries)
```

## Database Schema

```sql
CREATE TABLE EMAIL_HISTORY (
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    MESSAGE_ID      VARCHAR(36),
    USER_ID         BIGINT,
    TENANT_ID       VARCHAR(36),
    RECIPIENT       VARCHAR(255) NOT NULL,
    SUBJECT         VARCHAR(500),
    TEMPLATE        VARCHAR(50),
    VARIABLES       TEXT,
    STATUS          VARCHAR(20) NOT NULL,
    RETRY_COUNT     INT DEFAULT 0,
    ERROR_MESSAGE   TEXT,
    SCHEDULED_AT    TIMESTAMP,
    SENT_AT         TIMESTAMP,
    CREATED_AT      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT      TIMESTAMP
);
```

## Configuration

```yaml
server:
  port: 8086

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/101_softwares
    username: 101_user
    password: ${DB_PASSWORD}

  kafka:
    bootstrap-servers: 127.0.0.1:9092
    consumer:
      group-id: notification-consumer

  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

app:
  notification:
    enabled: true
    from-address: noreply@example.com
    from-name: Softwares101
    base-url: http://localhost:3001

  rate-limit:
    max-per-window: 10
    window-minutes: 60
```

## Multi-Tenant Support

This service implements full multi-tenant isolation:

- **TenantFilter** - Extracts `X-Tenant-Id` from request headers
- **TenantContext** - ThreadLocal storage for current tenant
- Email history is tracked per tenant

## Quick Start

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# Test
mvn test

# Health Check
curl http://localhost:8086/actuator/health
```

## Testing Examples

```bash
# List templates
curl http://localhost:8086/api/notification/templates

# Test SMTP connection
curl http://localhost:8086/api/notification/test-connection

# Send email
curl -X POST http://localhost:8086/api/notification/send \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -H "X-Tenant-Id: your-tenant-uuid" \
  -d '{
    "to": "user@example.com",
    "template": "VERIFICATION",
    "variables": {
      "userName": "John",
      "verificationCode": "123456",
      "expirationMinutes": 15
    }
  }'

# Get email history
curl http://localhost:8086/api/notification/history \
  -H "X-User-Id: 1" \
  -H "X-Tenant-Id: your-tenant-uuid"
```

## Dependencies

- **MySQL** - Email history storage
- **Kafka** - Async message queue
- **SMTP Server** - Email delivery

## API Documentation

- Swagger UI: http://localhost:8086/swagger-ui.html
- OpenAPI JSON: http://localhost:8086/api-docs

---

