package com.pk.mockjobsapi.dto;

import java.util.List;

public record JobsPageResponse(
    List<JobDto> items,
    String nextCursor,
    boolean hasMore
) { }
