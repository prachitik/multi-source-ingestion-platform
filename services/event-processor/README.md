## Overview
event-processor is a core backend microservice responsible for consuming events from Kafka, validating them, persisting raw data, and performing processor-level normalization to transform source-specific payloads into a unified domain model.
This service enables reliable, consistent, and queryable data across heterogeneous ingestion sources.

## Responsibilities
- Consume events from Kafka (events.ingest.v1)

- Validate event structure using EventEnvelopeValidator

- Persist raw events (raw_events)

- Maintain ingestion lifecycle (ingestion_receipts)

- Normalize events for supported sources (currently WS_ACTIVITY)

- Persist normalized data (normalized_activity_events)

- Ensure idempotency and safe retries



## Data Flow
Kafka Topic (events.ingest.v1)       
        ↓  
EventEnvelope        
        ↓  
Validation       
        ↓
Persist raw_events        
        ↓  
Normalize (if applicable)       
        ↓  
Persist normalized_activity_events        
        ↓  
Update ingestion_receipts

### Input Event (EventEnvelope)
All events consumed are already structurally normalized:  
{  "tenantId": "tenant-a",  "source": "WS_ACTIVITY",  "eventType": "steps.updated",  "eventId": "act-123",  "occurredAt": "...",  "ingestedAt": "...",  "payload": { ... }}

### Normalization Logic
Why normalization?  
Different sources may send semantically identical data in different formats.
Processor-level normalization converts source-specific payloads into a canonical domain model.

Example
- Input (ACTIVITY_WS)
```{  "eventType": "steps.updated", 
       "payload": {
            "userId": "user-1",
            "deviceId": "device-2",
            "steps": 834,
            "distanceMeters": 420
        }
    }
 ```
- Output (Normalized)
```{  "tenantId": "tenant-a",
       "eventType": "steps.updated",
       "userId": "user-1",
       "deviceId": "device-2",
       "steps": 834,
       "distanceMeters": 420
    }
```

## Database Tables
1. raw_events  
   Stores original events exactly as received.

2. normalized_activity_events  
   Stores processed, structured data for querying.
   ```Key fields:
   tenant_id
   event_id 
   event_type 
   user_id
   device_id
   steps
   heart_rate
   calories
   sleep_minutes
   ...
   ```

3. ingestion_receipts  
   Tracks lifecycle of each event:
   ```ACCEPTED → PROCESSED / DUPLICATE / FAILED```

## Supported Sources   
WS_ACTIVITY - Supported  
Others - Not yet implemented

## Idempotency

- Duplicate events are safely ignored using:
ON CONFLICT (tenant_id, event_id)

- Ensures at-least-once delivery safety


## Error Handling


- Failed events:  
    - marked as FAILED in ingestion_receipts  
    - not acknowledged → Kafka retries

- Future improvement: Dead Letter Queue (DLQ)


## Configuration
spring:
    kafka:    
        bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:kafka:9092}
app:  
    kafka:    
        topic: events.ingest.v1
 
## How to Run
1. Start infrastructure
   ``` docker compose up -d ```

2. Run event-processor locally
   ```POSTGRES_HOST=localhost POSTGRES_PORT=5433 \KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \mvn -pl services/event-processor spring-boot:run```

3. Trigger ingestion (via connector)
   ```curl -X POST http://localhost:8085/v1/tenants/tenant-a/ws-activity \  -H "Content-Type: application/json" \  -d '{}'```

4. Verify data
   ```docker exec -it postgres psql -U ingestion -d ingestion```
   Raw events:
   ```SELECT * FROM raw_events ORDER BY created_at DESC LIMIT 10;```
   Normalized events:
   ```SELECT * FROM normalized_activity_events ORDER BY normalized_at DESC LIMIT 10;```

## Design Highlights

- Separation of concerns:
    - ingestion services → data intake
    - processor → validation + normalization
- Supports heterogeneous data sources
- Extensible normalization strategy
- Kafka-based decoupling
- Idempotent processing model


## Future Enhancements
- Support additional sources (API, batch)
- Event-type specific normalization logic
- Schema validation
- Dead Letter Queue (DLQ)
- Metrics (Micrometer + Grafana)
- Retry policies and backoff tuning
