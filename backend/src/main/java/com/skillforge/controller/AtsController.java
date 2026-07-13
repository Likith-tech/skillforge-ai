package com.skillforge.controller;

import com.skillforge.dto.AtsAnalyzeRequest;
import com.skillforge.dto.AtsHistoryItemResponse;
import com.skillforge.dto.AtsReportResponse;
import com.skillforge.dto.PageResponse;
import com.skillforge.security.SecurityUser;
import com.skillforge.service.AtsReportService;
import com.skillforge.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Module 2: AI ATS Intelligence Engine - analysis, reports, and history. */
@RestController
@RequestMapping("/ats")
@RequiredArgsConstructor
public class AtsController {

    private final AtsReportService atsReportService;
    private final ResumeService resumeService;

    @PostMapping("/analyze")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AtsReportResponse> analyze(
            @Valid @RequestBody AtsAnalyzeRequest request, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        resumeService.assertOwner(request.getResumeId(), principal.getUser().getId());
        AtsReportResponse response = atsReportService.analyze(request.getResumeId(), request.getTargetRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/report/{resumeId}")
    @PreAuthorize("hasAnyRole('STUDENT','RECRUITER','ADMIN')")
    public ResponseEntity<AtsReportResponse> getReport(@PathVariable Long resumeId, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        resumeService.assertViewable(resumeId, principal.getUser().getId(), principal.isAdmin());
        return ResponseEntity.ok(atsReportService.getLatestReport(resumeId));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PageResponse<AtsHistoryItemResponse>> history(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(atsReportService.listHistory(principal.getUser().getId(), page, size, sort));
    }

    @DeleteMapping("/history/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteHistoryEntry(@PathVariable Long id, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        atsReportService.deleteHistoryEntry(principal.getUser().getId(), id);
        return ResponseEntity.noContent().build();
    }
}
