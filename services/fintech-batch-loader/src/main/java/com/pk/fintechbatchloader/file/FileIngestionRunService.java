package com.pk.fintechbatchloader.file;

import java.time.Instant;
import java.util.UUID;

public class FileIngestionRunService {
    private final FileIngestionRunRepository repository;

    public FileIngestionRunService(FileIngestionRunRepository repository) {
        this.repository = repository;
    }

    public FileIngestionRun createReceived(String tenantId, String source, String fileName) {
        FileIngestionRun run = new FileIngestionRun();
        run.setFileId(UUID.randomUUID());
        run.setTenantId(tenantId);
        run.setSource(source);
        run.setFileName(fileName);
        run.setReceivedAt(Instant.now());
        run.setStatus(FileStatus.RECEIVED);
        run.setTotalLines(0);
        run.setAccepted(0);
        run.setFailed(0);

        repository.insert(run);
        return run;
    }

    public void markProcessingStarted(UUID fileId) {
        repository.updateProcessingStarted(fileId, Instant.now());
    }

    public void markCompleted(UUID fileId, int totalLines, int accepted, int failed) {
        FileStatus finalStatus = failed > 0
                ? FileStatus.PROCESSED_WITH_ERRORS
                : FileStatus.PROCESSED;

        repository.updateCompletion(fileId, Instant.now(), totalLines, accepted, failed, finalStatus);
    }

    public void markFailed(UUID fileId, int totalLines, int accepted, int failed, String errorMessage) {
        repository.updateFailure(fileId, Instant.now(), totalLines, accepted, failed, errorMessage);
    }
}
