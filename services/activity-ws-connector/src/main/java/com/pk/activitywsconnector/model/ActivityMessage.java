package com.pk.activitywsconnector.model;

import java.time.Instant;
import java.util.Map;

public record ActivityMessage (
        String activityId,
        String type,
        String userId,
        String deviceId,
        Instant occurredAt,
        Map<String, Object> payload
) {}
