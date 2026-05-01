package com.pk.activitywsconnector.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.activitywsconnector.model.ActivityConnectionContext;
import com.pk.activitywsconnector.model.ActivityMessage;
import com.pk.activitywsconnector.service.ActivityEventTransformer;
import com.pk.activitywsconnector.service.ActivityKafkaPublisher;
import com.pk.contracts.EventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class ActivityFeedClientHandler extends TextWebSocketHandler {
    private final ActivityConnectionContext context;
    private final ObjectMapper objectMapper;
    private final ActivityEventTransformer transformer;
    private final ActivityKafkaPublisher publisher;
    private final Consumer<ActivityConnectionContext> reconnectCallback;


    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        context.markConnected(session);
        log.info("WS connected: tenantId={}, url={}", context.tenantId(), context.feedUrl());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        ActivityMessage raw = objectMapper.readValue(message.getPayload(), ActivityMessage.class);
        context.markMessageReceived();

        EventEnvelope envelope = transformer.transform(context.tenantId(), raw);
        publisher.publish(context.tenantId(), envelope);

        log.info("WS message processed: tenantId={}, type={}, eventId={}",
                context.tenantId(), raw.type(), envelope.eventID());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception){
        log.warn("WS transport error: tenantId={}, error={}", context.tenantId(), exception.getMessage());
        reconnectCallback.accept(context);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.warn("WS closed: tenantId={}, status={}", context.tenantId(), status);
        reconnectCallback.accept(context);
    }
}
