package com.pk.fintechbatchloader;

import com.pk.fintechbatchloader.config.LoaderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LoaderProperties.class)
public class FintechBatchLoaderApplication {
    public static void main(String[] args){
        SpringApplication.run(FintechBatchLoaderApplication.class, args);
    }

}
