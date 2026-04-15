package com.pk.fintechbatchloader.file;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Repository
public class FileIngestionRunRepository {
    private final JdbcTemplate jdbcTemplate;

    public FileIngestionRunRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(FileIngestionRun run) {
        String sql = """
                INSERT INTO file_ingestion_runs (
                    file_id,
                    tenant_id,
                    source,
                    file_name,
                    received_at,
                    processing_started_at,
                    processed_at,
                    total_lines,
                    accepted,
                    failed,
                    status,
                    error_message
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                run.getFileId(),
                run.getTenantId(),
                run.getSource(),
                run.getFileName(),
                toTimestamp(run.getReceivedAt()),
                toTimestamp(run.getProcessingStartedAt()),
                toTimestamp(run.getProcessedAt()),
                run.getTotalLines(),
                run.getAccepted(),
                run.getFailed(),
                run.getStatus().name(),
                run.getErrorMessage()
        );
    }

    public void updateProcessingStarted(UUID fileId, Instant processingStartedAt) {
        String sql = """
                UPDATE file_ingestion_runs
                SET processing_started_at = ?, status = ?
                WHERE file_id = ?
                """;

        jdbcTemplate.update(sql,
                Timestamp.from(processingStartedAt),
                FileStatus.PROCESSING.name(),
                fileId
        );
    }

    public void updateCompletion(UUID fileId, Instant processedAt, int totalLines, int accepted, int failed, FileStatus status) {
        String sql = """
                UPDATE file_ingestion_runs
                SET processed_at = ?,
                    total_lines = ?,
                    accepted = ?,
                    failed = ?,
                    status = ?
                WHERE file_id = ?
                """;

        jdbcTemplate.update(sql,
                Timestamp.from(processedAt),
                totalLines,
                accepted,
                failed,
                status.name(),
                fileId
        );
    }

    public void updateFailure(UUID fileId, Instant processedAt, int totalLines, int accepted, int failed, String errorMessage) {
        String sql = """
                UPDATE file_ingestion_runs
                SET processed_at = ?,
                    total_lines = ?,
                    accepted = ?,
                    failed = ?,
                    status = ?,
                    error_message = ?
                WHERE file_id = ?
                """;

        jdbcTemplate.update(sql,
                Timestamp.from(processedAt),
                totalLines,
                accepted,
                failed,
                FileStatus.FAILED.name(),
                errorMessage,
                fileId
        );
    }

    private Timestamp toTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }
}
