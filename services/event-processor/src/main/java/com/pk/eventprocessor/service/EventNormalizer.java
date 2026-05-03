package com.pk.eventprocessor.service;

import com.pk.contracts.EventEnvelope;
import com.pk.contracts.Source;
import com.pk.eventprocessor.model.NormalizedActivityEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class EventNormalizer {
    public Optional<NormalizedActivityEvent> normalize(EventEnvelope envelope){
        if(envelope.source() != Source.ACTIVITY_WS){
            return Optional.empty();
        }
        //Map<String, Object> payload = envelope.payload();

        Object rawPayload = envelope.payload();

        if (!(rawPayload instanceof Map<?, ?> rawMap)) {
            return Optional.empty(); // or throw exception if you prefer
        }

        Map<String, Object> payload = (Map<String, Object>) rawMap;

        String eventType = envelope.eventType();
        return Optional.of(new NormalizedActivityEvent(
                envelope.tenantId(),
                envelope.eventID(),
                envelope.source().name(),
                eventType,
                getString(payload, "userId"),
                getString(payload, "deviceId"),
                getInteger(payload, "steps"),
                getInteger(payload, "distanceMeters"),
                getInteger(payload, "heartRate"),
                getInteger(payload, "calories"),
                getInteger(payload, "activeMinutes"),
                getInteger(payload, "sleepMinutes"),
                getString(payload, "sleepQuality"),
                envelope.occurredAt(),
                envelope.ingestedAt()
        ));
    }

    private String getString(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer getInteger(Map<String, Object> payload, String key) {
        Object value = payload.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer i) {
            return i;
        }

        if (value instanceof Number n) {
            return n.intValue();
        }

        return Integer.valueOf(value.toString());
    }
}
