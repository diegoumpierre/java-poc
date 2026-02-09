-- ============================================================================
-- KANBAN SERVICE - SAMPLE DATA
-- Dados de exemplo para desenvolvimento e testes
--
-- Executar: mysql -h 127.0.0.1 -u root -p kanban_db < sample-data.sql
-- Dependencia: Executar auth-service/seed/sample-data.sql ANTES
-- ============================================================================

-- ============================================================================
-- BOARDS
-- ============================================================================
INSERT INTO KANB_BOARDS (ID, TENANT_ID, NAME, DESCRIPTION, PREFIX, NEXT_CARD_NUMBER, CREATED_BY, CREATED_AT, UPDATED_AT)
VALUES
    ('b-qa-ciclo-fev-2026-00000001', 't-soft-0001-0001-000000000001', 'QA Ciclo Fevereiro 2026', 'Board de testes baseado nos TECH-REPORTs', 'QA', 27, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),
    ('b-dev-sprint-01-2026-00001', 't-soft-0001-0001-000000000001', 'Dev Sprint 01 - Fev/2026', 'Sprint atual de desenvolvimento', 'DEV', 9, 'u-dev-sr-0001-0001-000000000002', NOW(), NOW()),
    ('b-demo-proj-00000000000001', 't-demo-0001-0001-0001-000000000002', 'Projeto Demo', 'Board de exemplo para cliente demo', 'DEMO', 1, 'u-demo-user-0001-000000000008', NOW(), NOW())
ON DUPLICATE KEY UPDATE UPDATED_AT = NOW();

-- ============================================================================
-- LISTS
-- ============================================================================
INSERT INTO KANB_LISTS (ID, BOARD_ID, TITLE, POSITION, CREATED_AT, UPDATED_AT)
VALUES
    -- QA Board
    ('l-qa-backlog-00000000000001', 'b-qa-ciclo-fev-2026-00000001', 'Backlog', 0, NOW(), NOW()),
    ('l-qa-em-teste-0000000000002', 'b-qa-ciclo-fev-2026-00000001', 'Em Teste', 1, NOW(), NOW()),
    ('l-qa-bug-found-0000000000003', 'b-qa-ciclo-fev-2026-00000001', 'Bug Encontrado', 2, NOW(), NOW()),
    ('l-qa-wait-dev-0000000000004', 'b-qa-ciclo-fev-2026-00000001', 'Aguardando Dev', 3, NOW(), NOW()),
    ('l-qa-retest-00000000000005', 'b-qa-ciclo-fev-2026-00000001', 'Reteste', 4, NOW(), NOW()),
    ('l-qa-done-000000000000006', 'b-qa-ciclo-fev-2026-00000001', 'Concluido', 5, NOW(), NOW()),
    -- Dev Board
    ('l-dev-backlog-0000000000001', 'b-dev-sprint-01-2026-00001', 'Backlog', 0, NOW(), NOW()),
    ('l-dev-in-progress-00000002', 'b-dev-sprint-01-2026-00001', 'Em Desenvolvimento', 1, NOW(), NOW()),
    ('l-dev-code-review-00000003', 'b-dev-sprint-01-2026-00001', 'Code Review', 2, NOW(), NOW()),
    ('l-dev-qa-000000000000000004', 'b-dev-sprint-01-2026-00001', 'QA', 3, NOW(), NOW()),
    ('l-dev-done-00000000000005', 'b-dev-sprint-01-2026-00001', 'Concluido', 4, NOW(), NOW()),
    -- Demo Board
    ('l-demo-todo-00000000000001', 'b-demo-proj-00000000000001', 'A Fazer', 0, NOW(), NOW()),
    ('l-demo-doing-0000000000002', 'b-demo-proj-00000000000001', 'Fazendo', 1, NOW(), NOW()),
    ('l-demo-done-00000000000003', 'b-demo-proj-00000000000001', 'Feito', 2, NOW(), NOW())
ON DUPLICATE KEY UPDATE UPDATED_AT = NOW();

