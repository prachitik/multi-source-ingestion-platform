package com.pk.activitywsserver.model;

import java.time.Instant;
import java.util.Map;

/**
 * This is raw source side payload and the ws-connector will transform this.
 * @param activityId
 * @param type
 * @param userId
 * @param deviceId
 * @param occurredAt
 * @param payload
 */
public record ActivityMessage(
        String activityId,
        String type,
        String userId,
        String deviceId,
        Instant occurredAt,
        Map<String, Object> payload
) { }
