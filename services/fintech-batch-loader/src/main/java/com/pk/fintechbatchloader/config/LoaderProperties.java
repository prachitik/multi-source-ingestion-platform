package com.pk.fintechbatchloader.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "loader")
public record LoaderProperties (
        @NotBlank String tenantId,
        @NotBlank String source,
        @Min(1) int batchSize,
        @NotBlank String ingestApiBaseUrl,
        @NotBlank String incomingDir,
        @NotBlank String processingDir,
        @NotBlank String processedDir

){
}
