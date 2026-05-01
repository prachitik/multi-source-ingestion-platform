package com.pk.activitywsconnector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.activitywsconnector.config.ActivityWsProperties;
import com.pk.contracts.EventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ActivityWsProperties activityWsProperties;
    private final ObjectMapper objectMapper;

    public void publish(String tenantId, EventEnvelope envelope){
        try{
            String json = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(activityWsProperties.topic(), tenantId, json)
                    .whenComplete((result, ex) -> {
                        if(ex != null){
                            log.error("kafka publish failed: tenantId={}, eventId={}", tenantId, envelope.eventID(), ex);
                        } else{
                            log.info("kafka publish success: tenantId={}, topic={}, eventId={}",
                                    tenantId, activityWsProperties.topic(), envelope.eventID());
                        }
                    });

        }catch(Exception ex){
            log.error("Failed to serialize EventEnvelope: tenantId={}, eventId={}",
                    tenantId, envelope.eventID(), ex);
        }

    }

}
