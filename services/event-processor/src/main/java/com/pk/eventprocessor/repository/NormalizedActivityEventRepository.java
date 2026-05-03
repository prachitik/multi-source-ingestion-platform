package com.pk.eventprocessor.repository;

import com.pk.eventprocessor.model.NormalizedActivityEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NormalizedActivityEventRepository {
    private final JdbcTemplate jdbcTemplate;

    public NormalizedActivityEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(NormalizedActivityEvent event){
        String sql = """
            INSERT INTO normalized_activity_events (
                tenant_id,
                event_id,
                source,
                event_type,
                user_id,
                device_id,
                steps,
                distance_meters,
                heart_rate,
                calories,
                active_minutes,
                sleep_minutes,
                sleep_quality,
                occurred_at,
                ingested_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (tenant_id, event_id) DO NOTHING
        """;

        jdbcTemplate.update(sql,
                event.tenantId(),
                event.eventId(),
                event.source(),
                event.eventType(),
                event.userId(),
                event.deviceId(),
                event.steps(),
                event.distanceMeters(),
                event.heartRate(),
                event.calories(),
                event.activeMinutes(),
                event.sleepMinutes(),
                event.sleepQuality(),
                event.occurredAt(),
                event.ingestedAt()
        );
    }

}
