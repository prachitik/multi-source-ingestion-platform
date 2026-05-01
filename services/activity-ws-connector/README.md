## activity-ws-connector
### Overview
activity-ws-connector is a streaming ingestion microservice that connects to a live WebSocket data source, transforms incoming messages into a standardized event format, and publishes them to Kafka.
This service enables real-time ingestion as part of the multi-source ingestion platform.

### Responsibilities


- Establish WebSocket connection to live feed


- Continuously receive streaming data


- Parse incoming JSON messages


- Transform into EventEnvelope


- Publish events to Kafka (events.ingest.v1)


- Handle connection lifecycle (connect, error, reconnect)



### Data Flow
WebSocket Feed (activity-ws-server or external source)  
            ↓  
Raw ActivityMessage (JSON)         
            ↓  
Transform → EventEnvelope  
            ↓  
Kafka Topic (events.ingest.v1)

### API
- Start WebSocket ingestion
POST /v1/tenants/{tenantId}/ws-activity
- Example  
```curl -X POST http://localhost:8085/v1/tenants/tenant-a/ws-activity \  -H "Content-Type: application/json" \  -d '{}' ```
- Response  
```{  "tenantId": "tenant-a",  "status": "CONNECTING",  "feedUrl": "ws://localhost:8084/ws/activity-feed",  "message": "WS activity connector started"}```

- Configuration:   
application.yml
server:  port: 8085ws-activity:  default-feed-url: ws://localhost:8084/ws/activity-feed  topic: events.ingest.v1  initial-backoff-seconds: 1  max-backoff-seconds: 30spring:  kafka:    bootstrap-servers: localhost:9092

- Reconnect Strategy:  
The connector implements exponential backoff for reconnection:
Attempt 1 → 1 sec  Attempt 2 → 2 sec  Attempt 3 → 4 sec  Attempt 4 → 8 sec  ... capped at 30 sec
Triggers:

  - connection failure
  - transport error
  - connection closed



- Event Transformation
Incoming WebSocket messages are transformed into a unified format:
Input (ActivityMessage)
{  "activityId": "act-123",  "type": "steps.updated",  "userId": "user-1",  "deviceId": "device-2",  "occurredAt": "...",  "payload": { ... }}
- Output (EventEnvelope)
{  "tenantId": "tenant-a",  "source": "WS_ACTIVITY",  "eventType": "steps.updated",  "eventId": "act-123",  "occurredAt": "...",  "ingestedAt": "...",  "schemaVersion": 1,  "payload": { ... },  "correlationId": "..."}

### How to Run
1. Start infrastructure
   docker compose up -d

2. Run services
```# WebSocket mock server  
mvn -pl services/activity-ws-server spring-boot:run  
# Connector 
mvn -pl services/activity-ws-connector spring-boot:run
```

3. Start ingestion
   ``` 
   curl -X POST http://localhost:8085/v1/tenants/tenant-a/ws-activity \  -H "Content-Type: application/json" \  -d '{}'
   ```

4. Verify logs
   ```
   WS connected: tenantId=tenant-a
   WS message processed
   Kafka publish success
   ```

#### Important Networking Note
For hybrid setup (Docker + local services):
Component Kafka Address
Local services localhost:9092
Docker services kafka:29092
Kafka must be configured with dual listeners.

### Future Enhancements


- Stop endpoint:
DELETE /v1/tenants/{tenantId}/ws-activity


- Status endpoint:
GET /v1/tenants/{tenantId}/ws-activity


- Authentication support for WebSocket feeds


- Message validation + DLQ


- Metrics (connection health, throughput)


- Multi-tenant connection scaling



### Design Notes


- One active WebSocket connection per tenant


- Connector directly publishes to Kafka (no ingest-api hop)


- Separation of concerns:


- activity-ws-server → source simulator


- activity-ws-connector → ingestion client


