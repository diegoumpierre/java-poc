--liquibase formatted sql

-- ============================================================================
-- TENANT SERVICE - Metrics Tables (TNT_MTR_*)
-- ============================================================================

-- ===========================================================================
-- TNT_MTR_USAGE_METRICS
-- ===========================================================================

--changeset tenant:schema-mtr-usage-metrics
CREATE TABLE IF NOT EXISTS TNT_MTR_USAGE_METRICS (
    ID CHAR(36) NOT NULL,
    TENANT_ID CHAR(36),
    METRIC_TYPE VARCHAR(50),
    METRIC_VALUE DECIMAL(15,2) DEFAULT 0,
    PERIOD_TYPE VARCHAR(20),
    PERIOD_START DATE,
    PERIOD_END DATE,
    PRODUCT_ID CHAR(36),
    METADATA TEXT,
    CREATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    UPDATED_AT TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (ID),
    INDEX IDX_TNT_MTR_USAGE_METRICS_TENANT (TENANT_ID),
    INDEX IDX_TNT_MTR_USAGE_METRICS_TENANT_TYPE (TENANT_ID, METRIC_TYPE),
    INDEX IDX_TNT_MTR_USAGE_METRICS_TENANT_PERIOD (TENANT_ID, PERIOD_START, PERIOD_END),
    UNIQUE KEY UK_TNT_MTR_USAGE_METRICS_TENANT_TYPE_PERIOD (TENANT_ID, METRIC_TYPE, PERIOD_START, PERIOD_END),
    INDEX IDX_TNT_MTR_USAGE_METRICS_TYPE (METRIC_TYPE),
    INDEX IDX_TNT_MTR_USAGE_METRICS_PERIOD_END (PERIOD_END)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