-- ============================================================================
-- LABELS
-- ============================================================================
INSERT INTO KANB_LABELS (ID, BOARD_ID, NAME, COLOR, CREATED_AT, UPDATED_AT)
VALUES
    -- QA - Severidade
    ('lbl-qa-critical-0000000001', 'b-qa-ciclo-fev-2026-00000001', 'CRITICAL', '#DC2626', NOW(), NOW()),
    ('lbl-qa-high-000000000000002', 'b-qa-ciclo-fev-2026-00000001', 'HIGH', '#EA580C', NOW(), NOW()),
    ('lbl-qa-medium-00000000003', 'b-qa-ciclo-fev-2026-00000001', 'MEDIUM', '#CA8A04', NOW(), NOW()),
    ('lbl-qa-low-0000000000000004', 'b-qa-ciclo-fev-2026-00000001', 'LOW', '#16A34A', NOW(), NOW()),
    -- QA - Servicos
    ('lbl-qa-auth-00000000000005', 'b-qa-ciclo-fev-2026-00000001', 'auth-service:8091', '#7C3AED', NOW(), NOW()),
    ('lbl-qa-kanban-000000000006', 'b-qa-ciclo-fev-2026-00000001', 'kanban-service:8081', '#2563EB', NOW(), NOW()),
    ('lbl-qa-billing-0000000007', 'b-qa-ciclo-fev-2026-00000001', 'billing-service:8087', '#0891B2', NOW(), NOW()),
    ('lbl-qa-notif-00000000000008', 'b-qa-ciclo-fev-2026-00000001', 'notification:8085', '#DB2777', NOW(), NOW()),
    ('lbl-qa-storage-0000000009', 'b-qa-ciclo-fev-2026-00000001', 'storage-service:8086', '#78716C', NOW(), NOW()),
    ('lbl-qa-customer-000000010', 'b-qa-ciclo-fev-2026-00000001', 'api-gateway:8080', '#059669', NOW(), NOW()),
    ('lbl-qa-ui-0000000000000011', 'b-qa-ciclo-fev-2026-00000001', 'ui-generator:3001', '#E11D48', NOW(), NOW()),
    -- Dev - Tipo
    ('lbl-dev-feature-0000000001', 'b-dev-sprint-01-2026-00001', 'FEATURE', '#16A34A', NOW(), NOW()),
    ('lbl-dev-bug-00000000000002', 'b-dev-sprint-01-2026-00001', 'BUG', '#DC2626', NOW(), NOW()),
    ('lbl-dev-refactor-00000003', 'b-dev-sprint-01-2026-00001', 'REFACTOR', '#6B7280', NOW(), NOW()),
    ('lbl-dev-tech-debt-0000004', 'b-dev-sprint-01-2026-00001', 'TECH-DEBT', '#7C3AED', NOW(), NOW()),
    ('lbl-dev-docs-00000000000005', 'b-dev-sprint-01-2026-00001', 'DOCS', '#0EA5E9', NOW(), NOW())
ON DUPLICATE KEY UPDATE UPDATED_AT = NOW();

