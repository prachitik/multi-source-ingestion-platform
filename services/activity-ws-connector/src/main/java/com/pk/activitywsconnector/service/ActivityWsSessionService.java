package com.pk.activitywsconnector.service;

import com.pk.activitywsconnector.config.ActivityWsProperties;
import com.pk.activitywsconnector.dto.StartWsActivityRequest;
import com.pk.activitywsconnector.dto.WsActivityResponse;
import com.pk.activitywsconnector.model.ActivityConnectionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityWsSessionService {
    private final ActivityWsConnectionManager connectionManager;
    private final ActivityWsProperties properties;

    public WsActivityResponse start(String tenantId, StartWsActivityRequest request){
        String feedUrl = (request != null && request.feedUrl() != null && !request.feedUrl().isBlank())
                ? request.feedUrl()
                : properties.defaultFeedUrl();

        ActivityConnectionContext context = connectionManager.start(tenantId, feedUrl);

        return new WsActivityResponse(
                tenantId,
                context.status().name(),
                feedUrl,
                "WS activity connector started"
        );
    }

}
