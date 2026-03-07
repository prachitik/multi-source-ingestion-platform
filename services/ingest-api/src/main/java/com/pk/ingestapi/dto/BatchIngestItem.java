package com.pk.ingestapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record BatchIngestItem(
        @NotBlank String eventType,
        @NotBlank String eventId,
        @NotNull Instant occurredAt,
        int schemaVersion,
        @NotNull Object payload,
        String correlationId
){ }
