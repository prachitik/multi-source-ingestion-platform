package com.pk.activitywsserver.ws;

import com.pk.activitywsserver.service.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityFeedWebSocketHandler extends TextWebSocketHandler {

    private final SessionRegistry sessionRegistry;

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        sessionRegistry.add(session);
        log.info("WS client connected: sessionId:{}, activeSessions={}", session.getId(), sessionRegistry.size());
    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message){
        log.info("Received Client message: sessionId={}, payload={}", session.getId(), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        sessionRegistry.remove(session);
        log.info("WS client disconnected: sessionId={}, status={}, activeSessions={}",
                session.getId(), status, sessionRegistry.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception){
        sessionRegistry.remove(session);
        log.warn("WS transport error: sessionId={}, error={}",
                session != null ? session.getId():"unknown",
                exception.getMessage());
    }



}
