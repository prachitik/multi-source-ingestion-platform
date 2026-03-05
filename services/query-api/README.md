# query-api

Query/read layer for the **Multi-Source Ingestion Platform**.

## Tech Stack
- Java + Spring Boot
- Maven
- PostgreSQL
- Actuator (health/info)
- OpenAPI/Swagger (springdoc)

## Local Setup

### Prerequisites
- Java 17 (recommended)
- Maven 3.8+
- PostgreSQL (local or Docker)

### Configuration
This service reads Postgres configuration from environment variables (with defaults):

| Variable | Default |
|---|---|
| POSTGRES_HOST | postgres |
| POSTGRES_PORT | 5432 |
| POSTGRES_DB | ingestion |
| POSTGRES_USER | ingestion |
| POSTGRES_PASSWORD | ingestion |

Your JDBC URL resolves to:
`jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}`

### Run locally
```bash
cd query-api
mvn clean spring-boot:run
```

Service runs on: http://localhost:8090

### API Documentation (Swagger)

When the service is running:

- Swagger UI: http://localhost:8090/swagger-ui

- OpenAPI spec: http://localhost:8090/v3/api-docs

### Health / Info

- Health: http://localhost:8090/actuator/health

- Info: http://localhost:8090/actuator/info

### Build
```bash
mvn clean package
```
### Test
```bash
mvn test
```

## Example: List Events (cursor pagination)

First page:
```bash
curl "http://localhost:8090/v1/tenants/tenant_123/events?limit=30&fromTime=2026-03-01T00:00:00Z"
```
Next page:
- Use nextCursor from response :
```bash
curl "http://localhost:8090/v1/tenants/tenant_123/events?limit=30&cursor=<nextCursor>"
```

