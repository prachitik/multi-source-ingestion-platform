package com.pk.jobspoller.controller;

import com.pk.jobspoller.checkpoint.PollerCheckpoint;
import com.pk.jobspoller.checkpoint.PollerCheckpointRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/internal/checkpoints")
public class CheckpointTestController {
    private static final String SOURCE_NAME = "JOBS_POLLER";
    private final PollerCheckpointRepository repo;

    public CheckpointTestController(PollerCheckpointRepository repo){
        this.repo = repo;
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<PollerCheckpoint> getCheckpoint(@PathVariable String tenantId){
        return repo.findByTenantIdAndSourceName(tenantId, SOURCE_NAME)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{tenantId}")
    public ResponseEntity<Void> upsertCheckpoint(
            @PathVariable String tenantId,
            @RequestParam(required=false) String cursor
    ){
        repo.upsertCheckpoint(tenantId, SOURCE_NAME, cursor);
        return ResponseEntity.ok().build();
    }



}
