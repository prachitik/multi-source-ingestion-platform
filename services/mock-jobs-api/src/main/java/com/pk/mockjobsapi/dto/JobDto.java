package com.pk.mockjobsapi.dto;

import java.time.Instant;

public record JobDto(
        String jobId,
        String title,
        String company,
        String location,
        Instant postedAt

) { }
