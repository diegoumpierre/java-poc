# 101 API Gateway

Spring Cloud Gateway for the 101 Softwares microservices platform.

## Overview

| Property | Value |
|----------|-------|
| **Port** | 8080 |
| **Java** | 21 |
| **Framework** | Spring Cloud Gateway (Reactive) |
| **Cache** | Redis |
| **Status** | Optional (development) |

## Features

- **JWT Authentication** - Validates tokens and injects user headers
- **Rate Limiting** - Redis-backed token bucket algorithm
- **Circuit Breakers** - Resilience4J fault tolerance
- **Load Balancing** - Spring Cloud LoadBalancer
- **CORS Configuration** - Cross-Origin Resource Sharing
- **Monitoring** - Actuator endpoints with Prometheus metrics

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Runtime |
| Spring Cloud Gateway | 2024.0.0 | API Gateway |
| Spring Cloud LoadBalancer | 4.x | Load Balancing |
| Spring Cloud Circuit Breaker | 4.x | Resilience4J |
| Spring Data Redis | 3.4.x | Rate Limiting |
| JJWT | 0.12.5 | JWT Validation |
| Lombok | 1.18.x | Boilerplate Reduction |

## Architecture

```
Client → Gateway (8080) → JWT Validation → Rate Limit → Backend Service
                              ↓
                    X-User-Id, X-User-Email, X-User-Role, X-Tenant-Id
```

## Route Configuration

### Public Routes (No Authentication)
```
POST /api/auth/login
POST /api/auth/register
GET  /api/*/actuator/health
```

### Protected Routes (JWT Required)
```
/api/auth/**      → Auth Service (8091)
/api/kanban/**    → Kanban Service (8081)
/api/products/**  → Product Service (8082)
/api/projects/**  → Project Service (8083)
/api/storage/**   → Storage Service (8084)
/api/notification/** → Notification Service (8086)
/api/billing/**   → Billing Service (8087)
```

## JWT Header Injection

After validating JWT tokens, the gateway adds these headers to downstream requests:

| Header | Description |
|--------|-------------|
| X-User-Id | User UUID from token |
| X-User-Email | User email from token |
| X-User-Role | User role (ADMIN, USER) |
| X-Tenant-Id | Tenant UUID from token |

## Rate Limiting

| Setting | Value |
|---------|-------|
| Replenish Rate | 10 requests/second |
| Burst Capacity | 20 requests |
| Backend | Redis |

## Configuration

```yaml
server:
  port: 8080

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000  # 24 hours

spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379

  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: http://localhost:8091
          predicates:
            - Path=/api/auth/**
          filters:
            - JwtAuthenticationFilter
```

## Quick Start

```bash
# Start Redis (required)
docker run -d -p 6379:6379 redis:alpine

# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# Health Check
curl http://localhost:8080/actuator/health
```

## Testing Examples

```bash
# Login through gateway
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "changeme123"
  }'

# Use token for protected routes
TOKEN="eyJhbGc..."
curl http://localhost:8080/api/kanban/boards \
  -H "Authorization: Bearer $TOKEN"

# Check rate limiting
for i in {1..25}; do
  curl -s -o /dev/null -w "%{http_code}" \
    http://localhost:8080/api/kanban/boards \
    -H "Authorization: Bearer $TOKEN"
  echo " - Request $i"
done
```

## Circuit Breakers

Each service has its own circuit breaker:
- `authCircuitBreaker`
- `kanbanCircuitBreaker`
- `productCircuitBreaker`
- `projectCircuitBreaker`

**Behavior:**
- Opens after 5 consecutive failures
- Half-open state after 60 seconds
- Closes after 3 consecutive successes

## Monitoring

```bash
# Health status
curl http://localhost:8080/actuator/health

# Gateway routes
curl http://localhost:8080/actuator/gateway/routes

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

## Dependencies

- **Redis** - Rate limiting and caching
- **Backend Services** - All microservices

## Troubleshooting

### Redis Connection Issues
```bash
# Check Redis
docker ps | grep redis
redis-cli ping  # Should return PONG
```

### JWT Validation Failures
- Verify JWT secret matches auth-service
- Check token expiration
- Format: `Authorization: Bearer <token>`

---

