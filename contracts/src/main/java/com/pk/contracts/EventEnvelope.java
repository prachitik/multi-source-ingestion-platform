package com.pk.contracts;

import java.time.Instant;

public record EventEnvelope (
        String tenantId,
        Source source,
        String eventType,
        String eventID,
        Instant occurredAt,
        Instant ingestedAt,
        int schemaVersion,
        Object payload,

        String correlationId

){
}
