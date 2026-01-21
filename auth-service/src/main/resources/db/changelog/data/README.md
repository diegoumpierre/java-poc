# Seed Data - Auth Service

## Arquivos de Dados

Os arquivos são carregados em ordem alfabética pelo Liquibase (`includeAll`):

```
db/changelog/data/
├── db.changelog-data.yaml         # usa includeAll para ler todos .sql
├── 002-seed-users.sql             # 8 usuarios
├── 003-seed-roles.sql             # 5 roles
├── 004-seed-permissions.sql       # 8 permissions
├── 005-seed-role-permissions.sql  # associacoes role<->permission
```

---

## Hierarquia de Tenants

```
101 Softwares (PLATFORM) [00000000-0000-0000-0000-000000000000]
  |
  +-- Grupo ABC (RESELLER) [10000000-0000-0000-0000-000000000001]
  |     +-- ABC Matriz (CLIENT) [11000000-0000-0000-0000-000000000001]
  |     +-- ABC Filial SP (CLIENT) [11000000-0000-0000-0000-000000000002]
  |     +-- ABC Filial RJ (CLIENT) [11000000-0000-0000-0000-000000000003]
  |
  +-- Empresa XYZ (CLIENT) [20000000-0000-0000-0000-000000000001] [TRIAL]
  |
  +-- Consultoria 123 (RESELLER) [30000000-0000-0000-0000-000000000001]
        +-- Cliente Alpha (CLIENT) [31000000-0000-0000-0000-000000000001]
        +-- Cliente Beta (CLIENT) [31000000-0000-0000-0000-000000000002] [SUSPENDED]
```

---

## Usuarios de Teste

**Senha para todos: `password`**

| Email | Tenant | Roles | Permissions |
|-------|--------|-------|-------------|
| admin@example.com | 101 Softwares | SUPER_ADMIN, ADMIN, MANAGER | PLATFORM_ADMIN |
| admin@grupoabc.com | Grupo ABC | ADMIN | RESELLER_MANAGE |
| admin@abcmatriz.com | ABC Matriz | ADMIN | - |
| joao@abcmatriz.com | ABC Matriz | USER | - |
| admin@abcsp.com | ABC Filial SP | ADMIN | - |
| admin@empresaxyz.com | Empresa XYZ | ADMIN | - |
| admin@consultoria123.com | Consultoria 123 | ADMIN | RESELLER_MANAGE |
| admin@clientealpha.com | Cliente Alpha | ADMIN | - |

---

## Sistema de Controle de Acesso

> **Nota:** Menus sao gerenciados pelo organization-service (`/api/organizations/menus`).
> O auth-service fornece roles, permissions e entitlements via `/api/auth/context`.

---

## Como Resetar os Dados

```bash
# 1. Limpar banco (remove todos os dados)
mysql -h 127.0.0.1 -u root -p auth_db < src/main/resources/db/seed/reset-sample-data.sql

# 2. Reiniciar aplicação (Liquibase recarrega dados)
./101 stop auth && ./101 start auth

# Ou apenas reiniciar
./101 restart auth
```

