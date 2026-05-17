package com.pk.mockjobsapi.api;

import com.pk.mockjobsapi.dto.JobsPageResponse;
import com.pk.mockjobsapi.service.MockJobsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/jobs")
public class MockJobsController {
    private final MockJobsService mockJobsService;

    public MockJobsController(MockJobsService mockJobsService){
        this.mockJobsService = mockJobsService;
    }

    @GetMapping
    public ResponseEntity<JobsPageResponse> getJobs(
            @RequestParam(required=false) String cursor,
            @RequestParam(defaultValue="5") int limit
    ){
        JobsPageResponse response = mockJobsService.getJobs(cursor, limit);
        return ResponseEntity.ok(response);
    }
}
