package com.pk.queryapi.api;

import com.pk.queryapi.db.EventQueryRepository;
import com.pk.queryapi.db.EventQueryRepository.Cursor;
import com.pk.queryapi.model.EventRow;
import com.pk.queryapi.util.CursorCodec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1/tenants/{tenantId}")
public class EventQueryController {
    private final EventQueryRepository repo;

    public EventQueryController(EventQueryRepository repo){
        this.repo = repo;
    }

    @GetMapping("/events")
    public PagedResponse listEvents(@PathVariable String tenantId,
                                     @RequestParam(required = false) Instant fromTime,
                                     @RequestParam(required = false) Instant toTime,
                                     @RequestParam(required = false) String eventType,
                                     @RequestParam(required = false) String source,
                                     @RequestParam(required = false) String cursor,
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

    public record PagedResponse(List<EventRow> events, String nextCursor){}
}
