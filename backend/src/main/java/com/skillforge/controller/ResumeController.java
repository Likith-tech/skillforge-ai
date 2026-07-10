package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.dto.ResumeResponse;
import com.skillforge.dto.ResumeVersionResponse;
import com.skillforge.security.UserPrincipal;
import com.skillforge.service.ResumeFileResource;
import com.skillforge.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resumes")
@Validated
@Tag(name = "Resume Management", description = "Resume upload, preview, download, replacement, deletion, and history APIs")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Upload current student's resume")
    public ApiResponse<ResumeResponse> uploadMyResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.success("Resume uploaded", resumeService.upload(principal.getId(), file));
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Replace current student's active resume")
    public ApiResponse<ResumeResponse> replaceMyResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.success("Resume replaced", resumeService.replace(principal.getId(), file));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View current student's active resume metadata")
    public ApiResponse<ResumeResponse> getMyResume(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Resume loaded", resumeService.getCurrent(principal.getId()));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Delete current student's active resume")
    public ApiResponse<Void> deleteMyResume(@AuthenticationPrincipal UserPrincipal principal) {
        resumeService.delete(principal.getId());
        return ApiResponse.success("Resume deleted", null);
    }

    @GetMapping("/me/history")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View current student's resume version history")
    public ApiResponse<List<ResumeVersionResponse>> getMyResumeHistory(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Resume history loaded", resumeService.getHistory(principal.getId()));
    }

    @GetMapping("/me/download")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Download current student's active resume")
    public ResponseEntity<Resource> downloadMyResume(@AuthenticationPrincipal UserPrincipal principal) {
        return fileResponse(resumeService.getCurrentFile(principal.getId(), false), false);
    }

    @GetMapping("/me/preview")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Preview current student's active PDF resume")
    public ResponseEntity<Resource> previewMyResume(@AuthenticationPrincipal UserPrincipal principal) {
        return fileResponse(resumeService.getCurrentFile(principal.getId(), true), true);
    }

    @GetMapping("/me/versions/{versionId}/download")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Download one resume version for current student")
    public ResponseEntity<Resource> downloadMyResumeVersion(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable @Positive Long versionId
    ) {
        return fileResponse(resumeService.getVersionFile(principal.getId(), versionId, false), false);
    }

    @GetMapping("/me/versions/{versionId}/preview")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Preview one PDF resume version for current student")
    public ResponseEntity<Resource> previewMyResumeVersion(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable @Positive Long versionId
    ) {
        return fileResponse(resumeService.getVersionFile(principal.getId(), versionId, true), true);
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: view any student's active resume metadata")
    public ApiResponse<ResumeResponse> getStudentResume(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Resume loaded", resumeService.getCurrent(studentId));
    }

    @GetMapping("/students/{studentId}/history")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: view any student's resume version history")
    public ApiResponse<List<ResumeVersionResponse>> getStudentResumeHistory(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Resume history loaded", resumeService.getHistory(studentId));
    }

    @GetMapping("/students/{studentId}/download")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: download any student's active resume")
    public ResponseEntity<Resource> downloadStudentResume(@PathVariable @Positive Long studentId) {
        return fileResponse(resumeService.getCurrentFile(studentId, false), false);
    }

    @GetMapping("/students/{studentId}/preview")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: preview any student's active PDF resume")
    public ResponseEntity<Resource> previewStudentResume(@PathVariable @Positive Long studentId) {
        return fileResponse(resumeService.getCurrentFile(studentId, true), true);
    }

    @GetMapping("/students/{studentId}/versions/{versionId}/download")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: download any student's resume version")
    public ResponseEntity<Resource> downloadStudentResumeVersion(
            @PathVariable @Positive Long studentId,
            @PathVariable @Positive Long versionId
    ) {
        return fileResponse(resumeService.getVersionFile(studentId, versionId, false), false);
    }

    @GetMapping("/students/{studentId}/versions/{versionId}/preview")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: preview any student's PDF resume version")
    public ResponseEntity<Resource> previewStudentResumeVersion(
            @PathVariable @Positive Long studentId,
            @PathVariable @Positive Long versionId
    ) {
        return fileResponse(resumeService.getVersionFile(studentId, versionId, true), true);
    }

    private ResponseEntity<Resource> fileResponse(ResumeFileResource file, boolean inline) {
        ContentDisposition disposition = inline
                ? ContentDisposition.inline().filename(file.getOriginalFileName()).build()
                : ContentDisposition.attachment().filename(file.getOriginalFileName()).build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .contentLength(file.getFileSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(file.getResource());
    }
}
