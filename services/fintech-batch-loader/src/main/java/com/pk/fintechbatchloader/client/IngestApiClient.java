package com.pk.fintechbatchloader.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.fintechbatchloader.api.ingest.BatchIngestRequest;
import com.pk.fintechbatchloader.api.ingest.BatchIngestResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class IngestApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public IngestApiClient(ObjectMapper om){
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = om;
    }

    public BatchIngestResponse sendBatch(String baseUrl, String tenantId, BatchIngestRequest request) throws IOException, InterruptedException {
        String url = baseUrl + "/v1/tenants/" + tenantId + "/events:batch";
        String body = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if(status < 200 || status >= 300){
            throw new IllegalStateException("ingest-api batch request failed with status " + status + ": " + response.body());
        }

        return objectMapper.readValue(response.body(), BatchIngestResponse.class);
    }


}
