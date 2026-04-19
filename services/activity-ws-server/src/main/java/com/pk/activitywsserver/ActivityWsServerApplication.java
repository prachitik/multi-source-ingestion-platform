package com.pk.activitywsserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ActivityWsServerApplication {
    public static void main(String[] args){
        SpringApplication.run(ActivityWsServerApplication.class, args);
    }
}
