package com.pk.activitywsconnector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix= "ws-activity")
public record ActivityWsProperties(
    String defaultFeedUrl,
    String topic,
    long initialBackoffSeconds,
    long maxBackoffSeconds
) {}
