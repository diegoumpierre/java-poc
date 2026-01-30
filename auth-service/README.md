# 101 Auth Service

Authentication and user management microservice for the 101 Softwares platform.

## Overview

| Property | Value |
|----------|-------|
| **Port** | 8091 |
| **Database** | auth_db |
| **Storage** | MinIO (auth-files bucket) |
| **Multi-Tenant** | No (manages users/tenants) |

## Features

- **Authentication** - JWT-based login and registration
- **User Management** - CRUD operations for users
- **Profile Management** - User profile and settings
- **Avatar Upload** - Profile pictures via MinIO
- **Password Reset** - Email-based password recovery
- **Role Management** - User roles and permissions

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Runtime |
| Spring Boot | 3.4.1 | Framework |
| Spring Data JDBC | 3.4.x | Data Access |
| Spring Security | 6.x | Security (minimal) |
| JJWT | 0.12.5 | JWT Token Generation |
| MinIO Client | 8.5.7 | Avatar Storage |
| MySQL Connector | 8.x | Database Driver |
| Liquibase | 4.x | Migrations |
| Lombok | 1.18.x | Boilerplate Reduction |
| SpringDoc OpenAPI | 2.8.0 | API Documentation |

## API Endpoints

### Public (No Auth)
```
POST /api/auth/register           Register new user
POST /api/auth/login              User login (returns JWT)
POST /api/auth/forgot-password    Request password reset
POST /api/auth/verify-code        Verify reset code
POST /api/auth/reset-password     Reset password
```

### Authenticated (X-User-Id Header)
```
GET    /api/auth/me               Get current user
POST   /api/auth/logout           Logout user
GET    /api/profile               Get user profile
PUT    /api/profile               Update profile
POST   /api/profile/avatar        Upload avatar
DELETE /api/profile/avatar        Delete avatar
GET    /api/settings              Get user settings
PUT    /api/settings              Update settings
GET    /api/users                 List all users
GET    /api/users/{id}            Get user by ID
DELETE /api/users/{id}            Delete user
DELETE /api/users/batch           Batch delete users
```

## Configuration

```yaml
server:
  port: 8091

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/auth_db
    username: 101_user
    password: ${DB_PASSWORD}

app:
  minio:
    endpoint: http://127.0.0.1:9002
    accessKey: ${MINIO_ACCESS_KEY}
    secretKey: ${MINIO_SECRET_KEY}
    bucket: auth-files

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000  # 24 hours
```

## Default Admin

On startup, the service creates a default admin user:
- **Email:** `admin@example.com`
- **Password:** `changeme123`

## Quick Start

```bash
# Build shared-libraries first
cd ../shared-libraries && mvn clean install -DskipTests

# Build
cd ../auth-service
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# Test
mvn test

# Health Check
curl http://localhost:8091/actuator/health
```

## Testing Examples

```bash
# Register
curl -X POST http://localhost:8091/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe"
  }'

# Login
curl -X POST http://localhost:8091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "changeme123"
  }'

# Get profile (simulating Gateway header)
curl http://localhost:8091/api/profile \
  -H "X-User-Id: user-uuid"

# Upload avatar
curl -X POST http://localhost:8091/api/profile/avatar \
  -H "X-User-Id: user-uuid" \
  -F "file=@avatar.png"
```

## Security Note

Security is disabled in this service (`app.security.enabled=false`). Authentication is handled by the API Gateway, which validates JWT tokens and adds `X-User-Id` headers.

## Dependencies

- **MySQL** - User data storage
- **MinIO** - Avatar storage
- **shared-libraries** - Shared domain models

## API Documentation

- Swagger UI: http://localhost:8091/swagger-ui.html
- OpenAPI JSON: http://localhost:8091/api-docs

---

