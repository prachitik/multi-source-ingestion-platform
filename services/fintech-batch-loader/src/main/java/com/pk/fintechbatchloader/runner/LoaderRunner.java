package com.pk.fintechbatchloader.runner;

import com.pk.fintechbatchloader.config.LoaderProperties;
import com.pk.fintechbatchloader.service.NdjsonLoadService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LoaderRunner implements CommandLineRunner {
    private final LoaderProperties properties;
    private final NdjsonLoadService loadService;

    public LoaderRunner(LoaderProperties properties, NdjsonLoadService loadService){
        this.properties = properties;
        this.loadService = loadService;
    }

    @Override
    public void run(String... args) throws IOException {
        //Path incomingDir = Path.of(properties.incomingDir());
        Path incomingDir = Path.of(properties.incomingDir()).toAbsolutePath().normalize();
        Files.createDirectories(incomingDir);

        try(Stream<Path> stream = Files.list(incomingDir)){
            List<Path> files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".ndjson"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .collect(Collectors.toList());

            if(files.isEmpty()){
                System.out.println("No NDJSON files found in incoming directory: "+ incomingDir);
                return;
            }

            for(Path file: files){
                System.out.println("Starting processing for file: "+ file.getFileName());
                loadService.processFile(file);
            }

        }
    }

}
