package com.skillforge.controller;

import com.skillforge.dto.JobMatchResponse;
import com.skillforge.dto.JobRequest;
import com.skillforge.dto.JobResponse;
import com.skillforge.dto.JobStatusUpdateRequest;
import com.skillforge.dto.PageResponse;
import com.skillforge.dto.SavedJobResponse;
import com.skillforge.security.SecurityUser;
import com.skillforge.service.JobMatchingService;
import com.skillforge.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final JobMatchingService jobMatchingService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        JobResponse response = jobService.createJob(principal.getUser().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<JobResponse>> listOpenJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(
                jobService.searchJobs(keyword, location, type, experienceLevel, skill, company, page, size, sort));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<PageResponse<JobResponse>> listMine(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(jobService.listJobsForRecruiter(principal.getUser().getId(), page, size, sort));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<JobResponse>> listAllJobs(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(jobService.listAllJobs(page, size, sort));
    }

    @GetMapping("/recommended")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<JobMatchResponse>> recommended(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(jobMatchingService.getRecommendedJobs(principal.getUser().getId()));
    }

    @GetMapping("/saved")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SavedJobResponse>> savedJobs(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(jobService.listSavedJobs(principal.getUser().getId()));
    }

    @PostMapping("/{id}/save")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> saveJob(@PathVariable Long id, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        jobService.saveJob(principal.getUser().getId(), id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/save")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> unsaveJob(@PathVariable Long id, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        jobService.unsaveJob(principal.getUser().getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id, @Valid @RequestBody JobRequest request, Authentication authentication) {
        assertOwnsJobOrIsAdmin(id, authentication);
        return ResponseEntity.ok(jobService.updateJob(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id, Authentication authentication) {
        assertOwnsJobOrIsAdmin(id, authentication);
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    public ResponseEntity<JobResponse> setStatus(
            @PathVariable Long id, @Valid @RequestBody JobStatusUpdateRequest request, Authentication authentication) {
        assertOwnsJobOrIsAdmin(id, authentication);
        return ResponseEntity.ok(jobService.setStatus(id, request.getStatus()));
    }

    private void assertOwnsJobOrIsAdmin(Long jobId, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return;
        }

        Long ownerId = jobService.getJob(jobId).getPostedById();
        if (!ownerId.equals(principal.getUser().getId())) {
            throw new AccessDeniedException("You can only manage jobs you posted");
        }
    }
}
