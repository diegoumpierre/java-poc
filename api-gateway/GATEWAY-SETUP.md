# API Gateway - Configuração Completa

**Porta:** 8080
**Status:** Funcional e pronto para uso
**Data:** 2026-01-23

---

## 🎯 Visão Geral

O API Gateway atua como ponto de entrada único para todos os microserviços, fornecendo:

- ✅ Autenticação JWT centralizada
- ✅ Rate limiting com Redis
- ✅ Circuit breaker (Resilience4j)
- ✅ CORS configurado
- ✅ Retry automático
- ✅ Fallback para serviços indisponíveis
- ✅ Acesso aos Swagger UIs de todos os serviços

---

## 🔐 Autenticação

### Rotas Públicas (Sem JWT)

```
POST   /api/auth/login
POST   /api/auth/register
POST   /api/auth/forgot-password
POST   /api/auth/verify-code
POST   /api/auth/reset-password
GET    /api/auth/actuator/health
GET    /api/kanban/actuator/health
GET    /api/products/actuator/health
GET    /api/project/actuator/health
```

### Swagger UI (Público)

```
/api/auth/swagger-ui.html
/api/kanban/swagger-ui.html
/api/products/swagger-ui.html
/api/project/swagger-ui.html
```

### Rotas Protegidas (Requer JWT)

Todas as outras rotas (`/api/**`) requerem token JWT válido no header:

```
Authorization: Bearer <jwt-token>
```

O gateway extrai informações do JWT e adiciona headers para os serviços downstream:

- `X-User-Id`: ID do usuário
- `X-User-Email`: Email do usuário
- `X-User-Role`: Role do usuário

---

## 📡 Serviços Roteados

| Padrão de Rota | Serviço Destino | Porta |
|----------------|-----------------|-------|
| `/api/auth/**` | auth-service | 8091 |
| `/api/users/**` | auth-service | 8091 |
| `/api/profile/**` | auth-service | 8091 |
| `/api/settings/**` | auth-service | 8091 |
| `/api/kanban/**` | kanban-service | 8081 |
| `/api/products/**` | product-service | 8082 |
| `/api/codegen/**` | codegen-service | 8083 |

---

## ⚙️ Configurações Externas

### Redis (Rate Limiting)

```yaml
host: 127.0.0.1
port: 6379
password: redis-default
```

### JWT Secret

**IMPORTANTE:** O JWT secret deve ser o mesmo do auth-service!

```yaml
jwt:
  secret: ${JWT_SECRET:change-this-jwt-secret-change-in-production}
```

**Configure via variável de ambiente em produção:**

```bash
export JWT_SECRET="seu-secret-ultra-seguro-256-bits-aqui"
```

---

## 🔄 Circuit Breaker

**Configuração Resilience4j:**

- **Sliding Window:** 10 chamadas
- **Failure Rate Threshold:** 50%
- **Wait Duration (Open State):** 30 segundos
- **Timeout:** 30 segundos
- **Fallback:** `/fallback` endpoint

**Resposta de Fallback:**

```json
{
  "error": "Service Temporarily Unavailable",
  "message": "The requested service is currently unavailable. Please try again later.",
  "timestamp": "2026-01-23T12:00:00Z",
  "status": 503
}
```

---

## 🔁 Retry Policy

**Apenas para requisições GET:**

- **Retries:** 3 tentativas
- **First Backoff:** 50ms
- **Max Backoff:** 500ms

---

## 🌐 CORS

**Origens permitidas:**

- `http://localhost:3000`
- `http://localhost:3001`
- `http://localhost:4200`

**Métodos:** GET, POST, PUT, DELETE, PATCH, OPTIONS
**Headers:** Todos (`*`)
**Credentials:** Permitido
**Max Age:** 3600 segundos

---

## 🚀 Como Usar

### 1. Pré-requisitos

**Redis deve estar rodando:**

```bash
# Verificar se Redis está acessível
redis-cli -h 127.0.0.1 -p 6379 -a "redis-default" ping
# Deve retornar: PONG
```

**Serviços backend devem estar rodando:**

```bash
curl http://localhost:8091/actuator/health  # auth-service
curl http://localhost:8081/actuator/health  # kanban-service
curl http://localhost:8082/actuator/health  # product-service
curl http://localhost:8083/actuator/health  # codegen-service
```

### 2. Build e Start

```bash
cd api-gateway

# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# Ou executar o JAR
java -jar target/api-gateway-1.0.0.jar
```

### 3. Health Check

```bash
curl http://localhost:8080/actuator/health
```

**Resposta esperada:**

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"},
    "redis": {"status": "UP"}
  }
}
```

### 4. Testar Autenticação

**Login (rota pública):**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "changeme123"
  }'
```

**Resposta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

**Acessar rota protegida:**

