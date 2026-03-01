package com.pk.queryapi.model;

import java.time.Instant;
import java.util.Map;

public record EventRow(
        long id,
        String tenantId,
        String eventId,
        String source,
        String eventType,
        Instant occurredAt,
        Instant ingestedAt,
        String correlationId,
        Map<String, Object> envelopeJson,
        Instant createdAt

) {

}
