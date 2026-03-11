package com.pk.ingestapi.dto;

import com.pk.contracts.Source;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description="Batch ingest request containing multiple events under same source")
public record BatchIngestRequest (
        @Schema(
                description="Source of all events in this batch",
                example="ECOMMERCE"
        )
        @NotNull Source source,

        @Schema(
                description="List of events to ingest in this batch",
                requiredMode= Schema.RequiredMode.REQUIRED
        )
        @Size(min = 1, max = 200)
        List<@Valid BatchIngestItem> events

){ }
