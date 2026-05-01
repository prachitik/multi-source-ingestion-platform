package com.pk.activitywsconnector.service;

import com.pk.activitywsconnector.model.ActivityMessage;
import com.pk.contracts.EventEnvelope;
import com.pk.contracts.Source;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ActivityEventTransformer {

    public EventEnvelope transform(String tenantId, ActivityMessage raw){
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", raw.userId());
        payload.put("deviceId", raw.deviceId());

        if(raw.payload() != null){
            payload.putAll(raw.payload());
        }

        return new EventEnvelope(
                tenantId,
                Source.ACTIVITY_WS,
                raw.type(),
                raw.activityId() != null ? raw.activityId() : UUID.randomUUID().toString(),
                raw.occurredAt() != null ? raw.occurredAt() : Instant.now(),
                Instant.now(),
                1,
                payload,
                UUID.randomUUID().toString()

        );
    }

}
