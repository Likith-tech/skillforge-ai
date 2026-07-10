package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.dto.ResumeAnalysisDashboardResponse;
import com.skillforge.dto.ResumeAnalyticsResponse;
import com.skillforge.dto.ResumeComparisonResponse;
import com.skillforge.dto.ResumeHistoryEntryResponse;
import com.skillforge.dto.ResumeKeywordAnalysisResponse;
import com.skillforge.dto.ResumeParsedDataResponse;
import com.skillforge.dto.ResumeScoreBreakdownResponse;
import com.skillforge.dto.ResumeSuggestionResponse;
import com.skillforge.security.UserPrincipal;
import com.skillforge.service.ResumeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resumes")
@Tag(name = "Resume Analysis", description = "Resume parsing, ATS scoring, keyword analysis, analytics, history, and comparison APIs")
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeAnalysisController(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
    }

    @GetMapping("/me/analysis")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View current student's full resume analysis dashboard")
    public ApiResponse<ResumeAnalysisDashboardResponse> getMyAnalysis(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Resume analysis loaded", resumeAnalysisService.getDashboard(principal.getId()));
    }

    @GetMapping("/me/analysis/parsed")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View current student's parsed resume data")
    public ApiResponse<ResumeParsedDataResponse> getMyParsedResume(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Parsed resume loaded", resumeAnalysisService.getParsedResume(principal.getId()));
    }

    @GetMapping("/me/analysis/scores")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View current student's ATS score breakdown")
    public ApiResponse<ResumeScoreBreakdownResponse> getMyScores(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("ATS scores loaded", resumeAnalysisService.getScores(principal.getId()));
    }

    @GetMapping("/me/analysis/keywords")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View current student's keyword analysis")
    public ApiResponse<ResumeKeywordAnalysisResponse> getMyKeywords(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Keyword analysis loaded", resumeAnalysisService.getKeywords(principal.getId()));
    }

    @GetMapping("/me/analysis/suggestions")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View current student's resume suggestions")
    public ApiResponse<List<ResumeSuggestionResponse>> getMySuggestions(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Resume suggestions loaded", resumeAnalysisService.getSuggestions(principal.getId()));
    }

    @GetMapping("/me/analysis/analytics")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View current student's resume analytics")
    public ApiResponse<ResumeAnalyticsResponse> getMyAnalytics(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Resume analytics loaded", resumeAnalysisService.getAnalytics(principal.getId()));
    }

    @GetMapping("/me/analysis/history")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View current student's analyzed resume history")
    public ApiResponse<List<ResumeHistoryEntryResponse>> getMyAnalysisHistory(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Resume history loaded", resumeAnalysisService.getHistory(principal.getId()));
    }

    @GetMapping("/me/analysis/history/compare")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Compare two analyzed resume versions for the current student")
    public ApiResponse<ResumeComparisonResponse> compareMyAnalysisVersions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @Positive Long leftVersionId,
            @RequestParam @Positive Long rightVersionId
    ) {
        return ApiResponse.success("Resume comparison loaded", resumeAnalysisService.compare(principal.getId(), leftVersionId, rightVersionId));
    }

    @GetMapping("/students/{studentId}/analysis")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: view any student's full resume analysis dashboard")
    public ApiResponse<ResumeAnalysisDashboardResponse> getStudentAnalysis(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Resume analysis loaded", resumeAnalysisService.getAdminDashboard(studentId));
    }

    @GetMapping("/students/{studentId}/analysis/parsed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: view any student's parsed resume data")
    public ApiResponse<ResumeParsedDataResponse> getStudentParsedResume(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Parsed resume loaded", resumeAnalysisService.getAdminParsedResume(studentId));
    }

    @GetMapping("/students/{studentId}/analysis/scores")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: view any student's ATS score breakdown")
    public ApiResponse<ResumeScoreBreakdownResponse> getStudentScores(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("ATS scores loaded", resumeAnalysisService.getAdminScores(studentId));
    }

    @GetMapping("/students/{studentId}/analysis/keywords")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: view any student's keyword analysis")
    public ApiResponse<ResumeKeywordAnalysisResponse> getStudentKeywords(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Keyword analysis loaded", resumeAnalysisService.getAdminKeywords(studentId));
    }

    @GetMapping("/students/{studentId}/analysis/suggestions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: view any student's resume suggestions")
    public ApiResponse<List<ResumeSuggestionResponse>> getStudentSuggestions(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Resume suggestions loaded", resumeAnalysisService.getAdminSuggestions(studentId));
    }

    @GetMapping("/students/{studentId}/analysis/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: view any student's resume analytics")
    public ApiResponse<ResumeAnalyticsResponse> getStudentAnalytics(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Resume analytics loaded", resumeAnalysisService.getAdminAnalytics(studentId));
    }

    @GetMapping("/students/{studentId}/analysis/history")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: view any student's analyzed resume history")
    public ApiResponse<List<ResumeHistoryEntryResponse>> getStudentAnalysisHistory(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Resume history loaded", resumeAnalysisService.getAdminHistory(studentId));
    }

    @GetMapping("/students/{studentId}/analysis/history/compare")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: compare two analyzed resume versions for a student")
    public ApiResponse<ResumeComparisonResponse> compareStudentAnalysisVersions(
            @PathVariable @Positive Long studentId,
            @RequestParam @Positive Long leftVersionId,
            @RequestParam @Positive Long rightVersionId
    ) {
        return ApiResponse.success("Resume comparison loaded", resumeAnalysisService.compareAdmin(studentId, leftVersionId, rightVersionId));
    }
}