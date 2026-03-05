package com.pk.queryapi.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Represents an ingested event stored in the system")
public record EventRow(
        @Schema(description = "Internal database identifier", example = "10231")
        long id,

        @Schema(description = "Tenant that owns this event", example = "tenant-123")
        String tenantId,

        @Schema(description = "Original event identifier from source system", example = "evt-123")
        String eventId,

        @Schema(description = "Source system or topic that produced the event", example = "STREAMING")
        String source,

        @Schema(description = "Type/category of the event", example = "USER_CREATED")
        String eventType,

        @Schema(description = "When the event occurred in the source system (ISO-8601)", example = "2026-03-01T12:15:30Z")
        Instant occurredAt,

        @Schema(description = "When the event was ingested into the platform", example = "2026-03-01T12:15:32Z")
        Instant ingestedAt,

        @Schema(description = "Correlation id used for tracing across services", example = "corr-abc123")
        String correlationId,

        @Schema(
                description = "Raw event envelope or payload as JSON",
                example = "{\"source\":\"STREAMING\",\"tenantId\":\"tenant-1\",\"eventID\":\"evt-5\"}"
        )
        Map<String, Object> envelopeJson,

        @Schema(description = "Timestamp used for cursor pagination ordering", example = "2026-03-01T12:15:32Z")
        Instant createdAt

) { }
