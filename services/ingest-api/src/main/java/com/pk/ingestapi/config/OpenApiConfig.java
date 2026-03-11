package com.pk.ingestapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ingestionPlatformApiOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Multi-source event ingestion platform API")
                        .description("REST APIs for ingesting and querying events across multiple sources such as ECOMMERCE, FINTECH, STREAMING, and JOBS_POLLER.")
                        .version("v1"));
    }
}
