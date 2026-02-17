package com.pk.contracts;

import java.util.ArrayList;
import java.util.List;

public final class EventEnvelopeValidator {
    private EventEnvelopeValidator(){};

    public static List<String> validate(EventEnvelope ev){
        List<String> errors = new ArrayList<>();
        if(ev == null){
            return List.of("Event is null.");
        }
        if(ev.tenantId().isEmpty()) errors.add("tenantId is required");
        if(ev.source().isEmpty()) errors.add("source is required");
        if(ev.eventType().isEmpty()) errors.add("eventType is required");
        if(ev.eventID().isEmpty()) errors.add("eventId is required");

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
