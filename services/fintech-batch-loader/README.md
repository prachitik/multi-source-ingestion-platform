# Fintech Batch Loader (NDJSON Ingestion)

## Overview

The **Fintech Batch Loader** is a batch ingestion component of the Multi-Source Ingestion Platform.
It processes NDJSON files containing FINTECH events and sends them to the ingestion pipeline via the ingest API.

This loader simulates real-world upstream systems that produce event files for ingestion.

---

## Architecture

```
NDJSON File
   ↓
Fintech Batch Loader (this module)
   ↓
Nginx Load Balancer (http://localhost:8088)
   ↓
Ingest API (multiple instances)
   ↓
Kafka
   ↓
Event Processor
   ↓
Postgres
   ↓
Query API
```

---

## Features

* Reads NDJSON files line-by-line (streaming, memory efficient)
* Batches events and sends to ingest API
* Tracks file ingestion metadata in Postgres
* Supports partial failures (malformed lines skipped)
* Moves files through lifecycle directories:

    * `incoming → processing → processed`
* Designed for future extensibility (DLQ, S3, watcher)

---

## File Lifecycle

```
data/fintech/
  ├── incoming/
  ├── processing/
  └── processed/
```

Flow:

1. File is placed in `incoming/`
2. Loader moves it to `processing/`
3. Processes line-by-line
4. Sends events in batches
5. Moves file to `processed/` after completion

---

## NDJSON Format

Each line must be a valid JSON object:

```json
{"eventId":"ft_1001","eventType":"PAYMENT_INITIATED","occurredAt":"2026-04-09T18:00:00Z","schemaVersion":1,"payload":{"accountId":"A1","amount":120.50},"correlationId":"corr-1001"}
```

---

## Configuration

`application.yml`:

```yaml
loader:
  tenant-id: tenant_fintech
  batch-size: 100
  ingest-api-base-url: http://localhost:8088
  incoming-dir: ../../data/fintech/incoming
  processing-dir: ../../data/fintech/processing
  processed-dir: ../../data/fintech/processed

spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/ingestion
    username: ingestion
    password: ingestion
```

---

## File Metadata Tracking

Each processed file is recorded in the database:

Table: `file_ingestion_runs`

| Field                 | Description                  |
| --------------------- | ---------------------------- |
| file_id               | Unique identifier            |
| file_name             | File name                    |
| tenant_id             | Tenant                       |
| source                | FINTECH                      |
| received_at           | File detected time           |
| processing_started_at | Processing start             |
| processed_at          | Completion time              |
| total_lines           | Total lines read             |
| accepted              | Successfully ingested events |
| failed                | Failed/malformed lines       |
| status                | Processing status            |
| error_message         | Failure reason               |

### Status Values

* `RECEIVED`
* `PROCESSING`
* `PROCESSED`
* `PROCESSED_WITH_ERRORS`
* `FAILED`

---

## Running the Loader

### 1. Start Docker services

```bash
docker compose up -d
```

### 2. Place NDJSON file

```bash
mkdir -p data/fintech/incoming
```

Add file:

```
data/fintech/incoming/sample.ndjson
```

### 3. Run loader

```bash
mvn -pl services/fintech-batch-loader spring-boot:run
```

---

## Verification

### Check file movement

```
incoming → processing → processed
```

### Check DB

```sql
SELECT file_name, status, total_lines, accepted, failed
FROM file_ingestion_runs;
```

### Check query API

```bash
curl "http://localhost:8090/v1/tenants/tenant_fintech/events?limit=10"
```

---

## Error Handling

* Malformed JSON lines are skipped
* Failed lines are counted in `failed`
* File still completes with `PROCESSED_WITH_ERRORS`

---

## Future Enhancements

* Directory watcher (auto-detect new files)
* Dead Letter Queue (DLQ) for malformed records
* S3-based ingestion
* Retry mechanism for failed batches
* Progress tracking and metrics

---

## Design Highlights

* Streaming file processing (no full file load)
* Clear separation of concerns (client, service, metadata)
* Extensible architecture for future ingestion sources
* Production-like file lifecycle management

---

## Notes

This module demonstrates real-world ingestion patterns:

* batch processing
* file lifecycle management
* event-driven architecture integration

It is designed to mimic how financial systems ingest transaction/event files at scale.
