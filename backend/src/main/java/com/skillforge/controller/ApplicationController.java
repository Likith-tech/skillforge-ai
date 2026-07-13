package com.skillforge.controller;

import com.skillforge.dto.ApplicationRequest;
import com.skillforge.dto.ApplicationResponse;
import com.skillforge.dto.ApplicationStatusUpdateRequest;
import com.skillforge.dto.PageResponse;
import com.skillforge.security.SecurityUser;
import com.skillforge.service.ApplicationService;
import com.skillforge.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationResponse> apply(
            @Valid @RequestBody ApplicationRequest request, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        ApplicationResponse response = applicationService.apply(principal.getUser().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PageResponse<ApplicationResponse>> myApplications(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(applicationService.listForStudent(principal.getUser().getId(), page, size, sort));
    }

    /** Alias of /applications/me under the Module 3 spec's naming - same behavior, both stay supported. */
    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PageResponse<ApplicationResponse>> myApplicationsAlias(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        return myApplications(authentication, page, size, sort);
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    public ResponseEntity<PageResponse<ApplicationResponse>> forJob(
            @PathVariable Long jobId,
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        assertRecruiterOwnsJobOrIsAdmin(jobId, authentication);
        return ResponseEntity.ok(applicationService.listForJob(jobId, status, page, size, sort));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request,
            Authentication authentication) {
        ApplicationResponse existing = applicationService.getById(id);
        assertRecruiterOwnsJobOrIsAdmin(existing.getJobId(), authentication);
        return ResponseEntity.ok(applicationService.updateStatus(id, request.getStatus()));
    }

    private void assertRecruiterOwnsJobOrIsAdmin(Long jobId, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return;
        }

        Long ownerId = jobService.getJob(jobId).getPostedById();
        if (!ownerId.equals(principal.getUser().getId())) {
            throw new AccessDeniedException("You can only manage applications for jobs you posted");
        }
    }
}
