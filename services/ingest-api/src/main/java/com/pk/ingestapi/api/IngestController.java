package com.pk.ingestapi.api;

import com.pk.contracts.EventEnvelope;
import com.pk.contracts.EventEnvelopeValidator;
import com.pk.ingestapi.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Tag(name = "Ingestion", description = "APIs for ingesting events into the platform")
@RestController
@RequestMapping("/v1/tenants/{tenantId}")
public class IngestController {

    private final KafkaTemplate<String, EventEnvelope> kafkaTemplate;
    private final String topic;


    public IngestController(KafkaTemplate<String, EventEnvelope> kafkaTemplate, @Value("${kafka.topic.main}") String topic){
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Operation(
            summary = "Ingest a single event",
            description = "Validates a single event request, publishes it to Kafka using tenantId as the message key, and returns an accepted response."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Event accepted for asynchronous processing"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
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

    @Operation(
            summary = "Batch ingest events",
            description = "Accepts up to 200 events in a single request, validates each item, publishes valid events to Kafka, and returns per-item status."
    )
    @PostMapping("/events:batch")
    public ResponseEntity<?> ingestBatchEvents(
            @Parameter(description = "Tenant identifier", example = "tenant_123")
            @PathVariable String tenantId,
            @Valid @RequestBody BatchIngestRequest request){
        // extra check for validation or if list is null
        if(request.events() == null || request.events().isEmpty()){
            return ResponseEntity.badRequest().body("events must contain at least 1 event");
        }

        HashSet<String> seen = new HashSet<String>();
        List<BatchItemResult> results = new ArrayList<BatchItemResult>(request.events().size());

        int accepted = 0;
        int failed = 0;

        for(int i = 0; i < request.events().size(); i++){
            BatchIngestItem item = request.events().get(i);

            EventEnvelope eventEnvelope = new EventEnvelope(tenantId,
                    request.source(),
                    item.eventType(),
                    item.eventId(),
                    item.occurredAt(),
                    Instant.now(),
                    item.schemaVersion() <= 0 ? 1 : item.schemaVersion(),
                    item.payload(),
                    (item.correlationId() != null && !item.correlationId().isBlank())
                            ? item.correlationId() : UUID.randomUUID().toString()
            );

            // dedup within the batch
            String dedupKey = tenantId + "|" + request.source() + "|" + item.eventType() + "|" + item.eventId();
            if(!seen.add(dedupKey)){
                failed++;
                results.add(new BatchItemResult(i, item.eventId(), "REJECTED_DUP_IN_BATCH",
                        "duplicate event in same batch", null));
                continue;
            }
            //validate each event - do not fail entire batch
            try{
                EventEnvelopeValidator.validateOrThrow(eventEnvelope);
            } catch(Exception e){
                failed++;
                results.add(new BatchItemResult(i, item.eventId(), "REJECTED",
                        e.getMessage(), null));
                continue;
            }

            // validated and hence now can be published to Kafka
            // publish with key=tenantId (topic, key, data)
            try{
                kafkaTemplate.send(topic, tenantId, eventEnvelope);
                accepted++;
                results.add(new BatchItemResult(i, item.eventId(), "ACCEPTED",
                        "event published", UUID.randomUUID().toString()));
            } catch(Exception e){
                failed++;
                results.add(new BatchItemResult(i, item.eventId(), "FAILED",
                        e.getMessage(), null));
            }

        }
        return ResponseEntity.accepted().body(new BatchIngestResponse(
                tenantId,
                request.source(),
                accepted,
                failed,
                results
        ));
    }

}
