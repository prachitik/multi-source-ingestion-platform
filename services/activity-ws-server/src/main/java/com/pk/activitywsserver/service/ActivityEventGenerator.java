package com.pk.activitywsserver.service;

import com.pk.activitywsserver.model.ActivityMessage;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ActivityEventGenerator {

    private static final String[] EVENT_TYPES = {
            "steps.updated",
            "heart_rate.updated",
            "calories.updated",
            "sleep.updated"
    };

    public ActivityMessage nextEvent(){
        String type = EVENT_TYPES[ThreadLocalRandom.current().nextInt(EVENT_TYPES.length)];

        Map<String, Object> payload = switch(type){
            case "steps.updated" -> Map.of(
                    "steps", ThreadLocalRandom.current().nextInt(100, 1200),
                    "distanceMeters", ThreadLocalRandom.current().nextInt(50, 900)
            );
            case "heart_rate.updated" -> Map.of(
                    "heartRate", ThreadLocalRandom.current().nextInt(60, 150),
                    "resting", ThreadLocalRandom.current().nextBoolean()
            );
            case "calories.updated" -> Map.of(
                    "calories", ThreadLocalRandom.current().nextInt(10, 200),
                    "activeMinutes", ThreadLocalRandom.current().nextInt(1, 45)
            );
            case "sleep.updated" -> Map.of(
                    "sleepMinutes", ThreadLocalRandom.current().nextInt(20, 480),
                    "quality", pickSleepQuality()
            );
            default -> Map.of("value", ThreadLocalRandom.current().nextInt(1,100));
        };

        return new ActivityMessage(
                "act-" + UUID.randomUUID(),
                type,
                "user-"+ ThreadLocalRandom.current().nextInt(1,11),
                "device-"+ ThreadLocalRandom.current().nextInt(1,6),
                Instant.now(),
                payload
        );
    }

    private String pickSleepQuality(){
        String[] values = {"LOW", "MEDIUM", "HIGH"};
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}
