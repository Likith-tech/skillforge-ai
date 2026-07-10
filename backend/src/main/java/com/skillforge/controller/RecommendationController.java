package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.dto.JobGapRecommendationResponse;
import com.skillforge.dto.JobRecommendationResponse;
import com.skillforge.dto.LearningRoadmapResponse;
import com.skillforge.dto.PlacementScoreResponse;
import com.skillforge.dto.SkillGapResponse;
import com.skillforge.dto.StudentDashboardResponse;
import com.skillforge.security.UserPrincipal;
import com.skillforge.service.JobRecommendationService;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students/me")
@PreAuthorize("hasRole('STUDENT')")
public class RecommendationController {

    private final JobRecommendationService recommendationService;

    public RecommendationController(JobRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommendations/refresh")
    public ApiResponse<List<JobRecommendationResponse>> refresh(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Recommendations refreshed", recommendationService.refreshRecommendations(principal.getId()));
    }

    @GetMapping("/recommendations")
    public ApiResponse<List<JobRecommendationResponse>> recommendations(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Recommendations loaded", recommendationService.getRecommendations(principal.getId()));
    }

    @GetMapping("/recommendations/{jobId}")
    public ApiResponse<JobRecommendationResponse> recommendation(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId) {
        return ApiResponse.success("Recommendation loaded", recommendationService.getRecommendation(principal.getId(), jobId));
    }

    @GetMapping("/skill-gaps")
    public ApiResponse<List<SkillGapResponse>> skillGaps(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Skill gaps loaded", recommendationService.getSkillGaps(principal.getId()));
    }

    @GetMapping("/skill-gaps/{jobId}")
    public ApiResponse<SkillGapResponse> skillGap(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId) {
        return ApiResponse.success("Skill gap loaded", recommendationService.getSkillGap(principal.getId(), jobId));
    }

    @GetMapping("/placement-score")
    public ApiResponse<PlacementScoreResponse> placementScore(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Placement score loaded", recommendationService.getPlacementScore(principal.getId()));
    }

    @GetMapping("/roadmaps")
    public ApiResponse<List<LearningRoadmapResponse>> roadmaps(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Roadmaps loaded", recommendationService.getRoadmaps(principal.getId()));
    }

    @GetMapping("/roadmaps/{jobId}")
    public ApiResponse<LearningRoadmapResponse> roadmap(@AuthenticationPrincipal UserPrincipal principal, @PathVariable @Positive Long jobId) {
        return ApiResponse.success("Roadmap loaded", recommendationService.getRoadmap(principal.getId(), jobId));
    }

    @GetMapping("/dashboard")
    public ApiResponse<StudentDashboardResponse> dashboard(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Dashboard loaded", recommendationService.getStudentDashboard(principal.getId()));
    }

    @GetMapping("/insights")
    public ApiResponse<List<JobGapRecommendationResponse>> insights(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success("Job insights loaded", recommendationService.getJobInsights(principal.getId()));
    }
}