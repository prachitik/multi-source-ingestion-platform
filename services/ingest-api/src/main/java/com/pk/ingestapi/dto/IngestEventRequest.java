package com.pk.ingestapi.dto;

import com.pk.contracts.Source;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Schema(description = "Request for ingesting a single event")
public record IngestEventRequest (
        @Schema(description = "Source system of the event", example = "ECOMMERCE")
        @NotNull Source source,

        @Schema(description = "Business event type", example = "ORDER_CREATED")
        @NotBlank String eventType,

        @Schema(description = "Unique event identifier from the source system", example = "evt_1001")
        @NotBlank String eventId,

        @Schema(description = "Timestamp when the event occurred in the source system", example = "2026-03-07T17:00:00Z")
        @NotNull Instant occurredAt,

        @Schema(description = "Schema version of the event payload", example = "1")
        int schemaVersion,

        @Schema(description = "Raw event payload", example = "{\"orderId\":\"A1001\",\"amount\":25.50}")
        @NotNull Object payload,

        @Schema(description = "Optional correlation id for tracing", example = "corr-123")
        String correlationId
){ }
