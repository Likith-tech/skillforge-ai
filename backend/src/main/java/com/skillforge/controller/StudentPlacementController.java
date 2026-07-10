package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.model.ApplicationStatus;
import com.skillforge.model.InterviewSchedule;
import com.skillforge.model.JobApplication;
import com.skillforge.model.OfferLetter;
import com.skillforge.model.PlacementTimelineEntry;
import com.skillforge.model.SavedJob;
import com.skillforge.security.UserPrincipal;
import com.skillforge.service.StudentPlacementService;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students/me/placement")
@PreAuthorize("hasRole('STUDENT')")
public class StudentPlacementController {

    private final StudentPlacementService placementService;

    public StudentPlacementController(StudentPlacementService placementService) {
        this.placementService = placementService;
    }

    @GetMapping("/applied-jobs")
    public ApiResponse<List<JobApplication>> appliedJobs(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Applied jobs loaded", placementService.appliedJobs(principal.getId()));
    }

    @GetMapping("/saved-jobs")
    public ApiResponse<List<SavedJob>> savedJobs(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Saved jobs loaded", placementService.savedJobs(principal.getId()));
    }

    @GetMapping("/interviews")
    public ApiResponse<List<InterviewSchedule>> interviews(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Interviews loaded", placementService.interviewSchedule(principal.getId()));
    }

    @GetMapping("/offers")
    public ApiResponse<List<OfferLetter>> offers(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Offers loaded", placementService.offers(principal.getId()));
    }

    @GetMapping("/timeline")
    public ApiResponse<List<PlacementTimelineEntry>> timeline(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Placement timeline loaded", placementService.timeline(principal.getId()));
    }

    @GetMapping("/applications/status")
    public ApiResponse<List<JobApplication>> applicationsByStatus(@AuthenticationPrincipal UserPrincipal principal, @RequestParam ApplicationStatus status) {
        return ApiResponse.success("Applications loaded", placementService.applicationsByStatus(principal.getId(), status));
    }

    @PostMapping("/saved-jobs/{jobId}")
    public ApiResponse<SavedJob> saveJob(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId) {
        return ApiResponse.success("Job saved", placementService.saveJob(principal.getId(), jobId));
    }

    @DeleteMapping("/saved-jobs/{jobId}")
    public ApiResponse<Void> removeSavedJob(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId) {
        placementService.removeSavedJob(principal.getId(), jobId);
        return ApiResponse.success("Saved job removed", null);
    }
}