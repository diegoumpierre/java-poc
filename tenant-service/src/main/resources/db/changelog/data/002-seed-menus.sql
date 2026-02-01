--liquibase formatted sql

-- =============================================
-- SEED ALL MENU ITEMS (FINAL STATE)
-- Database is dropped and recreated, fresh seed
-- =============================================

-- --------------------------------------------------
-- DASHBOARD + children
-- --------------------------------------------------
--changeset tenant:seed-menus-dashboard
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000001', NULL, 'dashboard', 'Dashboard', 'pi pi-home', NULL, 'ENTITLEMENT', '["CORE_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000002', '00000000-0000-0000-0001-000000000001', 'dashboard-overview', 'Overview', 'pi pi-fw pi-home', '/', 'ENTITLEMENT', '["CORE_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000015', '00000000-0000-0000-0001-000000000001', 'files', 'Files', 'pi pi-fw pi-folder', '/apps/files', 'ENTITLEMENT', '["FILES_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000090', '00000000-0000-0000-0001-000000000001', 'marketplace', 'Marketplace', 'pi pi-shopping-cart', '/ecommerce/marketplace', 'AUTHENTICATED', NULL, NULL, NULL, 3, 1);

-- --------------------------------------------------
-- CATEGORY GROUPS (root level)
-- --------------------------------------------------
--changeset tenant:seed-menus-categories
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0002-000000000001', NULL, 'cat-produtividade', 'Produtividade', 'pi pi-clipboard', NULL, 'FEATURE_GATED', '["KANBAN_MODULE", "EMAIL_MODULE", "CHAT_MODULE"]', NULL, NULL, 5, 1),
('00000000-0000-0000-0002-000000000002', NULL, 'cat-vendas', 'Vendas', 'pi pi-shopping-cart', NULL, 'FEATURE_GATED', '["ECOMMERCE_CATALOG", "VENDA_SIMPLES_MODULE", "SIGNATURE_MODULE", "COMISSOES_PRO_MODULE"]', NULL, NULL, 10, 1),
('00000000-0000-0000-0002-000000000003', NULL, 'cat-atendimento', 'Atendimento', 'pi pi-headphones', NULL, 'FEATURE_GATED', '["HELPDESK_MODULE", "WHATSAPP_MODULE"]', NULL, NULL, 15, 1),
('00000000-0000-0000-0002-000000000004', NULL, 'cat-financeiro', 'Financeiro', 'pi pi-wallet', NULL, 'FEATURE_GATED', '["FINANCE_MODULE", "FINANCE_ENTERPRISE"]', NULL, NULL, 20, 1),
('00000000-0000-0000-0002-000000000005', NULL, 'cat-marketing', 'Marketing', 'pi pi-megaphone', NULL, 'FEATURE_GATED', '["CMS_MODULE", "REPUTACAO_PRO_MODULE", "INSTA_METRICS_MODULE", "FIDELIDADE_PRO_MODULE"]', NULL, NULL, 25, 1),
('00000000-0000-0000-0002-000000000006', NULL, 'cat-gestao', 'Gestao', 'pi pi-briefcase', NULL, 'FEATURE_GATED', '["RH_MODULE", "BPF_MODULE", "PERICIA_MODULE", "OS_MODULE", "ESTOQUE_FACIL_MODULE"]', NULL, NULL, 30, 1);

-- --------------------------------------------------
-- PRODUTIVIDADE > Kanban
-- --------------------------------------------------
--changeset tenant:seed-menus-kanban
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000011', '00000000-0000-0000-0002-000000000001', 'kanban', 'Kanban', 'pi pi-th-large', NULL, 'ENTITLEMENT', '["KANBAN_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000012', '00000000-0000-0000-0001-000000000011', 'kanban-boards', 'Boards', 'pi pi-fw pi-table', '/apps/kanban/boards', 'ENTITLEMENT', '["KANBAN_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000013', '00000000-0000-0000-0001-000000000011', 'kanban-cards', 'Cards', 'pi pi-fw pi-list', '/apps/kanban/cards', 'ENTITLEMENT', '["KANBAN_MODULE"]', NULL, NULL, 2, 0),
('00000000-0000-0000-0001-000000000014', '00000000-0000-0000-0001-000000000011', 'kanban-engine', 'Engine', 'pi pi-fw pi-cog', '/apps/kanban/engine', 'ENTITLEMENT', '["KANBAN_MODULE"]', NULL, NULL, 3, 1);

-- --------------------------------------------------
-- PRODUTIVIDADE > GTD
-- --------------------------------------------------
--changeset tenant:seed-menus-gtd
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000017', '00000000-0000-0000-0002-000000000001', 'gtd', 'GTD', 'pi pi-check-square', '/apps/gtd', 'ENTITLEMENT', '["GTD_MODULE"]', NULL, NULL, 2, 1);

-- --------------------------------------------------
-- PRODUTIVIDADE > Email
-- --------------------------------------------------
--changeset tenant:seed-menus-email
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000605', '00000000-0000-0000-0002-000000000001', 'email', 'Email', 'pi pi-envelope', NULL, 'ENTITLEMENT', '["EMAIL_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000606', '00000000-0000-0000-0001-000000000605', 'email-inbox', 'Caixa de Entrada', 'pi pi-fw pi-inbox', '/apps/email/inbox', 'ENTITLEMENT', '["EMAIL_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000607', '00000000-0000-0000-0001-000000000605', 'email-conversations', 'Conversas', 'pi pi-fw pi-comments', '/apps/email/conversations', 'ENTITLEMENT', '["EMAIL_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000608', '00000000-0000-0000-0001-000000000605', 'email-config', 'Configuracao', 'pi pi-fw pi-cog', '/apps/email/config', 'ENTITLEMENT', '["EMAIL_MODULE"]', NULL, NULL, 3, 1);

-- --------------------------------------------------
-- PRODUTIVIDADE > Comunicador (Chat)
-- --------------------------------------------------
--changeset tenant:seed-menus-chat
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000070', '00000000-0000-0000-0002-000000000001', 'chat', 'Comunicador', 'pi pi-comments', NULL, 'FEATURE_GATED', '["CHAT_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000071', '00000000-0000-0000-0001-000000000070', 'chat-channels', 'Canais', 'pi pi-fw pi-comments', '/apps/chat', 'FEATURE_GATED', '["CHAT_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000073', '00000000-0000-0000-0001-000000000070', 'chat-livechat', 'Live Chat', 'pi pi-fw pi-headphones', '/apps/chat/livechat', 'FEATURE_GATED', '["CHAT_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000072', '00000000-0000-0000-0001-000000000070', 'chat-settings', 'Configuracao', 'pi pi-fw pi-cog', '/apps/chat/settings', 'FEATURE_GATED', '["CHAT_MODULE"]', NULL, NULL, 3, 1);

-- --------------------------------------------------
-- MARKETPLACE (root level)
-- --------------------------------------------------
--changeset tenant:seed-menus-ecommerce
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000320', NULL, 'ecom-marketplace', 'Marketplace', 'pi pi-globe', NULL, 'FEATURE_GATED', '["ECOMMERCE_STORE"]', NULL, NULL, 11, 1),
('00000000-0000-0000-0001-000000000321', '00000000-0000-0000-0001-000000000320', 'ecom-mkt-products', 'Produtos', 'pi pi-fw pi-shopping-bag', '/ecommerce/marketplace', 'FEATURE_GATED', '["ECOMMERCE_STORE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000322', '00000000-0000-0000-0001-000000000320', 'ecom-mkt-cart', 'Carrinho', 'pi pi-fw pi-shopping-cart', '/ecommerce/marketplace/cart', 'FEATURE_GATED', '["ECOMMERCE_STORE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000323', '00000000-0000-0000-0001-000000000320', 'ecom-mkt-orders', 'Meus Pedidos', 'pi pi-fw pi-list', '/ecommerce/marketplace/orders', 'FEATURE_GATED', '["ECOMMERCE_STORE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000324', '00000000-0000-0000-0001-000000000320', 'ecom-mkt-wishlist', 'Wishlist', 'pi pi-fw pi-heart', '/ecommerce/marketplace/wishlist', 'FEATURE_GATED', '["ECOMMERCE_STORE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000325', '00000000-0000-0000-0001-000000000320', 'ecom-mkt-addresses', 'Enderecos', 'pi pi-fw pi-map-marker', '/ecommerce/marketplace/addresses', 'FEATURE_GATED', '["ECOMMERCE_STORE"]', NULL, NULL, 5, 1),
-- Admin Marketplace (root level, admin only)
('00000000-0000-0000-0001-000000000330', NULL, 'ecom-admin', 'Admin Marketplace', 'pi pi-cog', NULL, 'ENTITLEMENT', '["ECOMMERCE_CATALOG"]', '["admin"]', NULL, 12, 1),
('00000000-0000-0000-0001-000000000331', '00000000-0000-0000-0001-000000000330', 'ecom-admin-products', 'Produtos', 'pi pi-fw pi-box', '/ecommerce/admin/products', 'ENTITLEMENT', '["ECOMMERCE_CATALOG"]', '["admin"]', NULL, 1, 1),
('00000000-0000-0000-0001-000000000332', '00000000-0000-0000-0001-000000000330', 'ecom-admin-categories', 'Categorias', 'pi pi-fw pi-tags', '/ecommerce/admin/categories', 'ENTITLEMENT', '["ECOMMERCE_CATALOG"]', '["admin"]', NULL, 2, 1),
('00000000-0000-0000-0001-000000000333', '00000000-0000-0000-0001-000000000330', 'ecom-admin-orders', 'Pedidos', 'pi pi-fw pi-list', '/ecommerce/admin/orders', 'FEATURE_GATED', '["ECOMMERCE_STORE"]', '["admin"]', NULL, 3, 1),
('00000000-0000-0000-0001-000000000334', '00000000-0000-0000-0001-000000000330', 'ecom-admin-coupons', 'Cupons', 'pi pi-fw pi-ticket', '/ecommerce/admin/coupons', 'FEATURE_GATED', '["ECOMMERCE_STORE"]', '["admin"]', NULL, 4, 1),
('00000000-0000-0000-0001-000000000335', '00000000-0000-0000-0001-000000000330', 'ecom-admin-shipping', 'Frete', 'pi pi-fw pi-truck', '/ecommerce/admin/shipping', 'FEATURE_GATED', '["ECOMMERCE_STORE"]', '["admin"]', NULL, 5, 1),
('00000000-0000-0000-0001-000000000336', '00000000-0000-0000-0001-000000000330', 'ecom-admin-reviews', 'Reviews', 'pi pi-fw pi-star', '/ecommerce/admin/reviews', 'FEATURE_GATED', '["ECOMMERCE_STORE"]', '["admin"]', NULL, 6, 1),
('00000000-0000-0000-0001-000000000337', '00000000-0000-0000-0001-000000000330', 'ecom-admin-vendors', 'Vendors', 'pi pi-fw pi-users', '/ecommerce/admin/vendors', 'FEATURE_GATED', '["ECOMMERCE_MARKETPLACE"]', '["admin"]', NULL, 7, 1),
('00000000-0000-0000-0001-000000000338', '00000000-0000-0000-0001-000000000330', 'ecom-admin-payouts', 'Payouts', 'pi pi-fw pi-wallet', '/ecommerce/admin/payouts', 'FEATURE_GATED', '["ECOMMERCE_MARKETPLACE"]', '["admin"]', NULL, 8, 1),
('00000000-0000-0000-0001-000000000339', '00000000-0000-0000-0001-000000000330', 'ecom-admin-settings', 'Configuracoes', 'pi pi-fw pi-cog', '/ecommerce/admin/settings', 'ENTITLEMENT', '["ECOMMERCE_CATALOG"]', '["admin"]', NULL, 9, 1),
-- Minha Loja (root level, vendor portal)
('00000000-0000-0000-0001-000000000340', NULL, 'ecom-vendor', 'Minha Loja', 'pi pi-shop', NULL, 'FEATURE_GATED', '["ECOMMERCE_MARKETPLACE"]', NULL, NULL, 13, 1),
('00000000-0000-0000-0001-000000000341', '00000000-0000-0000-0001-000000000340', 'ecom-vendor-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/ecommerce/vendor', 'FEATURE_GATED', '["ECOMMERCE_MARKETPLACE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000342', '00000000-0000-0000-0001-000000000340', 'ecom-vendor-register', 'Registrar', 'pi pi-fw pi-user-plus', '/ecommerce/vendor/register', 'FEATURE_GATED', '["ECOMMERCE_MARKETPLACE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000343', '00000000-0000-0000-0001-000000000340', 'ecom-vendor-products', 'Produtos', 'pi pi-fw pi-box', '/ecommerce/vendor/products', 'FEATURE_GATED', '["ECOMMERCE_MARKETPLACE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000344', '00000000-0000-0000-0001-000000000340', 'ecom-vendor-orders', 'Pedidos', 'pi pi-fw pi-shopping-bag', '/ecommerce/vendor/orders', 'FEATURE_GATED', '["ECOMMERCE_MARKETPLACE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000345', '00000000-0000-0000-0001-000000000340', 'ecom-vendor-payouts', 'Payouts', 'pi pi-fw pi-wallet', '/ecommerce/vendor/payouts', 'FEATURE_GATED', '["ECOMMERCE_MARKETPLACE"]', NULL, NULL, 5, 1);

-- --------------------------------------------------
-- VENDAS > VendaSimples
-- --------------------------------------------------
--changeset tenant:seed-menus-venda-simples
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000240', '00000000-0000-0000-0002-000000000002', 'venda-simples', 'VendaSimples', 'pi pi-chart-line', NULL, 'ENTITLEMENT', '["VENDA_SIMPLES_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000241', '00000000-0000-0000-0001-000000000240', 'vs-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/venda-simples', 'ENTITLEMENT', '["VENDA_SIMPLES_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000242', '00000000-0000-0000-0001-000000000240', 'vs-leads', 'Leads', 'pi pi-fw pi-users', '/apps/venda-simples/leads', 'ENTITLEMENT', '["VENDA_SIMPLES_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000243', '00000000-0000-0000-0001-000000000240', 'vs-pipeline', 'Pipeline', 'pi pi-fw pi-th-large', '/apps/venda-simples/pipeline', 'ENTITLEMENT', '["VENDA_SIMPLES_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000244', '00000000-0000-0000-0001-000000000240', 'vs-atividades', 'Atividades', 'pi pi-fw pi-clock', '/apps/venda-simples/atividades', 'ENTITLEMENT', '["VENDA_SIMPLES_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000245', '00000000-0000-0000-0001-000000000240', 'vs-relatorios', 'Relatorios', 'pi pi-fw pi-chart-pie', '/apps/venda-simples/relatorios', 'ENTITLEMENT', '["VENDA_SIMPLES_MODULE"]', NULL, NULL, 5, 1);

-- --------------------------------------------------
-- VENDAS > PropostaFacil
-- --------------------------------------------------
--changeset tenant:seed-menus-proposta-facil
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000260', '00000000-0000-0000-0002-000000000002', 'proposta-facil', 'PropostaFacil', 'pi pi-file-edit', NULL, 'ENTITLEMENT', '["SIGNATURE_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000261', '00000000-0000-0000-0001-000000000260', 'pf-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/proposta-facil', 'ENTITLEMENT', '["SIGNATURE_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000262', '00000000-0000-0000-0001-000000000260', 'pf-propostas', 'Propostas', 'pi pi-fw pi-file', '/apps/proposta-facil/propostas', 'ENTITLEMENT', '["SIGNATURE_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000263', '00000000-0000-0000-0001-000000000260', 'pf-templates', 'Templates', 'pi pi-fw pi-copy', '/apps/proposta-facil/templates', 'ENTITLEMENT', '["SIGNATURE_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000264', '00000000-0000-0000-0001-000000000260', 'pf-clientes', 'Clientes', 'pi pi-fw pi-users', '/apps/proposta-facil/clientes', 'ENTITLEMENT', '["SIGNATURE_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000265', '00000000-0000-0000-0001-000000000260', 'pf-relatorios', 'Relatorios', 'pi pi-fw pi-chart-pie', '/apps/proposta-facil/relatorios', 'ENTITLEMENT', '["SIGNATURE_MODULE"]', NULL, NULL, 5, 1);

-- --------------------------------------------------
-- VENDAS > ComissoesPro
-- --------------------------------------------------
--changeset tenant:seed-menus-comissoes-pro
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000270', '00000000-0000-0000-0002-000000000002', 'comissoes-pro', 'ComissoesPro', 'pi pi-percentage', NULL, 'ENTITLEMENT', '["COMISSOES_PRO_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000271', '00000000-0000-0000-0001-000000000270', 'cp-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/comissoes-pro', 'ENTITLEMENT', '["COMISSOES_PRO_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000272', '00000000-0000-0000-0001-000000000270', 'cp-vendedores', 'Vendedores', 'pi pi-fw pi-users', '/apps/comissoes-pro/vendedores', 'ENTITLEMENT', '["COMISSOES_PRO_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000273', '00000000-0000-0000-0001-000000000270', 'cp-comissoes', 'Comissoes', 'pi pi-fw pi-dollar', '/apps/comissoes-pro/comissoes', 'ENTITLEMENT', '["COMISSOES_PRO_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000274', '00000000-0000-0000-0001-000000000270', 'cp-simulador', 'Simulador', 'pi pi-fw pi-calculator', '/apps/comissoes-pro/simulador', 'ENTITLEMENT', '["COMISSOES_PRO_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000275', '00000000-0000-0000-0001-000000000270', 'cp-relatorios', 'Relatorios', 'pi pi-fw pi-chart-pie', '/apps/comissoes-pro/relatorios', 'ENTITLEMENT', '["COMISSOES_PRO_MODULE"]', NULL, NULL, 5, 1);

-- --------------------------------------------------
-- ATENDIMENTO > Help Desk
-- --------------------------------------------------
--changeset tenant:seed-menus-helpdesk
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000040', '00000000-0000-0000-0002-000000000003', 'helpdesk', 'Help Desk', 'pi pi-ticket', NULL, 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000041', '00000000-0000-0000-0001-000000000040', 'helpdesk-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/helpdesk', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000042', '00000000-0000-0000-0001-000000000040', 'helpdesk-tickets', 'Tickets', 'pi pi-fw pi-ticket', '/apps/helpdesk/tickets', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000046', '00000000-0000-0000-0001-000000000040', 'helpdesk-contacts', 'Contatos', 'pi pi-fw pi-address-book', '/apps/helpdesk/contacts', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000047', '00000000-0000-0000-0001-000000000040', 'helpdesk-chat', 'Chat', 'pi pi-fw pi-comments', '/apps/helpdesk/chat', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000043', '00000000-0000-0000-0001-000000000040', 'helpdesk-kb', 'Base de Conhecimento', 'pi pi-fw pi-book', '/apps/helpdesk/kb', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 5, 1),
('00000000-0000-0000-0001-000000000048', '00000000-0000-0000-0001-000000000040', 'helpdesk-analytics', 'Analytics', 'pi pi-fw pi-chart-line', '/apps/helpdesk/analytics', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 6, 1),
('00000000-0000-0000-0001-000000000049', '00000000-0000-0000-0001-000000000040', 'helpdesk-licenses', 'Licencas', 'pi pi-fw pi-key', '/apps/helpdesk/licenses', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 7, 1),
('00000000-0000-0000-0001-00000000005A', '00000000-0000-0000-0001-000000000040', 'helpdesk-customers', 'Clientes', 'pi pi-fw pi-building', '/apps/helpdesk/customers', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 8, 1),
('00000000-0000-0000-0001-000000000044', '00000000-0000-0000-0001-000000000040', 'helpdesk-settings', 'Configuracao', 'pi pi-fw pi-cog', '/apps/helpdesk/settings', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 9, 1),
('00000000-0000-0000-0001-000000000045', '00000000-0000-0000-0001-000000000040', 'helpdesk-portal', 'Portal do Cliente', 'pi pi-fw pi-users', '/apps/helpdesk/portal-cliente', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 10, 1),
('00000000-0000-0000-0001-000000000056', '00000000-0000-0000-0001-000000000040', 'helpdesk-escalation', 'Escalonamento', 'pi pi-fw pi-arrow-up-right', '/apps/helpdesk/escalation', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 11, 1),
('00000000-0000-0000-0001-000000000057', '00000000-0000-0000-0001-000000000040', 'helpdesk-chat-widget', 'Teste Chat Widget', 'pi pi-fw pi-desktop', '/apps/helpdesk/chat-widget-test', 'FEATURE_GATED', '["HELPDESK_MODULE"]', NULL, NULL, 12, 1);

-- --------------------------------------------------
-- ATENDIMENTO > WhatsApp
-- --------------------------------------------------
--changeset tenant:seed-menus-whatsapp
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000316', '00000000-0000-0000-0002-000000000003', 'whatsapp', 'WhatsApp', 'pi pi-whatsapp', NULL, 'ENTITLEMENT', '["WHATSAPP_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000317', '00000000-0000-0000-0001-000000000316', 'whatsapp-conversations', 'Conversas', 'pi pi-fw pi-comments', '/whatsapp/conversations', 'ENTITLEMENT', '["WHATSAPP_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000318', '00000000-0000-0000-0001-000000000316', 'whatsapp-settings', 'Configuracao', 'pi pi-fw pi-cog', '/whatsapp/settings', 'ENTITLEMENT', '["WHATSAPP_MODULE"]', NULL, NULL, 2, 1);

-- --------------------------------------------------
-- FINANCEIRO > Finance
-- --------------------------------------------------
--changeset tenant:seed-menus-finance
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000030', '00000000-0000-0000-0002-000000000004', 'finance', 'Finance', 'pi pi-wallet', NULL, 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000031', '00000000-0000-0000-0001-000000000030', 'finance-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/finance', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000032', '00000000-0000-0000-0001-000000000030', 'finance-transactions', 'Transacoes', 'pi pi-fw pi-arrows-h', '/apps/finance/transactions', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000033', '00000000-0000-0000-0001-000000000030', 'finance-settings', 'Configuracao', 'pi pi-fw pi-cog', '/apps/finance/settings', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000034', '00000000-0000-0000-0001-000000000030', 'finance-wallets', 'Carteiras', 'pi pi-fw pi-wallet', '/apps/finance/wallets', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000035', '00000000-0000-0000-0001-000000000030', 'finance-categories', 'Categorias', 'pi pi-fw pi-tags', '/apps/finance/categories', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 5, 1),
('00000000-0000-0000-0001-000000000036', '00000000-0000-0000-0001-000000000030', 'finance-credit-cards', 'Cartoes', 'pi pi-fw pi-credit-card', '/apps/finance/credit-cards', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 6, 1),
('00000000-0000-0000-0001-000000000037', '00000000-0000-0000-0001-000000000030', 'finance-budgets', 'Orcamentos', 'pi pi-fw pi-calculator', '/apps/finance/budgets', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 7, 1),
('00000000-0000-0000-0001-000000000038', '00000000-0000-0000-0001-000000000030', 'finance-persons', 'Pessoas', 'pi pi-fw pi-users', '/apps/finance/persons', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 8, 1),
('00000000-0000-0000-0001-000000000039', '00000000-0000-0000-0001-000000000030', 'finance-shared', 'Compartilhadas', 'pi pi-fw pi-share-alt', '/apps/finance/shared-accounts', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 9, 1),
('00000000-0000-0000-0001-00000000003A', '00000000-0000-0000-0001-000000000030', 'finance-income', 'Renda', 'pi pi-fw pi-dollar', '/apps/finance/income', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 10, 1),
('00000000-0000-0000-0001-00000000003B', '00000000-0000-0000-0001-000000000030', 'finance-cashflow', 'Fluxo de Caixa', 'pi pi-fw pi-chart-line', '/apps/finance/cash-flow', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 11, 1),
('00000000-0000-0000-0001-00000000003C', '00000000-0000-0000-0001-000000000030', 'finance-goals', 'Metas', 'pi pi-fw pi-flag', '/apps/finance/goals', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 12, 1),
('00000000-0000-0000-0001-00000000003D', '00000000-0000-0000-0001-000000000030', 'finance-reports', 'Relatorios', 'pi pi-fw pi-chart-pie', '/apps/finance/reports', 'FEATURE_GATED', '["FINANCE_MODULE"]', NULL, NULL, 13, 1);

-- --------------------------------------------------
-- FINANCEIRO > Finance Enterprise
-- --------------------------------------------------
--changeset tenant:seed-menus-finance-enterprise
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000330', '00000000-0000-0000-0002-000000000004', 'finance-enterprise', 'Financeiro ERP', 'pi pi-calculator', NULL, 'FEATURE_GATED', '["FINANCE_ENTERPRISE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000331', '00000000-0000-0000-0001-000000000330', 'fe-accounts', 'Plano de Contas', 'pi pi-fw pi-sitemap', '/apps/finance/enterprise/accounts', 'FEATURE_GATED', '["FINANCE_ENTERPRISE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000332', '00000000-0000-0000-0001-000000000330', 'fe-bank-accounts', 'Contas Bancarias', 'pi pi-fw pi-building', '/apps/finance/enterprise/bank-accounts', 'FEATURE_GATED', '["FINANCE_ENTERPRISE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000333', '00000000-0000-0000-0001-000000000330', 'fe-entries', 'Lancamentos', 'pi pi-fw pi-book', '/apps/finance/enterprise/entries', 'FEATURE_GATED', '["FINANCE_ENTERPRISE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000334', '00000000-0000-0000-0001-000000000330', 'fe-payables', 'Contas a Pagar', 'pi pi-fw pi-arrow-circle-up', '/apps/finance/enterprise/payables', 'FEATURE_GATED', '["FINANCE_ENTERPRISE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000335', '00000000-0000-0000-0001-000000000330', 'fe-receivables', 'Contas a Receber', 'pi pi-fw pi-arrow-circle-down', '/apps/finance/enterprise/receivables', 'FEATURE_GATED', '["FINANCE_ENTERPRISE"]', NULL, NULL, 5, 1),
('00000000-0000-0000-0001-000000000336', '00000000-0000-0000-0001-000000000330', 'fe-cashflow', 'Fluxo de Caixa', 'pi pi-fw pi-chart-line', '/apps/finance/enterprise/cash-flow', 'FEATURE_GATED', '["FINANCE_ENTERPRISE"]', NULL, NULL, 6, 1),
('00000000-0000-0000-0001-000000000337', '00000000-0000-0000-0001-000000000330', 'fe-dre', 'DRE', 'pi pi-fw pi-file', '/apps/finance/enterprise/dre', 'FEATURE_GATED', '["FINANCE_ENTERPRISE"]', NULL, NULL, 7, 1),
('00000000-0000-0000-0001-000000000338', '00000000-0000-0000-0001-000000000330', 'fe-reconciliation', 'Conciliacao', 'pi pi-fw pi-check-square', '/apps/finance/enterprise/reconciliation', 'FEATURE_GATED', '["FINANCE_ENTERPRISE"]', NULL, NULL, 8, 1);

-- --------------------------------------------------
-- FINANCEIRO > FinancasFacil
-- --------------------------------------------------
--changeset tenant:seed-menus-financas-facil
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000200', '00000000-0000-0000-0002-000000000004', 'financas-facil', 'FinancasFacil', 'pi pi-money-bill', NULL, 'ENTITLEMENT', '["FINANCE_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000201', '00000000-0000-0000-0001-000000000200', 'ff-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/financas-facil', 'ENTITLEMENT', '["FINANCE_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000202', '00000000-0000-0000-0001-000000000200', 'ff-caixa', 'Caixa', 'pi pi-fw pi-money-bill', '/apps/financas-facil/caixa', 'ENTITLEMENT', '["FINANCE_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000203', '00000000-0000-0000-0001-000000000200', 'ff-clientes', 'Clientes', 'pi pi-fw pi-users', '/apps/financas-facil/clientes', 'ENTITLEMENT', '["FINANCE_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000204', '00000000-0000-0000-0001-000000000200', 'ff-categorias', 'Categorias', 'pi pi-fw pi-tags', '/apps/financas-facil/categorias', 'ENTITLEMENT', '["FINANCE_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000205', '00000000-0000-0000-0001-000000000200', 'ff-relatorios', 'Relatorios', 'pi pi-fw pi-chart-pie', '/apps/financas-facil/relatorios', 'ENTITLEMENT', '["FINANCE_MODULE"]', NULL, NULL, 5, 1);

-- --------------------------------------------------
-- MARKETING > CMS
-- --------------------------------------------------
--changeset tenant:seed-menus-cms
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000130', '00000000-0000-0000-0002-000000000005', 'cms', 'CMS', 'pi pi-file-edit', NULL, 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000131', '00000000-0000-0000-0001-000000000130', 'cms-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/cms', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-00000000013B', '00000000-0000-0000-0001-000000000130', 'cms-conteudo', 'Conteudo', 'pi pi-fw pi-file', NULL, 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000132', '00000000-0000-0000-0001-00000000013B', 'cms-posts', 'Posts', 'pi pi-fw pi-pencil', '/apps/cms/posts', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000133', '00000000-0000-0000-0001-00000000013B', 'cms-pages', 'Paginas', 'pi pi-fw pi-copy', '/apps/cms/pages', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000134', '00000000-0000-0000-0001-00000000013B', 'cms-comments', 'Comentarios', 'pi pi-fw pi-comments', '/apps/cms/comments', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000135', '00000000-0000-0000-0001-00000000013B', 'cms-media', 'Media', 'pi pi-fw pi-images', '/apps/cms/media', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000138', '00000000-0000-0000-0001-000000000130', 'cms-marketing', 'Marketing', 'pi pi-fw pi-megaphone', '/apps/cms/marketing', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000137', '00000000-0000-0000-0001-000000000130', 'cms-settings', 'Configuracao', 'pi pi-fw pi-cog', NULL, 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-00000000013C', '00000000-0000-0000-0001-000000000137', 'cms-settings-general', 'Geral', 'pi pi-fw pi-sliders-h', '/apps/cms/settings', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000136', '00000000-0000-0000-0001-000000000137', 'cms-categories', 'Categorias', 'pi pi-fw pi-tags', '/apps/cms/categories', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000139', '00000000-0000-0000-0001-000000000137', 'cms-menus', 'Menus', 'pi pi-fw pi-bars', '/apps/cms/menus', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-00000000013A', '00000000-0000-0000-0001-000000000137', 'cms-redirects', 'Redirects', 'pi pi-fw pi-directions', '/apps/cms/redirects', 'FEATURE_GATED', '["CMS_MODULE"]', NULL, NULL, 4, 1);

-- --------------------------------------------------
-- MARKETING > ReputacaoPro
-- --------------------------------------------------
--changeset tenant:seed-menus-reputacao-pro
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000230', '00000000-0000-0000-0002-000000000005', 'reputacao-pro', 'ReputacaoPro', 'pi pi-star', NULL, 'ENTITLEMENT', '["REPUTACAO_PRO_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000231', '00000000-0000-0000-0001-000000000230', 'rp-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/reputacao-pro', 'ENTITLEMENT', '["REPUTACAO_PRO_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000232', '00000000-0000-0000-0001-000000000230', 'rp-avaliacoes', 'Avaliacoes', 'pi pi-fw pi-star-fill', '/apps/reputacao-pro/avaliacoes', 'ENTITLEMENT', '["REPUTACAO_PRO_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000233', '00000000-0000-0000-0001-000000000230', 'rp-pesquisas', 'Pesquisas', 'pi pi-fw pi-send', '/apps/reputacao-pro/pesquisas', 'ENTITLEMENT', '["REPUTACAO_PRO_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000234', '00000000-0000-0000-0001-000000000230', 'rp-relatorios', 'Relatorios', 'pi pi-fw pi-chart-pie', '/apps/reputacao-pro/relatorios', 'ENTITLEMENT', '["REPUTACAO_PRO_MODULE"]', NULL, NULL, 4, 1);

-- --------------------------------------------------
-- MARKETING > InstaMetrics
-- --------------------------------------------------
--changeset tenant:seed-menus-insta-metrics
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000250', '00000000-0000-0000-0002-000000000005', 'insta-metrics', 'InstaMetrics', 'pi pi-instagram', NULL, 'ENTITLEMENT', '["INSTA_METRICS_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000251', '00000000-0000-0000-0001-000000000250', 'im-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/insta-metrics', 'ENTITLEMENT', '["INSTA_METRICS_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000252', '00000000-0000-0000-0001-000000000250', 'im-contas', 'Contas', 'pi pi-fw pi-user', '/apps/insta-metrics/contas', 'ENTITLEMENT', '["INSTA_METRICS_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000253', '00000000-0000-0000-0001-000000000250', 'im-metricas', 'Metricas', 'pi pi-fw pi-chart-line', '/apps/insta-metrics/metricas', 'ENTITLEMENT', '["INSTA_METRICS_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000254', '00000000-0000-0000-0001-000000000250', 'im-relatorios', 'Relatorios', 'pi pi-fw pi-chart-pie', '/apps/insta-metrics/relatorios', 'ENTITLEMENT', '["INSTA_METRICS_MODULE"]', NULL, NULL, 4, 1);

-- --------------------------------------------------
-- MARKETING > FidelidadePro
-- --------------------------------------------------
--changeset tenant:seed-menus-fidelidade-pro
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000280', '00000000-0000-0000-0002-000000000005', 'fidelidade-pro', 'FidelidadePro', 'pi pi-heart', NULL, 'ENTITLEMENT', '["FIDELIDADE_PRO_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000281', '00000000-0000-0000-0001-000000000280', 'fid-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/fidelidade-pro', 'ENTITLEMENT', '["FIDELIDADE_PRO_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000282', '00000000-0000-0000-0001-000000000280', 'fid-membros', 'Membros', 'pi pi-fw pi-users', '/apps/fidelidade-pro/membros', 'ENTITLEMENT', '["FIDELIDADE_PRO_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000283', '00000000-0000-0000-0001-000000000280', 'fid-campanhas', 'Campanhas', 'pi pi-fw pi-megaphone', '/apps/fidelidade-pro/campanhas', 'ENTITLEMENT', '["FIDELIDADE_PRO_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000284', '00000000-0000-0000-0001-000000000280', 'fid-pontos', 'Pontos', 'pi pi-fw pi-star', '/apps/fidelidade-pro/pontos', 'ENTITLEMENT', '["FIDELIDADE_PRO_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000285', '00000000-0000-0000-0001-000000000280', 'fid-relatorios', 'Relatorios', 'pi pi-fw pi-chart-pie', '/apps/fidelidade-pro/relatorios', 'ENTITLEMENT', '["FIDELIDADE_PRO_MODULE"]', NULL, NULL, 5, 1);

-- --------------------------------------------------
-- GESTAO > RH
-- --------------------------------------------------
--changeset tenant:seed-menus-rh
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000080', '00000000-0000-0000-0002-000000000006', 'rh', 'RH', 'pi pi-users', NULL, 'FEATURE_GATED', '["RH_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000081', '00000000-0000-0000-0001-000000000080', 'rh-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/rh', 'FEATURE_GATED', '["RH_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000082', '00000000-0000-0000-0001-000000000080', 'rh-colaboradores', 'Colaboradores', 'pi pi-fw pi-users', '/apps/rh/colaboradores', 'FEATURE_GATED', '["RH_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000083', '00000000-0000-0000-0001-000000000080', 'rh-ponto', 'Ponto & Jornada', 'pi pi-fw pi-clock', '/apps/rh/ponto', 'FEATURE_GATED', '["RH_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000084', '00000000-0000-0000-0001-000000000080', 'rh-gestao', 'Gestao', 'pi pi-fw pi-briefcase', '/apps/rh/gestao', 'FEATURE_GATED', '["RH_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000085', '00000000-0000-0000-0001-000000000080', 'rh-settings', 'Configuracao', 'pi pi-fw pi-cog', '/apps/rh/settings', 'FEATURE_GATED', '["RH_MODULE"]', NULL, NULL, 5, 1);

-- --------------------------------------------------
-- GESTAO > BPF
-- --------------------------------------------------
--changeset tenant:seed-menus-bpf
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000060', '00000000-0000-0000-0002-000000000006', 'bpf', 'BPF', 'pi pi-shield', NULL, 'FEATURE_GATED', '["BPF_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000061', '00000000-0000-0000-0001-000000000060', 'bpf-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/bpf', 'FEATURE_GATED', '["BPF_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000062', '00000000-0000-0000-0001-000000000060', 'bpf-cadastros', 'Cadastros', 'pi pi-fw pi-users', '/apps/bpf/cadastros', 'FEATURE_GATED', '["BPF_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000063', '00000000-0000-0000-0001-000000000060', 'bpf-operacional', 'Operacional', 'pi pi-fw pi-clipboard', '/apps/bpf/operacional', 'FEATURE_GATED', '["BPF_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000064', '00000000-0000-0000-0001-000000000060', 'bpf-settings', 'Configuracao', 'pi pi-fw pi-cog', '/apps/bpf/settings', 'FEATURE_GATED', '["BPF_MODULE"]', NULL, NULL, 4, 1);

-- --------------------------------------------------
-- GESTAO > Pericias
-- --------------------------------------------------
--changeset tenant:seed-menus-pericia
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000050', '00000000-0000-0000-0002-000000000006', 'pericia', 'Pericias', 'pi pi-briefcase', NULL, 'FEATURE_GATED', '["PERICIA_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000051', '00000000-0000-0000-0001-000000000050', 'pericia-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/pericia', 'FEATURE_GATED', '["PERICIA_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000052', '00000000-0000-0000-0001-000000000050', 'pericia-processos', 'Processos', 'pi pi-fw pi-folder', '/apps/pericia/processos', 'FEATURE_GATED', '["PERICIA_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000053', '00000000-0000-0000-0001-000000000050', 'pericia-settings', 'Configuracao', 'pi pi-fw pi-cog', '/apps/pericia/settings', 'FEATURE_GATED', '["PERICIA_MODULE"]', NULL, NULL, 3, 1);

-- --------------------------------------------------
-- GESTAO > ArquiGestao
-- --------------------------------------------------
--changeset tenant:seed-menus-arquigestao
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000290', '00000000-0000-0000-0002-000000000006', 'arquitetura', 'ArquiGestao', 'pi pi-building', NULL, 'ENTITLEMENT', '["OS_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000291', '00000000-0000-0000-0001-000000000290', 'arq-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/arquitetura', 'ENTITLEMENT', '["OS_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000292', '00000000-0000-0000-0001-000000000290', 'arq-projetos', 'Projetos', 'pi pi-fw pi-briefcase', '/apps/arquitetura/projetos', 'ENTITLEMENT', '["OS_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000293', '00000000-0000-0000-0001-000000000290', 'arq-financeiro', 'Financeiro', 'pi pi-fw pi-wallet', '/apps/arquitetura/financeiro', 'ENTITLEMENT', '["OS_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000294', '00000000-0000-0000-0001-000000000290', 'arq-clientes', 'Clientes', 'pi pi-fw pi-users', '/apps/arquitetura/clientes', 'ENTITLEMENT', '["OS_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000295', '00000000-0000-0000-0001-000000000290', 'arq-arquivos', 'Arquivos', 'pi pi-fw pi-folder-open', '/apps/arquitetura/arquivos', 'ENTITLEMENT', '["OS_MODULE"]', NULL, NULL, 5, 1),
('00000000-0000-0000-0001-000000000296', '00000000-0000-0000-0001-000000000290', 'arq-agenda', 'Agenda', 'pi pi-fw pi-calendar', '/apps/arquitetura/agenda', 'ENTITLEMENT', '["OS_MODULE"]', NULL, NULL, 6, 1),
('00000000-0000-0000-0001-000000000297', '00000000-0000-0000-0001-000000000290', 'arq-os', 'Ordens de Servico', 'pi pi-fw pi-wrench', '/apps/arquitetura/os', 'ENTITLEMENT', '["OS_MODULE"]', NULL, NULL, 7, 1),
('00000000-0000-0000-0001-000000000298', '00000000-0000-0000-0001-000000000290', 'arq-fornecedores', 'Fornecedores', 'pi pi-fw pi-truck', '/apps/arquitetura/fornecedores', 'ENTITLEMENT', '["OS_MODULE"]', NULL, NULL, 8, 1);

-- --------------------------------------------------
-- GESTAO > EstoqueFacil
-- --------------------------------------------------
--changeset tenant:seed-menus-estoque-facil
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000220', '00000000-0000-0000-0002-000000000006', 'estoque-facil', 'EstoqueFacil', 'pi pi-box', NULL, 'ENTITLEMENT', '["ESTOQUE_FACIL_MODULE"]', NULL, NULL, 5, 1),
('00000000-0000-0000-0001-000000000221', '00000000-0000-0000-0001-000000000220', 'ef-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/estoque-facil', 'ENTITLEMENT', '["ESTOQUE_FACIL_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000222', '00000000-0000-0000-0001-000000000220', 'ef-produtos', 'Produtos', 'pi pi-fw pi-shopping-bag', '/apps/estoque-facil/produtos', 'ENTITLEMENT', '["ESTOQUE_FACIL_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000223', '00000000-0000-0000-0001-000000000220', 'ef-categorias', 'Categorias', 'pi pi-fw pi-tags', '/apps/estoque-facil/categorias', 'ENTITLEMENT', '["ESTOQUE_FACIL_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000224', '00000000-0000-0000-0001-000000000220', 'ef-movimentacoes', 'Movimentacoes', 'pi pi-fw pi-arrows-h', '/apps/estoque-facil/movimentacoes', 'ENTITLEMENT', '["ESTOQUE_FACIL_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000225', '00000000-0000-0000-0001-000000000220', 'ef-alertas', 'Alertas', 'pi pi-fw pi-bell', '/apps/estoque-facil/alertas', 'ENTITLEMENT', '["ESTOQUE_FACIL_MODULE"]', NULL, NULL, 5, 1);

-- --------------------------------------------------
-- STANDALONE > Simulados
-- --------------------------------------------------
--changeset tenant:seed-menus-simulados
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000120', NULL, 'simulado', 'Simulados', 'pi pi-graduation-cap', NULL, 'FEATURE_GATED', '["SIMULADO_MODULE"]', NULL, NULL, 35, 1),
('00000000-0000-0000-0001-000000000121', '00000000-0000-0000-0001-000000000120', 'simulado-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/simulado', 'FEATURE_GATED', '["SIMULADO_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000122', '00000000-0000-0000-0001-000000000120', 'simulado-questions', 'Banco de Questoes', 'pi pi-fw pi-list', '/apps/simulado/questions', 'FEATURE_GATED', '["SIMULADO_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000123', '00000000-0000-0000-0001-000000000120', 'simulado-exams', 'Simulados', 'pi pi-fw pi-file-edit', '/apps/simulado/exams', 'FEATURE_GATED', '["SIMULADO_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000124', '00000000-0000-0000-0001-000000000120', 'simulado-categories', 'Categorias', 'pi pi-fw pi-tags', '/apps/simulado/categories', 'FEATURE_GATED', '["SIMULADO_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000125', '00000000-0000-0000-0001-000000000120', 'simulado-results', 'Resultados', 'pi pi-fw pi-check-circle', '/apps/simulado/results', 'FEATURE_GATED', '["SIMULADO_MODULE"]', NULL, NULL, 5, 1),
('00000000-0000-0000-0001-000000000126', '00000000-0000-0000-0001-000000000120', 'simulado-certificates', 'Certificados', 'pi pi-fw pi-verified', '/apps/simulado/certificates', 'FEATURE_GATED', '["SIMULADO_MODULE"]', NULL, NULL, 6, 1);

-- --------------------------------------------------
-- STANDALONE > LGPD
-- --------------------------------------------------
--changeset tenant:seed-menus-lgpd
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000140', NULL, 'lgpd', 'LGPD & Privacidade', 'pi pi-lock', NULL, 'ENTITLEMENT', '["LGPD_MODULE"]', NULL, NULL, 40, 1),
('00000000-0000-0000-0001-000000000141', '00000000-0000-0000-0001-000000000140', 'lgpd-consents', 'Consentimentos', 'pi pi-fw pi-check-square', '/apps/lgpd', 'ENTITLEMENT', '["LGPD_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000142', '00000000-0000-0000-0001-000000000140', 'lgpd-requests', 'Solicitacoes', 'pi pi-fw pi-inbox', '/apps/lgpd/requests', 'ENTITLEMENT', '["LGPD_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000143', '00000000-0000-0000-0001-000000000140', 'lgpd-ropa', 'ROPA', 'pi pi-fw pi-file-edit', '/apps/lgpd/ropa', 'FEATURE_GATED', '["LGPD_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000144', '00000000-0000-0000-0001-000000000140', 'lgpd-incidents', 'Incidentes', 'pi pi-fw pi-exclamation-triangle', '/apps/lgpd/incidents', 'FEATURE_GATED', '["LGPD_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000145', '00000000-0000-0000-0001-000000000140', 'lgpd-retention', 'Retencao', 'pi pi-fw pi-clock', '/apps/lgpd/retention', 'FEATURE_GATED', '["LGPD_MODULE"]', NULL, NULL, 5, 1);

-- --------------------------------------------------
-- STANDALONE > 101 Lar
-- --------------------------------------------------
--changeset tenant:seed-menus-lar
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000160', NULL, 'lar', '101 Lar', 'pi pi-home', NULL, 'ENTITLEMENT', '["LAR_MODULE"]', NULL, NULL, 45, 1),
('00000000-0000-0000-0001-000000000161', '00000000-0000-0000-0001-000000000160', 'lar-family', 'Familia', 'pi pi-fw pi-users', '/apps/lar', 'ENTITLEMENT', '["LAR_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000162', '00000000-0000-0000-0001-000000000160', 'lar-outings', 'Saidas', 'pi pi-fw pi-map', '/apps/lar/outings', 'ENTITLEMENT', '["LAR_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000163', '00000000-0000-0000-0001-000000000160', 'lar-health', 'Saude', 'pi pi-fw pi-heart', '/apps/lar/health', 'ENTITLEMENT', '["LAR_MODULE"]', NULL, NULL, 3, 1),
('00000000-0000-0000-0001-000000000164', '00000000-0000-0000-0001-000000000160', 'lar-shopping', 'Compras', 'pi pi-fw pi-shopping-cart', '/apps/lar/shopping', 'ENTITLEMENT', '["LAR_MODULE"]', NULL, NULL, 4, 1),
('00000000-0000-0000-0001-000000000165', '00000000-0000-0000-0001-000000000160', 'lar-bills', 'Contas', 'pi pi-fw pi-wallet', '/apps/lar/bills', 'ENTITLEMENT', '["LAR_MODULE"]', NULL, NULL, 5, 1),
('00000000-0000-0000-0001-000000000166', '00000000-0000-0000-0001-000000000160', 'lar-chores', 'Tarefas', 'pi pi-fw pi-check-square', '/apps/lar/chores', 'ENTITLEMENT', '["LAR_MODULE"]', NULL, NULL, 6, 1),
('00000000-0000-0000-0001-000000000167', '00000000-0000-0000-0001-000000000160', 'lar-documents', 'Documentos', 'pi pi-fw pi-folder', '/apps/lar/documents', 'ENTITLEMENT', '["LAR_MODULE"]', NULL, NULL, 7, 1);

-- --------------------------------------------------
-- STANDALONE > CodeGen
-- --------------------------------------------------
--changeset tenant:seed-menus-codegen
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000091', NULL, 'codegen', 'CodeGen', 'pi pi-code', NULL, 'FEATURE_GATED', '["CODEGEN_MODULE"]', NULL, NULL, 50, 1),
('00000000-0000-0000-0001-000000000092', '00000000-0000-0000-0001-000000000091', 'codegen-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/codegen', 'FEATURE_GATED', '["CODEGEN_MODULE"]', NULL, NULL, 1, 1),
('00000000-0000-0000-0001-000000000093', '00000000-0000-0000-0001-000000000091', 'codegen-templates', 'Templates', 'pi pi-fw pi-file-edit', '/apps/codegen/templates', 'FEATURE_GATED', '["CODEGEN_MODULE"]', NULL, NULL, 2, 1),
('00000000-0000-0000-0001-000000000094', '00000000-0000-0000-0001-000000000091', 'codegen-settings', 'Configuracao', 'pi pi-fw pi-cog', '/apps/codegen/settings', 'FEATURE_GATED', '["CODEGEN_MODULE"]', NULL, NULL, 3, 1);

-- --------------------------------------------------
-- PLATAFORMA (admin root + direct children)
-- --------------------------------------------------
--changeset tenant:seed-menus-plataforma
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000099', NULL, 'plataforma', 'Plataforma', 'pi pi-cog', NULL, 'SUPER_ADMIN', NULL, NULL, '["PLATFORM_ADMIN"]', 90, 1),
('00000000-0000-0000-0001-000000000101', '00000000-0000-0000-0001-000000000099', 'admin-dashboard', 'Dashboard', 'pi pi-fw pi-chart-bar', '/apps/tenant', 'ADMIN', NULL, '["admin", "manager"]', NULL, 1, 1),
('00000000-0000-0000-0001-000000000150', '00000000-0000-0000-0001-000000000099', 'billing-dashboard', 'Financeiro', 'pi pi-fw pi-credit-card', '/apps/tenant/billing', 'ADMIN', NULL, '["admin", "manager"]', NULL, 2, 1),
('00000000-0000-0000-0001-000000000104', '00000000-0000-0000-0001-000000000099', 'admin-catalog', 'Catalogo', 'pi pi-fw pi-box', '/apps/tenant/catalog', 'ADMIN', NULL, '["admin", "manager"]', NULL, 3, 1),
('00000000-0000-0000-0001-000000000107', '00000000-0000-0000-0001-000000000099', 'admin-menus', 'Menus', 'pi pi-fw pi-bars', '/apps/tenant/menus/manage', 'SUPER_ADMIN', NULL, NULL, '["PLATFORM_ADMIN"]', 4, 1),
('00000000-0000-0000-0001-000000000603', '00000000-0000-0000-0001-000000000099', 'system-status', 'System Status', 'pi pi-fw pi-server', '/status', 'PLATFORM_ONLY', '["PLATFORM_ACCESS"]', NULL, NULL, 5, 1),
('00000000-0000-0000-0001-000000000604', '00000000-0000-0000-0001-000000000099', 'purchase-diagnostics', 'Purchase Diagnostics', 'pi pi-fw pi-search', '/apps/tools/purchase-diagnostics', 'SUPER_ADMIN', NULL, NULL, '["PLATFORM_ADMIN"]', 6, 1);

-- Hidden: Platform Admin group, Financeiro group, billing sub-menus, team/cleanup (accessed via dashboard)
--changeset tenant:seed-menus-platform-hidden
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000100', '00000000-0000-0000-0001-000000000099', 'platform-admin', 'Platform Admin', 'pi pi-cog', NULL, 'ADMIN', NULL, '["admin", "manager"]', NULL, 99, 0),
('00000000-0000-0000-0001-000000000105', '00000000-0000-0000-0001-000000000099', 'admin-billing', 'Financeiro', 'pi pi-fw pi-credit-card', NULL, 'ADMIN', NULL, '["admin", "manager"]', NULL, 99, 0),
('00000000-0000-0000-0001-000000000151', '00000000-0000-0000-0001-000000000150', 'billing-admin', 'Gestao', 'pi pi-fw pi-chart-line', '/apps/tenant/billing/admin', 'ADMIN', NULL, '["admin"]', NULL, 2, 0),
('00000000-0000-0000-0001-000000000152', '00000000-0000-0000-0001-000000000150', 'billing-reseller', 'Revenda', 'pi pi-fw pi-users', '/apps/tenant/billing/reseller', 'ADMIN', NULL, '["admin", "manager"]', NULL, 3, 0),
('00000000-0000-0000-0001-000000000153', '00000000-0000-0000-0001-000000000150', 'billing-my', 'Meu Financeiro', 'pi pi-fw pi-wallet', '/apps/tenant/billing/my', 'ADMIN', NULL, '["admin", "manager"]', NULL, 4, 0),
('00000000-0000-0000-0001-000000000111', '00000000-0000-0000-0001-000000000099', 'settings-team', 'Team Management', 'pi pi-fw pi-user-edit', '/settings/team', 'SUPER_ADMIN', NULL, NULL, '["PLATFORM_ADMIN"]', 99, 0),
('00000000-0000-0000-0001-000000000112', '00000000-0000-0000-0001-000000000099', 'settings-cleanup', 'Data Cleanup', 'pi pi-fw pi-trash', '/settings/admin/cleanup', 'SUPER_ADMIN', NULL, NULL, '["PLATFORM_ADMIN"]', 99, 0);

-- --------------------------------------------------
-- PORTAL DO CLIENTE (all hidden)
-- --------------------------------------------------
--changeset tenant:seed-menus-portal
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00400', NULL, 'portal', 'Portal do Cliente', 'pi pi-home', NULL, 'CUSTOMER', NULL, NULL, '["PORTAL_ACCESS"]', 400, 0),
('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00401', 'd0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00400', 'portal-dashboard', 'Dashboard', 'pi pi-th-large', '/portal', 'CUSTOMER', NULL, NULL, '["PORTAL_ACCESS"]', 401, 0),
('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00402', 'd0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00400', 'portal-tickets', 'Meus Chamados', 'pi pi-ticket', '/portal/tickets', 'CUSTOMER', '["HELPDESK_MODULE"]', NULL, '["PORTAL_TICKETS"]', 402, 0),
('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00403', 'd0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00400', 'portal-orders', 'Meus Pedidos', 'pi pi-shopping-cart', '/portal/orders', 'CUSTOMER', '["ECOMMERCE_CATALOG"]', NULL, '["PORTAL_ORDERS"]', 403, 0),
('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00404', 'd0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00400', 'portal-appointments', 'Agendamentos', 'pi pi-calendar', '/portal/appointments', 'CUSTOMER', '["AGENDA_MODULE"]', NULL, '["PORTAL_APPOINTMENTS"]', 404, 0),
('d0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00405', 'd0d0d0d0-d0d0-d0d0-d0d0-d0d0d0d00400', 'portal-profile', 'Meu Perfil', 'pi pi-user', '/portal/profile', 'CUSTOMER', NULL, NULL, '["PORTAL_PROFILE"]', 405, 0);

-- --------------------------------------------------
-- LEGACY HIDDEN (parent groups no longer used)
-- --------------------------------------------------
--changeset tenant:seed-menus-legacy-hidden
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000010', NULL, 'apps', 'Apps', 'pi pi-th-large', NULL, 'ENTITLEMENT', '["KANBAN_MODULE", "FILES_MODULE"]', NULL, NULL, 10, 0),
('00000000-0000-0000-0001-000000000025', NULL, 'products', 'Products', 'pi pi-box', NULL, 'FEATURE_GATED', '["FINANCE_MODULE", "FINANCE_ENTERPRISE", "HELPDESK_MODULE", "PERICIA_MODULE", "BPF_MODULE", "RH_MODULE", "CHAT_MODULE", "SIMULADO_MODULE", "CMS_MODULE", "CODEGEN_MODULE"]', NULL, NULL, 25, 0);

-- --------------------------------------------------
-- PURCHASE DIAGNOSTICS (platform tool)
-- --------------------------------------------------
--changeset tenant:seed-menu-purchase-diagnostics
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000604', '00000000-0000-0000-0001-000000000099', 'purchase-diagnostics', 'Purchase Diagnostics', 'pi pi-fw pi-search', '/apps/tools/purchase-diagnostics', 'SUPER_ADMIN', NULL, NULL, '["PLATFORM_ADMIN"]', 6, 1);

-- --------------------------------------------------
-- ECOMMERCE ADMIN SETTINGS MENU ITEM
-- --------------------------------------------------
--changeset tenant:seed-menu-ecom-admin-settings
INSERT IGNORE INTO TNT_MENU_ITEMS (ID, PARENT_ID, MENU_KEY, LABEL, ICON, ROUTE, CATEGORY, FEATURE_CODES, ROLES, PERMISSIONS, ORDER_INDEX, VISIBLE) VALUES
('00000000-0000-0000-0001-000000000339', '00000000-0000-0000-0001-000000000330', 'ecom-admin-settings', 'Configuracoes', 'pi pi-fw pi-cog', '/ecommerce/admin/settings', 'ENTITLEMENT', '["ECOMMERCE_CATALOG"]', '["admin"]', NULL, 9, 1);
