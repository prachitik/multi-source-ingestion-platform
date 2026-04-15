package com.pk.fintechbatchloader.api.ingest;

import java.time.Instant;

public record BatchIngestItem (
        String eventType,
        String eventId,
        Instant occurredAt,
        int schemaVersion,
        Object payload,
        String correlationId
){
}
