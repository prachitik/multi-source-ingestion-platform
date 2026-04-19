package com.pk.activitywsserver.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SessionRegistry {

    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void add(WebSocketSession session){
        sessions.put(session.getId(), session);
    }

    public void remove(WebSocketSession session){
        if(session != null){
            sessions.remove(session.getId());
        }
    }

    public Collection<WebSocketSession> getAll(){
        return sessions.values();
    }

    public int size(){
        return sessions.size();
    }


}
