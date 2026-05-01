package com.pk.activitywsconnector.connector;

import com.pk.activitywsconnector.dto.StartWsActivityRequest;
import com.pk.activitywsconnector.dto.WsActivityResponse;
import com.pk.activitywsconnector.service.ActivityWsSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tenants/{tenantId}/ws-activity")
@RequiredArgsConstructor
public class ActivityWsController {
    private final ActivityWsSessionService sessionService;

    @PostMapping
    public ResponseEntity<WsActivityResponse> start(
            @PathVariable String tenantId,
            @RequestBody(required=false) StartWsActivityRequest request){
        return ResponseEntity.accepted().body(sessionService.start(tenantId, request));
    }
}
