package com.skillforge.controller;

import com.skillforge.dto.ResumeResponse;
import com.skillforge.security.SecurityUser;
import com.skillforge.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResumeResponse> upload(@RequestParam("file") MultipartFile file, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        ResumeResponse response = resumeService.upload(principal.getUser().getId(), file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT','RECRUITER','ADMIN')")
    public ResponseEntity<ResumeResponse> getResume(@PathVariable Long userId, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        ResumeResponse response = resumeService.getLatestForUser(userId);
        resumeService.assertViewable(response.getId(), principal.getUser().getId(), principal.isAdmin());
        return ResponseEntity.ok(response);
    }
}
