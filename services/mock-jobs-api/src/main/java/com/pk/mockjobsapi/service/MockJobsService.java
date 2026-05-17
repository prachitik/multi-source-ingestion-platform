package com.pk.mockjobsapi.service;

import com.pk.mockjobsapi.dto.JobDto;
import com.pk.mockjobsapi.dto.JobsPageResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class MockJobsService {
    private final List<JobDto> jobs = seedJobs();

    public JobsPageResponse getJobs(String cursor, int limit){
        int safeLimit = Math.min(Math.max(limit, 1), 20); // If limit exceeds 20, limit at most 20 at a time

        int startIndex = decodeCursor(cursor);
        if(startIndex >= jobs.size()){
            return new JobsPageResponse(List.of(), null, false);
        }
        int endIndex = Math.min(startIndex + safeLimit, jobs.size());

        List<JobDto> pageItems = jobs.subList(startIndex, endIndex);

        boolean hasMore = endIndex < jobs.size();
        String nextCursor = hasMore ? encodeCursor(endIndex) : null;

        return new JobsPageResponse(pageItems, nextCursor, hasMore);
    }

    private int decodeCursor(String cursor){
        if(cursor == null || cursor.isBlank()){
            return 0;
        }
        try{
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);

            return Integer.parseInt(decoded);
        }catch(Exception e){
            throw new IllegalArgumentException("Invalid Cursor");
        }
    }

    private String encodeCursor(int index){
        return Base64.getEncoder().encodeToString(String.valueOf(index).getBytes(StandardCharsets.UTF_8));
    }

    private List<JobDto> seedJobs() {
        List<JobDto> list = new ArrayList<>();

        list.add(new JobDto("job-001", "Backend Engineer", "Acme Corp", "Remote", Instant.parse("2026-05-01T10:00:00Z")));
        list.add(new JobDto("job-002", "Java Developer", "Nimbus Tech", "Bangalore", Instant.parse("2026-05-01T11:00:00Z")));
        list.add(new JobDto("job-003", "Platform Engineer", "CloudWorks", "Pune", Instant.parse("2026-05-01T12:00:00Z")));
        list.add(new JobDto("job-004", "Software Engineer II", "DataNest", "Hyderabad", Instant.parse("2026-05-01T13:00:00Z")));
        list.add(new JobDto("job-005", "Spring Boot Developer", "FinServe", "Mumbai", Instant.parse("2026-05-01T14:00:00Z")));
        list.add(new JobDto("job-006", "Distributed Systems Engineer", "ScaleGrid", "Remote", Instant.parse("2026-05-02T09:00:00Z")));
        list.add(new JobDto("job-007", "Kafka Engineer", "Streamline", "Bangalore", Instant.parse("2026-05-02T10:00:00Z")));
        list.add(new JobDto("job-008", "Backend Developer", "RetailStack", "Pune", Instant.parse("2026-05-02T11:00:00Z")));
        list.add(new JobDto("job-009", "Cloud Backend Engineer", "InfraLabs", "Remote", Instant.parse("2026-05-02T12:00:00Z")));
        list.add(new JobDto("job-010", "Microservices Developer", "OrderFlow", "Hyderabad", Instant.parse("2026-05-02T13:00:00Z")));

        return list;
    }


    public record Cursor(Instant createdAt, long id){}
}
