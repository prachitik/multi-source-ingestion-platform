package com.pk.contracts;

import java.util.ArrayList;
import java.util.List;

public final class EventEnvelopeValidator {
    private EventEnvelopeValidator(){};

    public static void validateOrThrow(EventEnvelope ev){
        List<String> errors = validate(ev);
        if(!errors.isEmpty()){
            throw new IllegalArgumentException("Invalid EventEnvelop: " + String.join("; ", errors));
        }
    }

    public static List<String> validate(EventEnvelope ev){
        List<String> errors = new ArrayList<>();
        if(ev == null){
            return List.of("Event is null.");
        }
        if(ev.tenantId() == null || ev.tenantId().trim().isEmpty()) errors.add("tenantId is required");
        if(ev.source() == null) errors.add("source is required");
        if(ev.eventType() == null || ev.eventType().trim().isEmpty()) errors.add("eventType is required");
        if(ev.eventID() == null || ev.eventID().trim().isEmpty()) errors.add("eventId is required");

        if(ev.occurredAt() == null) errors.add("occurredAt is required");
        if(ev.ingestedAt() == null) errors.add("ingestedAt is required");

        if(ev.schemaVersion() <= 0) errors.add("schemaVersion must be > 0");

        if(ev.payload() == null) errors.add("payload is required");

        // occurredAt <= ingestedAt
        if(ev.occurredAt() != null && ev.ingestedAt() != null){
            if(ev.ingestedAt().isBefore(ev.occurredAt().minusSeconds(60*60*24*365))){
                errors.add("ingestedAt is unreasonably before occurredAt");
            }
        }
        return errors;
    }
}
