/**
 * It keeps one active connection per tenant and schedules reconnect attempts.
 */
package com.pk.activitywsconnector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.activitywsconnector.config.ActivityWsProperties;
import com.pk.activitywsconnector.model.ActivityConnectionContext;
import com.pk.activitywsconnector.ws.ActivityFeedClientHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;

import java.net.URI;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityWsConnectionManager {

    private final WebSocketClient webSocketClient;
    private final ObjectMapper objectMapper;
    private final ActivityEventTransformer transformer;
    private final ActivityKafkaPublisher publisher;
    private final ActivityWsProperties properties;
    private final TaskScheduler taskScheduler;

    private final ConcurrentMap<String, ActivityConnectionContext> sessions = new ConcurrentHashMap<>();

    public ActivityConnectionContext start(String tenantId, String feedUrl){
        ActivityConnectionContext existing = sessions.get(tenantId);
        if(existing != null && existing.isActive()){
            return existing;
        }
        ActivityConnectionContext  context = new ActivityConnectionContext(tenantId, feedUrl);
        sessions.put(tenantId, context);
        connect(context);
        return context;
    }

    private void connect(ActivityConnectionContext context){
        context.markConnecting();
        ActivityFeedClientHandler handler = new ActivityFeedClientHandler(
                context,
                objectMapper,
                transformer,
                publisher,
                this::scheduleReconnect

        );

        log.info("Attempting WS connect: tenantId={}, url={}", context.tenantId(), context.feedUrl());

        webSocketClient.execute(handler, new WebSocketHttpHeaders(), URI.create(context.feedUrl()))
                .whenComplete((session, ex) -> {
                    if(ex != null){
                        log.error("WS connect failed: tenantId={}, url={}",
                                context.tenantId(), context.feedUrl(), ex);
                        scheduleReconnect(context);
                    }
                });
    }

    private void scheduleReconnect(ActivityConnectionContext context){
        int attempt = context.nextAttempt();
        long delay = Math.min(properties.maxBackoffSeconds(),
                (long) (properties.initialBackoffSeconds() * Math.pow(2, attempt - 1)));

        context.markRetryWait("Reconnect attempt "+ attempt);

        log.info("Scheduling reconnect: tenantId={}, attempt={}, delaySeconds={}",
                context.tenantId(), attempt, delay);

        context.setReconnectTask(
                taskScheduler.schedule(
                        () -> connect(context),
                        Instant.now().plusSeconds(delay)
                )
        );

    }



}