-- ============================================================================
-- CARDS - QA AUTH SERVICE
-- ============================================================================
INSERT INTO KANB_CARDS (ID, BOARD_ID, LIST_ID, CARD_NUMBER, TITLE, DESCRIPTION, POSITION, PRIORITY, COMPLETED, CREATED_BY, CREATED_AT, UPDATED_AT)
VALUES
('c-qa-auth-login-0000000001', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0001',
'[auth-service] Login e Registro',
'## Servico: auth-service (8091)

### POST /api/auth/register
- [ ] Registrar com email valido
- [ ] Email duplicado -> 409
- [ ] Sem @email -> 400

### POST /api/auth/login
- [ ] Credenciais corretas -> JWT
- [ ] Senha incorreta -> 401

### POST /api/auth/refresh
- [ ] Token valido -> Novo token
- [ ] Token expirado -> 401

### POST /api/auth/logout
- [ ] Invalida refresh token

**Credenciais:** admin@example.com / changeme123',
0, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-auth-2fa-00000000002', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0002',
'[auth-service] 2FA',
'## Servico: auth-service (8091)

### POST /api/auth/2fa/enable
- [ ] Retorna QR code TOTP

### POST /api/auth/2fa/verify
- [ ] Codigo correto -> Login completo
- [ ] Codigo incorreto -> 401
- [ ] Codigo expirado -> 401

### Login com 2FA
- [ ] /login retorna requires2FA: true
- [ ] /2fa/verify completa login',
1, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-auth-password-00000003', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0003',
'[auth-service] Password Reset',
'## Servico: auth-service (8091)
Integracao: notification-service

### POST /api/auth/forgot-password
- [ ] Email existente -> 200 (email enviado)
- [ ] Email inexistente -> 200 (nao revelar)

### POST /api/auth/reset-password
- [ ] Token valido -> Senha alterada
- [ ] Token expirado -> 400
- [ ] Token ja usado -> 400',
2, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-auth-profile-00000004', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0004',
'[auth-service] Profile e Avatar',
'## Servico: auth-service (8091)
Integracao: storage-service (MinIO)

### GET /api/profile
- [ ] Retorna dados do usuario

### PUT /api/profile
- [ ] Atualizar nome -> 200

### POST /api/profile/avatar
- [ ] Upload JPG < 5MB -> 200
- [ ] Upload > 5MB -> 400
- [ ] Tipo invalido -> 400

### DELETE /api/profile/avatar
- [ ] Avatar removido',
3, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-auth-multitenancy-005', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0005',
'[auth-service] Multi-tenancy',
'## Servico: auth-service (8091)

### GET /api/tenants
- [ ] Retorna apenas meus tenants

### POST /api/tenants
- [ ] Criar tenant -> Owner vira ADMIN
- [ ] Slug duplicado -> 409

### PUT /api/tenants/{id}
- [ ] Atualizar nome/logo

### DELETE /api/tenants/{id}
- [ ] Soft delete

### Isolamento
- [ ] Tenant A nao ve dados do Tenant B',
4, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-auth-membership-0006', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0006',
'[auth-service] Memberships',
'## Servico: auth-service (8091)

### GET /api/memberships
- [ ] Listar membros do tenant

### POST /api/memberships/invite
- [ ] Convidar email -> Email enviado
- [ ] Email ja membro -> 409

### PUT /api/memberships/{id}/role
- [ ] Admin altera role -> 200
- [ ] Non-admin -> 403

### DELETE /api/memberships/{id}
- [ ] Remover membro
- [ ] Ultimo admin -> 400',
5, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-auth-rbac-000000007', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0007',
'[auth-service] RBAC',
'## Servico: auth-service (8091)

### GET /api/roles
- [ ] Listar roles do tenant

### POST /api/roles
- [ ] Criar role customizada

### DELETE /api/roles/{id}
- [ ] Deletar customizada -> 200
- [ ] Deletar IS_SYSTEM=1 -> 400

### Permissions
- [ ] GET /api/roles/{id}/permissions
- [ ] POST /api/roles/{id}/permissions',
6, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-auth-sessions-00000008', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0008',
'[auth-service] Sessions',
'## Servico: auth-service (8091)

### GET /api/sessions
- [ ] Listar sessoes ativas

### DELETE /api/sessions/{id}
- [ ] Encerrar sessao especifica

### DELETE /api/sessions/all
- [ ] Encerrar todas exceto atual

### Verificar
- [ ] Login cria sessao com device_info
- [ ] last_activity atualizado',
7, 'LOW', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW())

ON DUPLICATE KEY UPDATE UPDATED_AT = NOW();

