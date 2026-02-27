package com.pk.eventprocessor.kafka;

import com.pk.contracts.EventEnvelope;
import com.pk.contracts.EventEnvelopeValidator;
import com.pk.eventprocessor.db.EventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);
    private final EventRepository repo;

    public EventConsumer(EventRepository repo){
        this.repo = repo;
    }

    @KafkaListener(topics="${app.kafka.topic}")
    public void onMessage(ConsumerRecord<String, EventEnvelope> record, Acknowledgment ack){
        EventEnvelope ev = record.value();
        try{
            // validate the contract
            EventEnvelopeValidator.validateOrThrow(ev);
            // mark "ACCEPTED" first time we see it
            repo.upsertReceipt(ev.tenantId(), ev.eventID(), "ACCEPTED", null);
            boolean inserted = repo.insertRawEvent(ev);

            if(inserted){
                repo.upsertReceipt(ev.tenantId(), ev.eventID(), "PROCESSED", null);
                log.info("processed tenant={} eventId={} partition={} offset={}",
                        ev.tenantId(), ev.eventID(), record.partition(), record.offset());
            }else{
                repo.upsertReceipt(ev.tenantId(), ev.eventID(), "DUPLICATE", null);
                log.info("duplicate tenant={} eventId={} partition={} offset={}",
                        ev.tenantId(), ev.eventID(), record.partition(), record.offset());
            }
            // commit offset only after DB work succeeds
            ack.acknowledge();

        } catch (Exception ex) {
            // For MVP: marked FAILED and do NOT ack (so it can retry). (Later we’ll route to DLQ.)
            repo.upsertReceipt(
                    ex != null ? ev.tenantId() : "unknown",
                    ex != null ? ev.eventID() : "unknown",
                    "FAILED",
                    ex.getMessage()
            );
            log.error("failed processing partition={} offset={}: {}", record.partition(), record.offset(), ex.toString(), ex);

            // No ack → Kafka will redeliver (at-least-once)
        }
    }
}
