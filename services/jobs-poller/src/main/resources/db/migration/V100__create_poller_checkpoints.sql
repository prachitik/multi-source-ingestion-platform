CREATE TABLE IF NOT EXISTS poller_checkpoints (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    source_name VARCHAR(100) NOT NULL,
    cursor_value TEXT,
    last_polled_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_poller_checkpoint UNIQUE (tenant_id, source_name)
);