-- ============================================================================
-- CARDS - QA KANBAN SERVICE
-- ============================================================================
INSERT INTO KANB_CARDS (ID, BOARD_ID, LIST_ID, CARD_NUMBER, TITLE, DESCRIPTION, POSITION, PRIORITY, COMPLETED, CREATED_BY, CREATED_AT, UPDATED_AT)
VALUES
('c-qa-kanban-boards-000009', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0009',
'[kanban-service] Boards CRUD',
'## Servico: kanban-service (8081)

### GET /api/kanban/boards
- [ ] Listar boards do tenant

### GET /api/kanban/boards/{id}
- [ ] Board completo com lists, cards
- [ ] Outro tenant -> 404

### POST /api/kanban/boards
- [ ] Criar com name e prefix
- [ ] NEXT_CARD_NUMBER = 1

### DELETE /api/kanban/boards/{id}
- [ ] Remove em cascata',
8, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-kanban-cards-0000010', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0010',
'[kanban-service] Cards e Numeracao',
'## Servico: kanban-service (8081)

### POST /api/kanban/boards/{boardId}/cards
- [ ] Criar card -> Recebe card_number
- [ ] Numeracao: PREFIX-0001, PREFIX-0002

### PUT /api/kanban/boards/{boardId}/cards/{cardId}
- [ ] Atualizar title, description
- [ ] Atualizar priority
- [ ] Marcar completed = true

### DELETE
- [ ] card_number NAO reutilizado',
9, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-kanban-move-00000011', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0011',
'[kanban-service] Move Card',
'## Servico: kanban-service (8081)

### PUT /api/kanban/cards/{cardId}/move
Body: { targetListId, targetPosition }

- [ ] Mover para outra lista
- [ ] Mover para posicao especifica
- [ ] Reordenar na mesma lista
- [ ] History registra MOVED

### Bulk Move
- [ ] PUT /boards/{id}/cards/bulk-move',
10, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-kanban-comments-0012', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0012',
'[kanban-service] Comments e SubTasks',
'## Servico: kanban-service (8081)

### Comments
- [ ] GET /cards/{cardId}/comments
- [ ] POST /cards/{cardId}/comments
- [ ] PUT (editar proprio)
- [ ] DELETE (deletar proprio)

### SubTasks
- [ ] POST /cards/{cardId}/subtasks
- [ ] PUT (toggle done)
- [ ] Verificar percentual de conclusao',
11, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-kanban-attachments-13', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0013',
'[kanban-service] Attachments',
'## Servico: kanban-service (8081)
Integracao: storage-service (MinIO)

### POST /cards/{cardId}/attachments/upload
- [ ] Upload < 10MB -> 200
- [ ] Verificar no MinIO

### GET /attachments/{id}/download
- [ ] Retorna presigned URL

### DELETE /attachments/{id}
- [ ] Remove do DB e MinIO',
12, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-kanban-history-00014', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0014',
'[kanban-service] Card History',
'## Servico: kanban-service (8081)

### GET /cards/{cardId}/history
Verificar:
- [ ] action = CREATED ao criar
- [ ] action = MOVED ao mover
- [ ] action = UPDATED ao editar
- [ ] action = COMMENTED ao comentar
- [ ] action = ATTACHMENT_ADDED ao anexar
- [ ] action = COMPLETED ao completar',
13, 'LOW', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW())

ON DUPLICATE KEY UPDATE UPDATED_AT = NOW();

