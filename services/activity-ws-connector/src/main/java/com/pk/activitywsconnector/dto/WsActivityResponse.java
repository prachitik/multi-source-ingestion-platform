package com.pk.activitywsconnector.dto;

public record WsActivityResponse (
        String tenantId,
        String status,
        String feedUrl,
        String message
){ }
