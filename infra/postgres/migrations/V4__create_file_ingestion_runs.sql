CREATE TABLE file_ingestion_runs (
    file_id UUID PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    source VARCHAR(50) NOT NULL,
    file_name VARCHAR(500) NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processing_started_at TIMESTAMP,
    processed_at TIMESTAMP,
    total_lines INT NOT NULL DEFAULT 0,
    accepted INT NOT NULL DEFAULT 0,
    failed INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    error_message TEXT
);