-- ============================================================================
-- CARDS - QA BILLING SERVICE
-- ============================================================================
INSERT INTO KANB_CARDS (ID, BOARD_ID, LIST_ID, CARD_NUMBER, TITLE, DESCRIPTION, POSITION, PRIORITY, COMPLETED, CREATED_BY, CREATED_AT, UPDATED_AT)
VALUES
('c-qa-billing-products-0015', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0015',
'[billing-service] Products e Plans',
'## Servico: billing-service (8087)

### Products
- [ ] GET /api/products
- [ ] POST /api/products
- [ ] POST /api/products/{id}/sync-stripe

### Plans
- [ ] GET /api/plans
- [ ] POST /api/plans (product_id, price, currency, billing_interval)
- [ ] GET /api/plans/{id}/features
- [ ] POST /api/plans/{id}/features',
14, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-billing-stripe-00016', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0016',
'[billing-service] Checkout Stripe',
'## Servico: billing-service (8087)

### POST /api/subscriptions/checkout
- [ ] Cria checkout session
- [ ] Retorna checkoutUrl

### Fluxo Completo
1. [ ] POST /checkout
2. [ ] Redireciona para Stripe
3. [ ] Cartao teste: 4242 4242 4242 4242
4. [ ] Webhook checkout.session.completed
5. [ ] Subscription criada
6. [ ] Entitlements criados

### Outros
- [ ] GET /api/subscriptions
- [ ] POST /api/subscriptions/cancel
- [ ] GET /api/subscriptions/portal',
15, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-billing-pix-000000017', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0017',
'[billing-service] PIX e Boleto',
'## Servico: billing-service (8087)
Integracao: EFI Bank

### PIX
- [ ] POST /api/billing/efi/pix/create
- [ ] GET /api/billing/efi/pix/{id}/qrcode
- [ ] Simular pagamento sandbox
- [ ] Webhook pix.received

### Boleto
- [ ] POST /api/billing/efi/boleto/create
- [ ] GET /api/billing/efi/boleto/{id}/pdf
- [ ] Webhook boleto.paid',
16, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-billing-entitlements-18', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0018',
'[billing-service] Entitlements',
'## Servico: billing-service (8087)

### GET /api/entitlements
- [ ] Listar do tenant

### GET /api/entitlements/check/{feature}
- [ ] Feature habilitada -> allowed: true
- [ ] Feature nao habilitada -> allowed: false
- [ ] Feature com limite -> limit e used

### Cenarios
- [ ] Incrementar uso ate limite
- [ ] Apos limite -> allowed: false',
17, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW())

ON DUPLICATE KEY UPDATE UPDATED_AT = NOW();

-- ============================================================================
-- CARDS - QA NOTIFICATION, STORAGE, UI
-- ============================================================================
INSERT INTO KANB_CARDS (ID, BOARD_ID, LIST_ID, CARD_NUMBER, TITLE, DESCRIPTION, POSITION, PRIORITY, COMPLETED, CREATED_BY, CREATED_AT, UPDATED_AT)
VALUES
('c-qa-notif-email-000000019', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0019',
'[notification-service] Email',
'## Servico: notification-service (8085)

### POST /api/notification/send
- [ ] Email SIMPLE -> Recebido
- [ ] Template VERIFICATION
- [ ] Template PASSWORD_RESET
- [ ] Email invalido -> 400

### GET /api/notification/history
### GET /api/notification/templates',
18, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-notif-kafka-000000020', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0020',
'[notification-service] Kafka',
'## Servico: notification-service (8085)
Kafka: 127.0.0.1:9092

### POST /api/notification/queue
- [ ] Enfileirar -> 202 Accepted
- [ ] Consumer processa
- [ ] Verificar NOTF_EMAIL_HISTORY

### Rate Limiting
- 10 emails/hora por destinatario
- 100 emails/dia',
19, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-notif-chat-000000021', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0021',
'[notification-service] Chat',
'## Servico: notification-service (8085)

### Conversations
- [ ] GET /api/chat/conversations
- [ ] POST /api/chat/conversations

### Messages
- [ ] GET /conversations/{id}/messages
- [ ] POST /conversations/{id}/messages
- [ ] PUT /messages/{id}/read',
20, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-storage-upload-000022', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0022',
'[storage-service] Upload',
'## Servico: storage-service (8086)
MinIO: 127.0.0.1:9002

### POST /api/storage/upload
Headers: X-Tenant-Id, X-User-Id, X-Service-Origin

- [ ] Upload < 1MB -> 200
- [ ] Upload 10-50MB -> 200
- [ ] Sem auth -> 401
- [ ] Tipo invalido -> 400
- [ ] Verificar no MinIO',
21, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-storage-download-00023', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0023',
'[storage-service] Download',
'## Servico: storage-service (8086)

### GET /api/storage/{id}
- [ ] Metadata do arquivo

### GET /api/storage/{id}/download
- [ ] Download direto

### GET /api/storage/{id}/presigned
- [ ] URL valido 30min
- [ ] Apos 30min -> 403

### DELETE /api/storage/{id}
- [ ] Remove do MySQL e MinIO',
22, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-ui-auth-0000000000024', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0024',
'[ui-generator] Auth e Navegacao',
'## Servico: ui-generator (3001)

### Auth
- [ ] /auth/login funciona
- [ ] Login valido -> Dashboard
- [ ] Login invalido -> Erro
- [ ] Logout -> Redirect

### Protected Routes
- [ ] /dashboard sem login -> Redirect
- [ ] Menu funciona
- [ ] Responsividade mobile

**Credenciais:** admin@example.com / changeme123',
23, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-ui-kanban-0000000025', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0025',
'[ui-generator] Kanban App',
'## Servico: ui-generator (3001)
Rota: /apps/kanban

### Boards
- [ ] Listar boards
- [ ] Criar board
- [ ] Deletar board

### Board View
- [ ] Carregar com lists e cards
- [ ] Criar lista
- [ ] Criar card
- [ ] Drag & drop entre listas

### Card Modal
- [ ] Editar card
- [ ] Adicionar comentario
- [ ] Upload attachment',
24, 'HIGH', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW()),

