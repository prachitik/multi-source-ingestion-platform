package com.pk.ingestapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result for an individual batch item")
public record BatchItemResult(
        @Schema(description = "Zero-based index of the event in the original request", example = "0")
        int index,

        @Schema(description = "Event id of the processed item", example = "evt_1")
        String eventId,

        @Schema(description = "Processing outcome", example = "ACCEPTED")
        String status, // ACCEPTED | REJECTED | FAILED | REJECTED_DUP_IN_BATCH

        @Schema(description = "Optional error/rejection message", nullable = true, example = "Duplicate event in same batch")
        String message,

        @Schema(description = "Generated request identifier", nullable = true, example = "7f8a0b9e-1234-4ef0-a111-222233334444")
        String requestId
) { }
