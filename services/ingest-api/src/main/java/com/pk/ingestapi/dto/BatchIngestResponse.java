package com.pk.ingestapi.dto;

import com.pk.contracts.Source;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response returned after batch ingest processing")
public record BatchIngestResponse(
        @Schema(description = "Tenant identifier", example = "tenant_123")
        String tenantId,

        @Schema(description = "Source of the batch", example = "ECOMMERCE")
        Source source,

        @Schema(description = "Number of accepted events", example = "2")
        int acceptedCount,

        @Schema(description = "Number of failed or rejected events", example = "0")
        int failedCount,

        @Schema(description = "Per-item batch processing results")
        List<BatchItemResult> results
){
}
