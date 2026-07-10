package com.skillforge.controller;

import com.skillforge.dto.ApiResponse;
import com.skillforge.dto.JobRecommendationResponse;
import com.skillforge.dto.LearningRoadmapResponse;
import com.skillforge.dto.PlacementScoreResponse;
import com.skillforge.dto.SkillGapResponse;
import com.skillforge.dto.StudentDashboardResponse;
import com.skillforge.service.JobRecommendationService;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/students/{studentId}")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStudentRecommendationController {

    private final JobRecommendationService recommendationService;

    public AdminStudentRecommendationController(JobRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommendations/refresh")
    public ApiResponse<List<JobRecommendationResponse>> refresh(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Recommendations refreshed", recommendationService.refreshRecommendations(studentId));
    }

    @GetMapping("/recommendations")
    public ApiResponse<List<JobRecommendationResponse>> recommendations(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Recommendations loaded", recommendationService.getRecommendations(studentId));
    }

    @GetMapping("/skill-gaps")
    public ApiResponse<List<SkillGapResponse>> skillGaps(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Skill gaps loaded", recommendationService.getSkillGaps(studentId));
    }

    @GetMapping("/placement-score")
    public ApiResponse<PlacementScoreResponse> placementScore(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Placement score loaded", recommendationService.getPlacementScore(studentId));
    }

    @GetMapping("/roadmaps")
    public ApiResponse<List<LearningRoadmapResponse>> roadmaps(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Roadmaps loaded", recommendationService.getRoadmaps(studentId));
    }

    @GetMapping("/dashboard")
    public ApiResponse<StudentDashboardResponse> dashboard(@PathVariable @Positive Long studentId) {
        return ApiResponse.success("Dashboard loaded", recommendationService.getStudentDashboard(studentId));
    }
}