('c-qa-ui-apps-0000000000026', 'b-qa-ciclo-fev-2026-00000001', 'l-qa-backlog-00000000000001', 'QA-0026',
'[ui-generator] Apps (Chat, Mail, Files)',
'## Servico: ui-generator (3001)

### Chat (/apps/chat)
- [ ] Listar conversas
- [ ] Enviar mensagem

### Mail (/apps/mail)
- [ ] Inbox carrega
- [ ] Compor email

### Calendar (/apps/calendar)
- [ ] Visualizar mes/semana/dia
- [ ] Criar/editar evento

### Files (/management/files)
- [ ] Listar arquivos
- [ ] Upload/Download
- [ ] Deletar',
25, 'MEDIUM', 0, 'u-qa-lead-0001-0001-000000000005', NOW(), NOW())

ON DUPLICATE KEY UPDATE UPDATED_AT = NOW();

-- ============================================================================
-- CARDS - DEV SPRINT
-- ============================================================================
INSERT INTO KANB_CARDS (ID, BOARD_ID, LIST_ID, CARD_NUMBER, TITLE, DESCRIPTION, POSITION, PRIORITY, COMPLETED, CREATED_BY, CREATED_AT, UPDATED_AT)
VALUES
('c-dev-oauth-000000000001', 'b-dev-sprint-01-2026-00001', 'l-dev-backlog-0000000000001', 'DEV-0001',
'[auth-service] Login Social',
'## Requisitos
- [ ] Spring Security OAuth2 Client
- [ ] GET /api/auth/oauth2/google
- [ ] GET /api/auth/oauth2/github
- [ ] Salvar provider na AUTH_USERS

## Arquivos
- config/OAuth2Config.java
- controller/OAuth2Controller.java
- domain/User.java

**Prioridade:** MEDIUM',
0, 'MEDIUM', 0, 'u-dev-sr-0001-0001-000000000002', NOW(), NOW()),

('c-dev-filters-000000000002', 'b-dev-sprint-01-2026-00001', 'l-dev-backlog-0000000000001', 'DEV-0002',
'[kanban-service] Filtros de Cards',
'## Requisitos
- [ ] GET /boards/{id}/cards/search
- [ ] Params: label, assignee, priority, dueDate
- [ ] SQL dinamico com filtros
- [ ] UI de filtros

## Arquivos
- controller/KanbanController.java
- service/KanbanBoardService.java
- repository/CardRepository.java

**Prioridade:** HIGH',
1, 'HIGH', 0, 'u-dev-sr-0001-0001-000000000002', NOW(), NOW()),

