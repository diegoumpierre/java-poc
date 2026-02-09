-- Script para criar tabelas do Kanban Service
-- Executar no banco de dados 101_softwares

USE 101_softwares;

-- Tabela: kanban_boards
CREATE TABLE IF NOT EXISTS kanban_boards (
    id CHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    user_id CHAR(36) NOT NULL,
    board_code VARCHAR(10),
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_kanban_boards_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela: kanban_lists
CREATE TABLE IF NOT EXISTS kanban_lists (
    id CHAR(36) NOT NULL PRIMARY KEY,
    board_id CHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    position INT DEFAULT 0,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_kanban_lists_board FOREIGN KEY (board_id) REFERENCES kanban_boards(id) ON DELETE CASCADE
);

-- Tabela: kanban_cards
CREATE TABLE IF NOT EXISTS kanban_cards (
    id CHAR(36) NOT NULL PRIMARY KEY,
    list_id CHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    due_date DATE,
    completed BOOLEAN DEFAULT FALSE,
    progress INT DEFAULT 0,
    position INT DEFAULT 0,
    priority_color VARCHAR(50),
    priority_title VARCHAR(100),
    attachments INT DEFAULT 0,
    card_number INT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_kanban_cards_list FOREIGN KEY (list_id) REFERENCES kanban_lists(id) ON DELETE CASCADE
);

-- Tabela: kanban_card_assignees
CREATE TABLE IF NOT EXISTS kanban_card_assignees (
    card_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    CONSTRAINT fk_kanban_assignees_card FOREIGN KEY (card_id) REFERENCES kanban_cards(id) ON DELETE CASCADE,
    CONSTRAINT fk_kanban_assignees_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela: kanban_comments
CREATE TABLE IF NOT EXISTS kanban_comments (
    id CHAR(36) NOT NULL PRIMARY KEY,
    card_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_kanban_comments_card FOREIGN KEY (card_id) REFERENCES kanban_cards(id) ON DELETE CASCADE,
    CONSTRAINT fk_kanban_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabela: kanban_subtasks
CREATE TABLE IF NOT EXISTS kanban_subtasks (
    id CHAR(36) NOT NULL PRIMARY KEY,
    card_id CHAR(36) NOT NULL,
    text VARCHAR(500) NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    position INT DEFAULT 0,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_kanban_subtasks_card FOREIGN KEY (card_id) REFERENCES kanban_cards(id) ON DELETE CASCADE
);

-- Tabela: kanban_attachments
CREATE TABLE IF NOT EXISTS kanban_attachments (
    id CHAR(36) NOT NULL PRIMARY KEY,
    card_id CHAR(36) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_kanban_attachments_card FOREIGN KEY (card_id) REFERENCES kanban_cards(id) ON DELETE CASCADE
);

-- Verificação
SELECT 'Tabelas do Kanban criadas com sucesso!' AS status;

-- Listar tabelas criadas
SHOW TABLES LIKE 'kanban_%';
