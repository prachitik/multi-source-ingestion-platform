package com.pk.activitywsserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.activitywsserver.model.ActivityMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityBroadcastService {

    private final SessionRegistry sessionRegistry;
    private final ActivityEventGenerator activityEventGenerator;
    private final ObjectMapper objectMapper;

    @Scheduled(initialDelay = 2000, fixedRate = 3000)
    public void broadcastActivity(){
        if(sessionRegistry.size() == 0){
            return;
        }

        ActivityMessage event = activityEventGenerator.nextEvent();

        try{
            String json = objectMapper.writeValueAsString(event);
            TextMessage message = new TextMessage(json);

            List<WebSocketSession> staleSessions = new ArrayList<>();

            for(WebSocketSession session: sessionRegistry.getAll()){
                if(!session.isOpen()){
                    staleSessions.add(session);
                    continue;
                }
                try{
                    session.sendMessage(message);
                }catch(IOException ex){
                    log.warn("Failed to send message to sessionId={}, error={}",
                            session.getId(), ex.getMessage());
                    staleSessions.add(session);
                }
            }

            for(WebSocketSession stale : staleSessions){
                sessionRegistry.remove(stale);
            }
            log.info("Broadcasted activity event: activityId={}, type={}, activeSessions={}",
                    event.activityId(), event.type(), sessionRegistry.size());

        }catch(Exception ex){
            log.error("Failed to broadcast activity event", ex);
        }
    }
}
