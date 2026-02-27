package com.pk.eventprocessor.db;

import com.pk.contracts.EventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.sql.Timestamp;

@Repository
public class EventRepository {
    private final JdbcTemplate jdbc;
    private final ObjectMapper om;

    public EventRepository(JdbcTemplate jdbc, ObjectMapper om){
        this.jdbc = jdbc;
        this.om = om;
    }

    /**
     *
     * @param ev
     * @return true if inserted, false if duplicate
     * @throws Exception
     */
    public boolean insertRawEvent(EventEnvelope ev) throws Exception{
        String json = om.writeValueAsString(ev);
        try{
            jdbc.update("""
                    INSERT INTO raw_events
                    (tenant_id, event_id, source, event_type, occurred_at, ingested_at, correlation_id, envelope_json)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)
                    """,
                    ev.tenantId(),
                    ev.eventID(),
                    ev.source().name(),
                    ev.eventType(),
                    Timestamp.from(ev.occurredAt()),
                    Timestamp.from(ev.ingestedAt()),
                    ev.correlationId(),
                    json
                    );
            return true;

        }catch(DuplicateKeyException ex){
            return false;
        }
    }

    public void upsertReceipt(String tenantId, String eventId, String status, String lastError){
        jdbc.update("""
        INSERT INTO ingestion_receipts (tenant_id, event_id, status, last_error, first_seen_at, updated_at)
        VALUES (?, ?, ?, ?, now(), now())
        ON CONFLICT (tenant_id, event_id)
        DO UPDATE SET status = EXCLUDED.status,
                      last_error = EXCLUDED.last_error,
                      updated_at = now()
        """,
                tenantId, eventId, status, lastError);
    }
}
