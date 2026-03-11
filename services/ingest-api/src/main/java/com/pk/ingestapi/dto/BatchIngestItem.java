package com.pk.ingestapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Schema(description = "Single event inside a batch ingest request")
public record BatchIngestItem(
        @Schema(description = "Business event type", example = "ORDER_CREATED")
        @NotBlank String eventType,

        @Schema(description = "Unique event identifier from source system", example = "evt_1")
        @NotBlank String eventId,

        @Schema(description = "When the event occurred in the source system", example = "2026-03-05T21:10:00Z")
        @NotNull Instant occurredAt,

        @Schema(description = "Schema version of the payload", example = "1")
        int schemaVersion,

        @Schema(description = "Event payload", example = "{\"orderId\":\"A1\",\"amount\":10.25}")
        @NotNull Object payload,

        @Schema(description = "Optional correlation identifier for tracing", example = "corr-123")
        String correlationId
){ }