```bash
TOKEN="<jwt-token-aqui>"

curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Acessar Swagger UIs

```bash
# Auth Service Swagger
http://localhost:8080/api/auth/swagger-ui.html

# Kanban Service Swagger
http://localhost:8080/api/kanban/swagger-ui.html

# Product Service Swagger
http://localhost:8080/api/products/swagger-ui.html

# Project Service Swagger
http://localhost:8080/api/project/swagger-ui.html
```

---

## 🔍 Monitoramento

### Actuator Endpoints

```bash
# Health
curl http://localhost:8080/actuator/health

# Gateway Routes
curl http://localhost:8080/actuator/gateway/routes

# Metrics
curl http://localhost:8080/actuator/metrics

# Prometheus
curl http://localhost:8080/actuator/prometheus
```

### Logs

```bash
# Console logs (DEBUG level para gateway)
tail -f logs/gateway.log

# Verificar rotas carregadas
grep "RouteDefinition" logs/gateway.log

# Verificar autenticação JWT
grep "JWT validated" logs/gateway.log
```

---

## 🐛 Troubleshooting

### ❌ "Connection refused to Redis"

**Verificar:**

```bash
# Testar conexão Redis
redis-cli -h 127.0.0.1 -p 6379 -a "redis-default" ping

# Verificar configuração no application.yml
cat src/main/resources/application.yml | grep -A 5 "redis"
```

### ❌ "JWT signature does not match"

**Causa:** JWT secret diferente entre gateway e auth-service.

**Solução:** Usar mesma variável de ambiente:

```bash
export JWT_SECRET="change-this-jwt-secret-change-in-production"

# Reiniciar ambos os serviços
cd auth-service && mvn spring-boot:run &
cd api-gateway && mvn spring-boot:run &
```

### ❌ "401 Unauthorized"

**Verificar formato do header:**

```bash
# ✅ CORRETO
Authorization: Bearer eyJhbGc...

# ❌ ERRADO (faltando "Bearer")
Authorization: eyJhbGc...
```

### ❌ "Service Unavailable" (Circuit Breaker)

**Causa:** Serviço backend não está respondendo.

**Verificar:**

```bash
# Health dos serviços
curl http://localhost:8091/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health

# Logs do gateway
grep "Circuit breaker" logs/gateway.log
```

### ❌ "CORS Error" no Frontend

**Adicionar origem no application.yml:**

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins:
              - "http://localhost:3000"
              - "http://localhost:3001"
              - "http://sua-origem-aqui"  # Adicionar aqui
```

---

## 📊 Endpoints de Teste Rápido

### Teste Completo (Com Gateway)

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"changeme123"}' \
  | jq -r '.token')

# 2. Obter perfil
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"

# 3. Listar boards do Kanban
curl http://localhost:8080/api/kanban/boards \
  -H "Authorization: Bearer $TOKEN"

# 4. Listar produtos
curl http://localhost:8080/api/products/all \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔐 Segurança em Produção

### Variáveis de Ambiente Obrigatórias

```bash
# JWT Secret (256 bits mínimo)
export JWT_SECRET="<secret-ultra-seguro-256-bits>"

# Redis Password
export SPRING_DATA_REDIS_PASSWORD="<senha-redis-producao>"

# Habilitar HTTPS
export SERVER_SSL_ENABLED=true
export SERVER_SSL_KEY_STORE=classpath:keystore.p12
export SERVER_SSL_KEY_STORE_PASSWORD=<senha>
```

### CORS em Produção

```yaml
allowedOrigins:
  - "https://app.my-platform.com"  # Apenas produção
  - "https://admin.my-platform.com"
```

### Rate Limiting Customizado (Se Necessário)

Adicionar filtro customizado para rate limiting por usuário/IP.

---

## 📝 Notas Importantes

1. **Redis é obrigatório** - Gateway não inicia sem Redis
2. **JWT Secret deve ser o mesmo** em gateway e auth-service
3. **Swagger UIs são públicos** - Proteger em produção se necessário
4. **Circuit Breaker ativo** - 30 segundos de espera após falhas
5. **Logs em DEBUG** - Reduzir para INFO em produção

---

## ✅ Checklist de Deploy

- [ ] Redis acessível e configurado
- [ ] JWT_SECRET configurado como variável de ambiente
- [ ] Todos os serviços backend rodando (8091, 8081, 8082, 8083)
- [ ] Gateway inicia sem erros
- [ ] Health check retorna UP
- [ ] Login funciona via gateway
- [ ] Rotas protegidas validam JWT
- [ ] Swagger UIs acessíveis via gateway
- [ ] CORS configurado para origens corretas
- [ ] Logs em nível apropriado (INFO em produção)

---

**Última Atualização:** 2026-01-23
**Versão Gateway:** 1.0.0
**Spring Cloud:** 2024.0.0
**Spring Boot:** 3.4.1
