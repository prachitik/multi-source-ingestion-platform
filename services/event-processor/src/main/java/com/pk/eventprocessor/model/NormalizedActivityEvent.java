package com.pk.eventprocessor.model;

import java.time.Instant;
public record NormalizedActivityEvent(
        String tenantId,
        String eventId,
        String source,
        String eventType,

        String userId,
        String deviceId,

        Integer steps,
        Integer distanceMeters,
        Integer heartRate,
        Integer calories,
        Integer activeMinutes,
        Integer sleepMinutes,
        String sleepQuality,

        Instant occurredAt,
        Instant ingestedAt

) { }
