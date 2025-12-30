# API Gateway Routes Reference

**Gateway URL:** `http://localhost:8080`

## Service Overview

| Service        | Backend Port | Gateway Path     | Status    |
| -------------- | ------------ | ---------------- | --------- |
| Auth Service   | 8091         | `/api/auth/**`   | ✅ Active |
| Kanban Service | 8081         | `/api/kanban/**` | ✅ Active |

---

## Authentication Flow

### Public Routes (No JWT Required)

**Auth Service - Registration & Login:**

```bash
POST http://localhost:8080/api/auth/register
POST http://localhost:8080/api/auth/login
POST http://localhost:8080/api/auth/forgot-password
POST http://localhost:8080/api/auth/verify-code
POST http://localhost:8080/api/auth/reset-password
```

**Example Login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "changeme123"
  }'
```

Response:

```json
{
  "success": true,
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "refreshExpiresIn": 604800
}
```

---

## Protected Routes (JWT Required)

All protected routes require the `Authorization` header with Bearer token:

```bash
Authorization: Bearer <your-jwt-token>
```

### Auth Service

**User Profile:**

```bash
GET    http://localhost:8080/api/auth/me
POST   http://localhost:8080/api/auth/logout
```

**Profile Management:**

```bash
GET    http://localhost:8080/api/profile
PUT    http://localhost:8080/api/profile
POST   http://localhost:8080/api/profile/avatar
DELETE http://localhost:8080/api/profile/avatar
```

**Settings:**

```bash
GET    http://localhost:8080/api/settings
PUT    http://localhost:8080/api/settings
```

**User Management:**

```bash
GET    http://localhost:8080/api/users
GET    http://localhost:8080/api/users/{id}
DELETE http://localhost:8080/api/users/{id}
DELETE http://localhost:8080/api/users/batch
```

---

### Kanban Service

**Boards:**

```bash
GET    http://localhost:8080/api/kanban/boards
GET    http://localhost:8080/api/kanban/boards/{id}
POST   http://localhost:8080/api/kanban/boards
PUT    http://localhost:8080/api/kanban/boards/{id}
DELETE http://localhost:8080/api/kanban/boards/{id}
PUT    http://localhost:8080/api/kanban/boards/{id}/state
```

**Lists:**

```bash
POST   http://localhost:8080/api/kanban/boards/{boardId}/lists
PUT    http://localhost:8080/api/kanban/boards/{boardId}/lists/{listId}
DELETE http://localhost:8080/api/kanban/boards/{boardId}/lists/{listId}
```

**Cards:**

```bash
POST   http://localhost:8080/api/kanban/boards/{boardId}/lists/{listId}/cards
PUT    http://localhost:8080/api/kanban/boards/{boardId}/lists/{listId}/cards/{cardId}
DELETE http://localhost:8080/api/kanban/boards/{boardId}/lists/{listId}/cards/{cardId}
POST   http://localhost:8080/api/kanban/boards/{boardId}/lists/{srcListId}/cards/{cardId}/move
```

**Attachments:**

```bash
GET    http://localhost:8080/api/kanban/cards/{cardId}/attachments
POST   http://localhost:8080/api/kanban/cards/{cardId}/attachments
GET    http://localhost:8080/api/kanban/cards/{cardId}/attachments/{attachmentId}/download
DELETE http://localhost:8080/api/kanban/cards/{cardId}/attachments/{attachmentId}
```

---

#

---

## Health Checks (Public)

All services expose health endpoints:

```bash
GET http://localhost:8080/api/auth/actuator/health
GET http://localhost:8080/api/kanban/actuator/health
GET http://localhost:8080/api/project/actuator/health
```

---

## Swagger/OpenAPI Documentation (Public)

Access interactive API documentation:

```bash
# Auth Service
http://localhost:8080/api/auth/swagger-ui.html

# Kanban Service
http://localhost:8080/api/kanban/swagger-ui.html

```

---

## Gateway Features

### 1. JWT Authentication

- Validates JWT tokens
- Adds `X-User-Id`, `X-User-Email`, `X-User-Role` headers
- Automatic token extraction from `Authorization: Bearer <token>`

### 2. Rate Limiting

- 10 requests per second per user
- Burst capacity: 20 requests
- Redis-based (requires Redis at `127.0.0.1:6379`)

### 3. Circuit Breaker

- Automatic fallback on service failure
- Retry policy: 3 attempts for GET requests
- Fallback endpoint: `/fallback`

### 4. CORS Configuration

- Allowed origins: `localhost:3000`, `localhost:3001`, `localhost:4200`
- Allowed methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
- Credentials: Enabled
- Max age: 3600 seconds

---

## Error Responses

### 401 Unauthorized

```json
{
  "timestamp": "2026-01-24T14:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "path": "/api/kanban/boards"
}
```

### 404 Not Found

```json
{
  "timestamp": "2026-01-24T14:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found with id: abc-123",
  "path": "/api/kanban/boards/abc-123"
}
```

### 429 Too Many Requests

```json
{
  "timestamp": "2026-01-24T14:30:00Z",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again later.",
  "path": "/api/products"
}
```

---

## Testing with cURL

**Complete workflow example:**

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"changeme123"}' \
  | jq -r '.accessToken')

# 2. Get user profile
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"

# 3. Get kanban boards
curl http://localhost:8080/api/kanban/boards \
  -H "Authorization: Bearer $TOKEN"

# 4. Get products
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"

# 5. Get products
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"
```

---

## Infrastructure Requirements

**Required Services:**

- Redis: `127.0.0.1:6379` (for rate limiting)

**Backend Services:**

- Auth Service: `http://localhost:8091`
- Kanban Service: `http://localhost:8081`

**All services must use the same JWT secret for token validation.**

---

