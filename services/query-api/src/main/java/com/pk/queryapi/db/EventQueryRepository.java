package com.pk.queryapi.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.queryapi.model.EventRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.*;

@Repository
public class EventQueryRepository {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    public EventQueryRepository(JdbcTemplate jdbc, ObjectMapper mapper){
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    // find from - to events
    public List<EventRow> find(String tenantId, Instant fromTime, Instant toTime, String eventType,
                               String source, Cursor cursor, int limit){
        StringBuilder sql = new StringBuilder();
        sql.append("""
                SELECT id, tenant_id, event_id, source, event_type, occurred_at, ingested_at,
                correlation_id, envelope_json, created_at
                FROM raw_events
                WHERE tenant_id = ?
                """);
        List<Object> args = new ArrayList<>();
        args.add(tenantId);
        if(fromTime != null){
            sql.append(" AND occurred_at >= ?");
            args.add(Timestamp.from(fromTime));
        }
        if(toTime != null){
            sql.append(" AND occurred_at <= ?");
            args.add(Timestamp.from(toTime));
        }
        if(eventType != null && !eventType.isBlank()){
            sql.append(" AND event_type = ?");
            args.add(eventType);
        }
        if(source != null && !source.isBlank()){
            sql.append(" AND source = ?");
            args.add(source);
        }
        if(cursor != null){
            sql.append(" AND (created_at, id) < (?, ?) ");
            args.add(Timestamp.from(cursor.createdAt()));
            args.add(cursor.id());
        }

        sql.append(" ORDER BY created_at DESC, id DESC LIMIT ?");
        args.add(limit);

        return jdbc.query(sql.toString(), (rs, rowNum) -> {
            String env = rs.getString("envelope_json");
            Map<String, Object> envMap = null;
            try {
                envMap = mapper.readValue(env, new TypeReference<>(){});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return new EventRow(
                    rs.getLong("id"),
                    rs.getString("tenant_id"),
                    rs.getString("event_id"),
                    rs.getString("source"),
                    rs.getString("event_type"),
                    rs.getTimestamp("occurred_at").toInstant(),
                    rs.getTimestamp("ingested_at").toInstant(),
                    rs.getString("correlation_id"),
                    envMap,
                    rs.getTimestamp("created_at").toInstant()
            );
        }, args.toArray());

    }

    public record Cursor(Instant createdAt, long id){}
}
