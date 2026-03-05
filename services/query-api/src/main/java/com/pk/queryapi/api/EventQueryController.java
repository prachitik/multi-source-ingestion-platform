package com.pk.queryapi.api;

import com.pk.queryapi.db.EventQueryRepository;
import com.pk.queryapi.db.EventQueryRepository.Cursor;
import com.pk.queryapi.model.EventRow;
import com.pk.queryapi.util.CursorCodec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name= "Events", description="Query ingested events for a tenant (cursor-based pagination).")
@RestController
@RequestMapping("/v1/tenants/{tenantId}")
public class EventQueryController {
    private final EventQueryRepository repo;

    public EventQueryController(EventQueryRepository repo){
        this.repo = repo;
    }

    @Operation(
            summary = "List Events",
            description = "Returns events for the tenant filtered by optional time range and other attributes. "
                        + "Uses cursor-based pagination; pass `nextCursor` from the previous response as `cursor`."
    )
    @GetMapping("/events")
    public PagedResponse listEvents(
            @Parameter(description = "Tenant identifier", example = "tenant_123", required = true)
            @PathVariable String tenantId,

            @Parameter(description = "Filter: start time (inclusive), ISO-8601", example = "2026-03-01T00:00:00Z")
            @RequestParam(required = false) Instant fromTime,

            @Parameter(description = "Filter: end time (exclusive), ISO-8601", example = "2026-03-02T00:00:00Z")
            @RequestParam(required = false) Instant toTime,

            @Parameter(description = "Filter: event type", example = "USER_CREATED")
            @RequestParam(required = false) String eventType,

            @Parameter(description = "Filter: event source (e.g., kafka topic or upstream system)", example = "STREAMING")
            @RequestParam(required = false) String source,

            @Parameter(
                    description = "Opaque cursor token from previous response (`nextCursor`). Leave empty for first page.",
                    example = "eyJjcmVhdGVkQXQiOiIyMDI2LTAzLTAxVDAwOjAwOjAwWiIsImlkIjoiMTIzIn0="
            )
            @RequestParam(required = false) String cursor,

            @Parameter(description = "Page size (1..200). Default 30.", example = "30")
            @RequestParam(defaultValue = "30") int limit){

        int safeLimit = Math.min(Math.max(1, limit), 200); // if limit exceed 200, limit at most 200 at a time

        Cursor decoded = (cursor == null || cursor.isBlank()) ? null : CursorCodec.decode(cursor);
        List<EventRow> events = repo.find(tenantId, fromTime, toTime, eventType, source, decoded, safeLimit);

        String nextCursor = null;
        // keep track of last record in this call
        if(events.size() == safeLimit){
            EventRow last = events.get(events.size()-1);
            nextCursor = CursorCodec.encode(new Cursor(last.createdAt(), last.id()));
        }

        return new PagedResponse(events, nextCursor);
    }


    @Schema(description = "Paged response for event queries")
    public record PagedResponse(
            @Schema(description = "Events returned for this page")
            List<EventRow> events,

            @Schema(
                    description = "Cursor token for the next page. Pass this value back as the `cursor` query parameter.",
                    nullable = true
            )
            String nextCursor
    ){}
}
