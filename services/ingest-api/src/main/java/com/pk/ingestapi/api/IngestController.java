package com.pk.ingestapi.api;

import com.pk.contracts.EventEnvelope;
import com.pk.contracts.EventEnvelopeValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants/{tenantId}")
public class IngestController {

    private final KafkaTemplate<String, EventEnvelope> kafkaTemplate;
    private final String topic;


    public IngestController(KafkaTemplate<String, EventEnvelope> kafkaTemplate, @Value("${kafka.topic.main}") String topic){
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @PostMapping("/events")
    public ResponseEntity<?> ingestSingleEvent(@PathVariable String tenantId, @Valid @RequestBody IngestEventRequest request){

        EventEnvelope eventEnvelop = new EventEnvelope(tenantId,
                request.source(),
                request.eventType(),
                request.eventId(),
                request.occurredAt(),
                Instant.now(),
                request.schemaVersion() <= 0 ? 1 : request.schemaVersion(),
                request.payload(),
                request.correlationId() != null ? request.correlationId() : UUID.randomUUID().toString()
        );

        EventEnvelopeValidator.validateOrThrow(eventEnvelop);

        //publish with key=tenantId (topic, key, data)
        kafkaTemplate.send(topic, tenantId, eventEnvelop);
        return ResponseEntity.accepted().body(new IngestResponse(UUID.randomUUID().toString(), "ACCEPTED"));
    }

    record IngestResponse(String ingestId, String status){
    }


}
