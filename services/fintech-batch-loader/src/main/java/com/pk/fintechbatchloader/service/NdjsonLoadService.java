package com.pk.fintechbatchloader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.contracts.Source;
import com.pk.fintechbatchloader.api.ingest.BatchIngestItem;
import com.pk.fintechbatchloader.api.ingest.BatchIngestRequest;
import com.pk.fintechbatchloader.api.ingest.BatchIngestResponse;
import com.pk.fintechbatchloader.client.IngestApiClient;
import com.pk.fintechbatchloader.config.LoaderProperties;
import com.pk.fintechbatchloader.file.FileIngestionRun;
import com.pk.fintechbatchloader.file.FileIngestionRunService;
import com.pk.fintechbatchloader.model.FintechNdjsonRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class NdjsonLoadService {
    private final ObjectMapper objectMapper;
    private final IngestApiClient ingestApiClient;
    private final FileIngestionRunService fileRunService;
    private final LoaderProperties properties;

    private static final Source SOURCE = Source.FINTECH;


    public NdjsonLoadService(
            ObjectMapper om,
            IngestApiClient ingestApiClient,
            FileIngestionRunService fileRunService,
            LoaderProperties properties
    ){
        this.objectMapper = om;
        this.ingestApiClient = ingestApiClient;
        this.fileRunService = fileRunService;
        this.properties = properties;
    }

    /**
     * malformed JSON lines are counted as failed and skipped
     * batch HTTP failures throw and fail the file
     * partial batch failures are counted using acceptedCount and failedCount
     * @param incomingFile
     */
    public void processFile(Path incomingFile){
        FileIngestionRun run = fileRunService.createReceived(
                properties.tenantId(),
                SOURCE.name() ,
                incomingFile.getFileName().toString()
        );

        int totalLines = 0;
        int accepted = 0;
        int failed = 0;

        Path processingPath = null;

        try{
            //create and ensure directories exist
            createDirectories();
            System.out.println("Incoming file path: " + incomingFile.toAbsolutePath());
            processingPath = moveToProcessing(incomingFile);
            System.out.println("Moved file to processing: " + processingPath.toAbsolutePath());

            fileRunService.markProcessingStarted(run.getFileId());

            List<BatchIngestItem> currentBatch = new ArrayList<>();

            try(BufferedReader reader = Files.newBufferedReader(processingPath)){
                String line;
                while((line = reader.readLine()) != null){
                    totalLines++;

                    if(line.isBlank()){
                        failed++;
                        System.out.println("Skipping blank line in file "+ processingPath.getFileName() + " at line: " + totalLines);
                        continue;
                    }

                    try{
                        FintechNdjsonRecord record = objectMapper.readValue(line, FintechNdjsonRecord.class);

                        BatchIngestItem item = new BatchIngestItem(
                                record.eventType(),
                                record.eventId(),
                                record.occurredAt(),
                                record.schemaVersion() <= 0 ? 1 : record.schemaVersion(),
                                record.payload(),
                                record.correlationId()
                        );

                        currentBatch.add(item);

                        if(currentBatch.size() >= properties.batchSize()){
                            BatchResult batchResult = sendBatch(currentBatch);
                            accepted += batchResult.accepted();
                            failed += batchResult.failed();
                            currentBatch.clear();
                        }

                    }catch(Exception e){
                        failed++;
                        System.out.println("Malformed JSON in file "+ processingPath.getFileName()
                                + " at line " + totalLines + ": " + e.getMessage());
                    }
                }
            }

            if(!currentBatch.isEmpty()){
                BatchResult batchResult = sendBatch(currentBatch);
                accepted += batchResult.accepted();
                failed += batchResult.failed();
            }

            Path processedPath = moveToProcessed(processingPath);
            System.out.println("Processed file moved to: "+ processedPath);

            fileRunService.markCompleted(run.getFileId(), totalLines, accepted, failed);

        }catch(Exception e){
            System.err.println("Error while processing file: " + incomingFile.getFileName());
            e.printStackTrace();
            //
            fileRunService.markFailed(run.getFileId(), totalLines, accepted, failed, e.getMessage());
            throw new RuntimeException("Failed processing file: " + incomingFile.getFileName(), e);
        }
    }

    /**
     * Caused by: java.nio.file.NoSuchFileException:
     * ../../data/fintech/incoming/sample.ndjson -> ../../data/fintech/processing/sample.ndjson
     * The failure is happening when the loader tries to move the file from incoming to processing.
     * The reasons could be -
     * Even though the file was discovered, one of these is true:
     * 1. The processing directory does not exist at the moment of move
     * 2. The relative path is fragile and not resolving the way we expect
     * 3. The source file path being passed around is relative, and the move operation is resolving it inconsistently
     *
     * Changing it to use normalized absolute paths and ensure the target parent exists right before the move.
     * @throws IOException
     */
    private void createDirectories() throws IOException {
        Files.createDirectories(Path.of(properties.incomingDir()).toAbsolutePath().normalize());
        Files.createDirectories(Path.of(properties.processedDir()).toAbsolutePath().normalize());
        Files.createDirectories(Path.of(properties.processedDir()).toAbsolutePath().normalize());
    }

    private Path moveToProcessing(Path incomingFile) throws IOException {
        Path source = incomingFile.toAbsolutePath().normalize();
        Path targetDir = Path.of(properties.processingDir()).toAbsolutePath().normalize();
        Files.createDirectories(targetDir);

        //Path target = Path.of(properties.processingDir(), incomingFile.getFileName().toString());
        Path target = targetDir.resolve(source.getFileName());
        return Files.move(incomingFile, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private Path moveToProcessed(Path processingFile) throws IOException {
        Path source = processingFile.toAbsolutePath().normalize();
        Path targetDir = Path.of(properties.processedDir()).toAbsolutePath().normalize();
        Files.createDirectories(targetDir);

        //Path target = Path.of(properties.processingDir(), processingFile.getFileName().toString());
        //return Files.move(processingFile, target);
        Path target = targetDir.resolve(source.getFileName());
        return Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private BatchResult sendBatch(List<BatchIngestItem> batch) throws IOException, InterruptedException {
        BatchIngestRequest request = new BatchIngestRequest(SOURCE, List.copyOf(batch));

        //
        System.out.println("Sending batch to ingest-api. Size=" + batch.size());

        BatchIngestResponse response = ingestApiClient.sendBatch(
                properties.ingestApiBaseUrl(),
                properties.tenantId(),
                request);

        System.out.println("Batch sent: accepted= " + response.acceptedCount() + ", failed=" + response.failedCount());
        return new BatchResult(response.acceptedCount(), response.failedCount());
    }

    private record BatchResult(
            int accepted,
            int failed
    ){ }


}
