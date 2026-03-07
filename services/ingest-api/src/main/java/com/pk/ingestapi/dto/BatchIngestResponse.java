package com.pk.ingestapi.dto;

import com.pk.contracts.Source;

import java.util.List;

public record BatchIngestResponse(
        String tenantId,
        Source source,
        int acceptedCount,
        int failedCount,
        List<BatchItemResult> results
){
}
