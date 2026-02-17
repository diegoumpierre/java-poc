# 101 Kanban Service

Kanban board management microservice for the 101 Softwares platform.

## Overview

| Property | Value |
|----------|-------|
| **Port** | 8081 |
| **Database** | kanban_db |
| **Storage** | MinIO (kanban-files bucket) |
| **Multi-Tenant** | Yes |

## Features

- **Board Management** - Create, update, delete Kanban boards
- **List Management** - Manage lists within boards
- **Card Management** - Full CRUD for cards with drag-and-drop
- **Sequential Numbering** - Cards get sequential numbers (e.g., PROJ-0001)
- **Attachments** - File uploads via MinIO
- **Subtasks** - Task breakdown with progress tracking
- **Comments** - Card activity and history
- **Multi-Tenant** - Full tenant isolation via TenantContext

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Runtime |
| Spring Boot | 3.4.1 | Framework |
| Spring Data JDBC | 3.4.x | Data Access (Aggregate Pattern) |
| Spring Security | 6.x | Security |
| Spring AOP | 6.x | Multi-Tenant Aspect |
| Spring Cloud OpenFeign | 4.x | Storage Service Client |
| MySQL Connector | 8.x | Database Driver |
| Liquibase | 4.x | Migrations |
| JJWT | 0.12.5 | JWT Handling |
| Lombok | 1.18.x | Boilerplate Reduction |
| SpringDoc OpenAPI | 2.8.0 | API Documentation |

## API Endpoints

### Boards
```
GET    /api/kanban/boards                     List boards
GET    /api/kanban/boards/{id}                Get board (with lists/cards)
POST   /api/kanban/boards                     Create board
PUT    /api/kanban/boards/{id}                Update board
DELETE /api/kanban/boards/{id}                Delete board
PUT    /api/kanban/boards/{id}/state          Bulk update (drag-drop)
```

### Lists
```
POST   /api/kanban/boards/{boardId}/lists              Add list
PUT    /api/kanban/boards/{boardId}/lists/{listId}     Update list
DELETE /api/kanban/boards/{boardId}/lists/{listId}     Delete list
```

### Cards
```
POST   /api/kanban/boards/{boardId}/lists/{listId}/cards              Add card
PUT    /api/kanban/boards/{boardId}/lists/{listId}/cards/{cardId}     Update card
DELETE /api/kanban/boards/{boardId}/lists/{listId}/cards/{cardId}     Delete card
POST   /api/kanban/boards/{boardId}/lists/{srcListId}/cards/{cardId}/move   Move card
GET    /api/kanban-cards/{cardId}/history                              Card history
```

### Attachments
```
GET    /api/kanban/cards/{cardId}/attachments                      List attachments
POST   /api/kanban/cards/{cardId}/attachments                      Upload attachment
GET    /api/kanban/cards/{cardId}/attachments/{id}/download        Download
DELETE /api/kanban/cards/{cardId}/attachments/{id}                 Delete
```

## Architecture Pattern

This service uses the **Aggregate Pattern** from Domain-Driven Design:

```
KanbanBoard (Root)
    └── KanbanList[]
            └── KanbanCard[]
```

**Key Rule:** Save the board root to save all children. Never save lists or cards directly.

```java
// Load board (includes lists and cards)
KanbanBoard board = boardRepository.findById(id).get();

// Modify child
board.getLists().get(0).setTitle("New Title");

// Save root (children saved automatically)
boardRepository.save(board);  // ONE save for all
```

## Configuration

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/kanban_db
    username: 101_user
    password: ${DB_PASSWORD}

app:
  storage:
    service-url: http://localhost:8084
```

## Multi-Tenant Support

This service implements full multi-tenant isolation:

- **TenantFilter** - Extracts `X-Tenant-Id` from request headers
- **TenantContext** - ThreadLocal storage for current tenant
- **TenantAspect** - AOP-based query filtering by tenant_id
- All board/list/card queries filtered by tenant

## Quick Start

```bash
# Build shared-libraries first
cd ../shared-libraries && mvn clean install -DskipTests

# Build
cd ../kanban-service
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# Test
mvn test

# Health Check
curl http://localhost:8081/actuator/health
```

## Testing Examples

```bash
# Create board
curl -X POST http://localhost:8081/api/kanban/boards \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-uuid" \
  -H "X-Tenant-Id: tenant-uuid" \
  -d '{
    "title": "My Project Board",
    "prefix": "MYPR"
  }'

# Get board (includes lists and cards)
curl http://localhost:8081/api/kanban/boards/{boardId} \
  -H "X-User-Id: user-uuid" \
  -H "X-Tenant-Id: tenant-uuid"

# Add card
curl -X POST http://localhost:8081/api/kanban/boards/{boardId}/lists/{listId}/cards \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-uuid" \
  -H "X-Tenant-Id: tenant-uuid" \
  -d '{
    "title": "New Feature",
    "description": "Implement new feature"
  }'

# Upload attachment
curl -X POST http://localhost:8081/api/kanban/cards/{cardId}/attachments \
  -H "X-User-Id: user-uuid" \
  -H "X-Tenant-Id: tenant-uuid" \
  -F "file=@document.pdf"
```

## Dependencies

- **MySQL** - Board/list/card data storage
- **Storage Service** - File attachments (MinIO)
- **shared-libraries** - Shared domain models

## API Documentation

- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/api-docs

---

