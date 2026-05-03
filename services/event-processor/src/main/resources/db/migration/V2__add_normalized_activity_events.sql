CREATE TABLE IF NOT EXISTS normalized_activity_events (
    id BIGSERIAL PRIMARY KEY,

    tenant_id VARCHAR(100) NOT NULL,
    event_id VARCHAR(255) NOT NULL,
    source VARCHAR(50) NOT NULL,
    event_type VARCHAR(100) NOT NULL,

    user_id VARCHAR(100),
    device_id VARCHAR(100),

    steps INTEGER,
    distance_meters INTEGER,
    heart_rate INTEGER,
    calories INTEGER,
    active_minutes INTEGER,
    sleep_minutes INTEGER,
    sleep_quality VARCHAR(50),

    occurred_at TIMESTAMP NOT NULL,
    ingested_at TIMESTAMP NOT NULL,
    normalized_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    raw_event_id BIGINT,

    CONSTRAINT uq_normalized_activity_event UNIQUE (tenant_id, event_id)
);