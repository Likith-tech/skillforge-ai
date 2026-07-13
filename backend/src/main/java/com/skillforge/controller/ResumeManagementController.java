package com.skillforge.controller;

import com.skillforge.dto.PageResponse;
import com.skillforge.dto.ResumeFileDownload;
import com.skillforge.dto.ResumeResponse;
import com.skillforge.dto.ResumeSummaryResponse;
import com.skillforge.security.SecurityUser;
import com.skillforge.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

/**
 * Resume history/detail/delete/download endpoints. Kept separate from
 * ResumeController (which owns /resume/upload and /resume/{userId}) so that
 * existing, already-integrated behavior isn't touched while this expands
 * resume management under a plural /resumes prefix.
 */
@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
public class ResumeManagementController {

    private final ResumeService resumeService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PageResponse<ResumeSummaryResponse>> history(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(resumeService.listHistory(principal.getUser().getId(), page, size, sort));
    }

    @GetMapping("/{resumeId}")
    @PreAuthorize("hasAnyRole('STUDENT','RECRUITER','ADMIN')")
    public ResponseEntity<ResumeResponse> getResume(@PathVariable Long resumeId, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        resumeService.assertViewable(resumeId, principal.getUser().getId(), principal.isAdmin());
        return ResponseEntity.ok(resumeService.getById(resumeId));
    }

    @GetMapping("/{resumeId}/file")
    @PreAuthorize("hasAnyRole('STUDENT','RECRUITER','ADMIN')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long resumeId, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        resumeService.assertViewable(resumeId, principal.getUser().getId(), principal.isAdmin());
        ResumeFileDownload file = resumeService.loadFile(resumeId);

        String encodedName = java.net.URLEncoder.encode(file.fileName(), StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedName)
                .body(file.resource());
    }

    @DeleteMapping("/{resumeId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteResume(@PathVariable Long resumeId, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        resumeService.deleteResume(principal.getUser().getId(), resumeId);
        return ResponseEntity.noContent().build();
    }
}
