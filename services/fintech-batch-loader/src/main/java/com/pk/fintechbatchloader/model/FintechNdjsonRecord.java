package com.pk.fintechbatchloader.model;

import java.time.Instant;

public record FintechNdjsonRecord (
        String eventId,
        String eventType,
        Instant occurredAt,
        int schemaVersion,
        Object payload,
        String correlationId
){
}
