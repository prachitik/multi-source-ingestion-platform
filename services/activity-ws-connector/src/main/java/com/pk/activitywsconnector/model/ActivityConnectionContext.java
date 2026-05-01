package com.pk.activitywsconnector.model;

import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ActivityConnectionContext {
    private final String tenantId;
    private final String feedUrl;
    private final AtomicReference<ConnectionStatus> status = new AtomicReference<>(ConnectionStatus.STOPPED);
    private final AtomicInteger reconnectAttempt = new AtomicInteger(0);

    private volatile Instant connectedAt;
    private volatile Instant lastMessageAt;
    private volatile String lastError;
    private volatile WebSocketSession session;
    private volatile ScheduledFuture<?> reconnectTask;


    public ActivityConnectionContext(String tenantId, String feedUrl){
        this.tenantId = tenantId;
        this.feedUrl = feedUrl;
    }

    public String tenantId(){
        return tenantId;
    }

    public String feedUrl(){
        return feedUrl;
    }

    public ConnectionStatus status() {
        return status.get();
    }

    public void markConnecting() {
        status.set(ConnectionStatus.CONNECTING);
    }

    public void markConnected(WebSocketSession session) {
        this.session = session;
        this.connectedAt = Instant.now();
        this.lastError = null;
        this.reconnectAttempt.set(0);
        status.set(ConnectionStatus.CONNECTED);
    }

    public void markMessageReceived() {
        this.lastMessageAt = Instant.now();
    }

    public void markRetryWait(String error) {
        this.lastError = error;
        status.set(ConnectionStatus.RETRY_WAIT);
    }

    public void markFailed(String error) {
        this.lastError = error;
        status.set(ConnectionStatus.FAILED);
    }
    public boolean isActive() {
        return session != null && session.isOpen();
    }

    public int nextAttempt() {
        return reconnectAttempt.incrementAndGet();
    }
    public String lastError() {
        return lastError;
    }

    public void setReconnectTask(ScheduledFuture<?> reconnectTask) {
        this.reconnectTask = reconnectTask;
    }

    public ScheduledFuture<?> reconnectTask() {
        return reconnectTask;
    }


}
