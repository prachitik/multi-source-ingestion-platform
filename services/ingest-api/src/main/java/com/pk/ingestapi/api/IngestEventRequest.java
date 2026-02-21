package com.pk.ingestapi.api;

import com.pk.contracts.Source;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record IngestEventRequest (
    @NotNull Source source,
    @NotBlank String eventType,
    @NotBlank String eventId,
    @NotNull Instant occurredAt,

    int schemaVersion,

    @NotNull Object payload,
    String correlationId
){ }
