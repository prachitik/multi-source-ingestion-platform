### multi-source-ingestion-platform

# Multi-Source Tenant-Aware Ingestion Platform

A distributed, multi-source event ingestion system designed to process, store, and analyze high-throughput data streams across multiple tenants.

- This project simulates industry like ingestion architecture handling diverse data sources including:
  1. HTTP streaming telemetry
  2. Ecommerce activity events
  3. Fintech batch files (NDJSON)
  4. Live WebSocket activity feeds
  5. Cursor-based job listings poller

- All ingestion paths publish events into a unified Kafka topic and are processed by horizontally scalable consumer services. The system ensures:

  - At-least-once event processing

  - Idempotent persistence

  - Tenant-level isolation

  - Dead Letter Queue (DLQ) with replay capability

  - Basic analytics and time-series queries

### Architecture Overview

The system follows a hybrid ingestion architecture:

- External producers → Load Balancer → Ingest API → Kafka

- Internal connectors (WebSocket, Poller, Batch Loader) → Kafka directly

- Kafka → Consumer Group (Event Processor instances)

- Postgres → Raw events, processed data, and ingestion metadata

- Query API → Tenant-scoped analytics

This project is built to demonstrate real-world distributed system concepts including:

- Partitioning strategy (key = tenantId)

- Consumer group scaling

- Fault tolerance and DLQ handling

- Cursor-based polling

- Idempotency guarantees

- Multi-tenant data modeling

### Why This Project?

The goal is to design and implement a scalable ingestion platform from scratch using:

Java 21 + Spring Boot

Apache Kafka

PostgreSQL

Docker / Docker Compose

This repository follows a monorepo structure while maintaining independently deployable services.
