package com.pk.jobspoller.checkpoint;

import java.time.Instant;
public record PollerCheckpoint (
    Long id,
    String tenantId,
    String sourceName,
    String cursorValue,
    Instant lastPolledAt,
    Instant createdAt,
    Instant updatedAt
){ }
