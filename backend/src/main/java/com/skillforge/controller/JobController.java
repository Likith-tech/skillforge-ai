package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.dto.JobListResponse;
import com.skillforge.dto.JobRequest;
import com.skillforge.dto.JobResponse;
import com.skillforge.model.JobStatus;
import com.skillforge.security.UserPrincipal;
import com.skillforge.service.JobService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@Validated
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('RECRUITER')")
    public ApiResponse<JobResponse> create(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody JobRequest request) {
        return ApiResponse.success("Job created", jobService.create(principal.getId(), request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<JobListResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ApiResponse.success("Jobs loaded", jobService.list(q, category, location, employmentType, sort, page, size));
    }

    @GetMapping("/{jobId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'RECRUITER', 'ADMIN')")
    public ApiResponse<JobResponse> get(@PathVariable @Positive Long jobId) {
        return ApiResponse.success("Job loaded", jobService.getById(jobId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RECRUITER')")
    public ApiResponse<java.util.List<JobResponse>> getMine(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Recruiter jobs loaded", jobService.listByRecruiter(principal.getId()));
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ApiResponse<JobResponse> update(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId, @Valid @RequestBody JobRequest request) {
        return ApiResponse.success("Job updated", jobService.update(principal.getId(), jobId, request));
    }

    @PutMapping("/{jobId}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ApiResponse<Void> updateStatus(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId, @RequestParam JobStatus status) {
        jobService.updateStatus(principal.getId(), jobId, status);
        return ApiResponse.success("Job status updated", null);
    }
}