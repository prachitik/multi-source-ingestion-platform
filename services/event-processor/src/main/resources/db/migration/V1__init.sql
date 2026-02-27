-- Raw events: store the full envelope (JSON) + key metadata
CREATE TABLE IF NOT EXISTS raw_events (
  id BIGSERIAL PRIMARY KEY,
  tenant_id TEXT NOT NULL,
  event_id TEXT NOT NULL,
  source TEXT NOT NULL,
  event_type TEXT NOT NULL,
  occurred_at TIMESTAMPTZ NOT NULL,
  ingested_at TIMESTAMPTZ NOT NULL,
  correlation_id TEXT NOT NULL,
  envelope_json JSONB NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (tenant_id, event_id)
);

-- Ingestion receipts: track processing status per tenant/event
CREATE TABLE IF NOT EXISTS ingestion_receipts (
  tenant_id TEXT NOT NULL,
  event_id TEXT NOT NULL,
  status TEXT NOT NULL, -- ACCEPTED/PROCESSED/DUPLICATE/FAILED
  last_error TEXT,
  first_seen_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (tenant_id, event_id)
);

CREATE INDEX IF NOT EXISTS idx_raw_events_tenant_created ON raw_events (tenant_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_raw_events_tenant_type_time ON raw_events (tenant_id, event_type, occurred_at DESC);