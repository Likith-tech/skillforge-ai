package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.model.ApplicationStatus;
import com.skillforge.model.InterviewSchedule;
import com.skillforge.model.JobApplication;
import com.skillforge.model.OfferLetter;
import com.skillforge.security.UserPrincipal;
import com.skillforge.service.RecruiterPortalService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/recruiter/workflow")
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterWorkflowController {

    private final RecruiterPortalService recruiterPortalService;

    public RecruiterWorkflowController(RecruiterPortalService recruiterPortalService) {
        this.recruiterPortalService = recruiterPortalService;
    }

    @GetMapping("/jobs/{jobId}/applicants")
    public ApiResponse<List<JobApplication>> applicants(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId) {
        return ApiResponse.success("Applicants loaded", recruiterPortalService.applicants(principal.getId(), jobId));
    }

    @GetMapping("/jobs/{jobId}/applicants/ranked")
    public ApiResponse<List<JobApplication>> rankedApplicants(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId) {
        return ApiResponse.success("Ranked applicants loaded", recruiterPortalService.rankedApplicants(principal.getId(), jobId));
    }

    @PostMapping("/applications/{applicationId}/shortlist")
    public ApiResponse<JobApplication> shortlist(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long applicationId) {
        return ApiResponse.success("Applicant shortlisted", recruiterPortalService.shortlist(principal.getId(), applicationId));
    }

    @PostMapping("/applications/{applicationId}/reject")
    public ApiResponse<JobApplication> reject(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long applicationId) {
        return ApiResponse.success("Applicant rejected", recruiterPortalService.reject(principal.getId(), applicationId));
    }

    @PostMapping("/applications/{applicationId}/interview")
    public ApiResponse<InterviewSchedule> scheduleInterview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable @Positive Long applicationId,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime interviewAt,
            @RequestParam(required = false) String meetingLink,
            @RequestParam(required = false) String notes
    ) {
        return ApiResponse.success("Interview scheduled", recruiterPortalService.scheduleInterview(principal.getId(), applicationId, interviewAt, meetingLink, notes));
    }

    @PostMapping("/applications/{applicationId}/offer")
    public ApiResponse<OfferLetter> offer(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long applicationId, @RequestParam @NotBlank String offerTitle, @RequestParam(required = false) String offerDescription) {
        return ApiResponse.success("Offer generated", recruiterPortalService.createOffer(principal.getId(), applicationId, offerTitle, offerDescription));
    }

    @DeleteMapping("/applications/{applicationId}/offer")
    public ApiResponse<OfferLetter> withdrawOffer(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long applicationId) {
        return ApiResponse.success("Offer withdrawn", recruiterPortalService.withdrawOffer(principal.getId(), applicationId));
    }

    @GetMapping("/jobs/{jobId}/timeline")
    public ApiResponse<List<JobApplication>> timeline(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId) {
        return ApiResponse.success("Candidate timeline loaded", recruiterPortalService.applicants(principal.getId(), jobId));
    }

    @GetMapping("/jobs/{jobId}/status-count")
    public ApiResponse<Long> statusCount(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId, @RequestParam ApplicationStatus status) {
        return ApiResponse.success("Applicant count loaded", recruiterPortalService.countApplicants(principal.getId(), jobId));
    }
}