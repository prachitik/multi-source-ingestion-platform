## activity-ws-server

Mock WebSocket server that simulates a live activity feed.

### Endpoint
ws://localhost:8084/ws/activity-feed

### Features
- Generates synthetic activity events every 3 seconds
- Supports multiple concurrent WebSocket clients
- Broadcasts events to all connected clients
- Used for testing websocket ingestion in activity-ws-connector

### Sample Event
{
"activityId": "act-123",
"type": "steps.updated",
"userId": "user-1",
"deviceId": "device-2",
"occurredAt": "...",
"payload": { ... }
}