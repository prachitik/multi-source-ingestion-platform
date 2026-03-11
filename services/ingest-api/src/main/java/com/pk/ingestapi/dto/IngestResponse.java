package com.pk.ingestapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after accepting an ingest request")
public record IngestResponse(
        @Schema(
                description = "Generated request identifier for tracking ingestion",
                example = "7f8a0b9e-1234-4ef0-a111-222233334444"
        )
        String ingestId,

        @Schema(
                description = "Processing status of the ingestion request",
                example = "ACCEPTED"
        )
        String status
) { }