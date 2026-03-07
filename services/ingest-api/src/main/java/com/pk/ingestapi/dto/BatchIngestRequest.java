package com.pk.ingestapi.dto;

import com.pk.contracts.Source;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BatchIngestRequest (
        @NotNull Source source,
        @Size(min = 1, max = 200)
        List<BatchIngestItem> events

){ }