('c-dev-efi-webhook-00000003', 'b-dev-sprint-01-2026-00001', 'l-dev-backlog-0000000000001', 'DEV-0003',
'[billing-service] Webhooks EFI',
'## Requisitos
- [ ] POST /api/webhooks/efi/pix
- [ ] POST /api/webhooks/efi/boleto
- [ ] Validar signature HMAC
- [ ] Processar pix.received
- [ ] Processar boleto.paid
- [ ] Idempotencia

## Arquivos
- controller/EfiWebhookController.java
- service/EfiService.java

**Prioridade:** HIGH',
2, 'HIGH', 0, 'u-dev-sr-0001-0001-000000000002', NOW(), NOW()),

('c-dev-push-000000000000004', 'b-dev-sprint-01-2026-00001', 'l-dev-backlog-0000000000001', 'DEV-0004',
'[notification-service] Push Firebase',
'## Requisitos
- [ ] Firebase Admin SDK
- [ ] Tabela NOTF_DEVICE_TOKENS
- [ ] POST /api/devices/register
- [ ] POST /api/notifications/push
- [ ] Consumer Kafka notification.push

## Arquivos
- config/FirebaseConfig.java
- service/PushNotificationService.java
- domain/DeviceToken.java

**Prioridade:** MEDIUM',
3, 'MEDIUM', 0, 'u-dev-sr-0001-0001-000000000002', NOW(), NOW()),

('c-dev-thumbnails-00000005', 'b-dev-sprint-01-2026-00001', 'l-dev-backlog-0000000000001', 'DEV-0005',
'[storage-service] Thumbnails',
'## Requisitos
- [ ] Detectar se e imagem
- [ ] Gerar thumbnail 150x150
- [ ] Gerar thumbnail 300x300
- [ ] GET /api/storage/{id}/thumbnail?size=150
- [ ] Lazy generation

## Dependencias
- net.coobird:thumbnailator

**Prioridade:** LOW',
4, 'LOW', 0, 'u-dev-sr-0001-0001-000000000002', NOW(), NOW()),

('c-dev-dark-mode-0000000006', 'b-dev-sprint-01-2026-00001', 'l-dev-backlog-0000000000001', 'DEV-0006',
'[ui-generator] Dark Mode',
'## Requisitos
- [ ] ThemeContext com toggle
- [ ] Persistir no localStorage
- [ ] Respeitar prefers-color-scheme
- [ ] Atualizar CSS PrimeReact

## Arquivos
- contexts/ThemeContext.tsx
- layout/AppTopbar.tsx
- styles/globals.css

**Prioridade:** LOW',
5, 'LOW', 0, 'u-dev-sr-0001-0001-000000000002', NOW(), NOW()),

('c-dev-export-000000000007', 'b-dev-sprint-01-2026-00001', 'l-dev-backlog-0000000000001', 'DEV-0007',
'[data-service] Export CSV/Excel',
'## Requisitos
- [ ] GET /api/data/export?format=csv
- [ ] GET /api/data/export?format=xlsx
- [ ] Aplicar mesmos filtros
- [ ] Streaming para arquivos grandes

## Dependencias
- org.apache.poi:poi-ooxml
- com.opencsv:opencsv

**Prioridade:** LOW',
6, 'LOW', 0, 'u-dev-sr-0001-0001-000000000002', NOW(), NOW()),

('c-dev-usage-dashboard-0008', 'b-dev-sprint-01-2026-00001', 'l-dev-backlog-0000000000001', 'DEV-0008',
'[billing-service] Usage Dashboard',
'## Requisitos
- [ ] GET /api/usage/dashboard
- [ ] Metricas por periodo (7d, 30d, 90d)
- [ ] Graficos de tendencia
- [ ] Alertas de limite proximo

## Metricas
- API_CALLS, STORAGE_MB, ACTIVE_USERS
- BOARDS_CREATED, FILES_UPLOADED

**Prioridade:** MEDIUM',
7, 'MEDIUM', 0, 'u-dev-sr-0001-0001-000000000002', NOW(), NOW())

ON DUPLICATE KEY UPDATE UPDATED_AT = NOW();

SELECT 'KANBAN SAMPLE DATA OK' AS STATUS;
