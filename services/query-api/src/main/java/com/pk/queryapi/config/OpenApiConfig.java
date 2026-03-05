package com.pk.queryapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI queryApiOpenAPI(){
        return new OpenAPI().info(new Info().title("Query API").version("v1").description("Read/query layer for the multi-source ingestion platform."));
    }
}
