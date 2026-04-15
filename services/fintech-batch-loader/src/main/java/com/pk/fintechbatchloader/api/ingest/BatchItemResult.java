package com.pk.fintechbatchloader.api.ingest;

public record BatchItemResult (
        int index,
        String eventId,
        String status, // ACCEPTED | REJECTED | FAILED | REJECTED_DUP_IN_BATCH
        String message,
        String requestId
) {
}
