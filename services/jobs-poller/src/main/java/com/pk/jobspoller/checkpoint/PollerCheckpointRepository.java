package com.pk.jobspoller.checkpoint;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Repository
public class PollerCheckpointRepository {
    private final JdbcTemplate jdbcTemplate;

    public PollerCheckpointRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<PollerCheckpoint> findByTenantIdAndSourceName(String tenantId, String sourceName){
        String sql = """
                SELECT id, tenant_id, source_name, cursor_value,
                last_polled_at, created_at, updated_at
                FROM poller_checkpoints
                WHERE tenant_id = ? AND source_name = ?
                """;
        return jdbcTemplate.query(sql, rs -> {
            if( !rs.next()){
                return Optional.empty();
            }

            return Optional.of(new PollerCheckpoint(
                    rs.getLong("id"),
                    rs.getString("tenant_id"),
                    rs.getString("source_name"),
                    rs.getString("cursor_value"),
                    toInstant(rs.getTimestamp("last_polled_at")),
                    toInstant(rs.getTimestamp("created_at")),
                    toInstant(rs.getTimestamp("updated_at"))
            ));
        }, tenantId, sourceName);

    }

    public void upsertCheckpoint(String tenantId, String sourceName, String cursorValue){
        String sql = """
                INSERT INTO poller_checkpoints(
                tenant_id,
                source_name,
                cursor_value,
                last_polled_at,
                created_at,
                updated_at
                )
                VALUES (?, ?, ?, NOW(), NOW(), NOW())
                ON CONFLICT (tenant_id, source_name)
                DO UPDATE SET
                    cursor_value = EXCLUDED.cursor_value,
                    last_polled_at = NOW(),
                    updated_at = NOW()
                """;
        jdbcTemplate.update(sql, tenantId, sourceName, cursorValue);
    }

    private Instant toInstant(Timestamp timestamp){
        return timestamp == null ? null : timestamp.toInstant();
    }

}
