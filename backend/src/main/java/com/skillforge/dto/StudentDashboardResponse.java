package com.skillforge.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDashboardResponse {

    private List<JobRecommendationResponse> recommendedJobs;
    private PlacementScoreResponse placementScore;
    private SkillGapResponse topSkillGap;
    private Long appliedJobsCount;
    private String resumeStatus;
    private List<TrendPoint> atsTrend;
    private List<TrendPoint> placementTrend;
    private List<TrendPoint> skillsDistribution;
    private List<TrendPoint> applicationsByStatus;
    private List<String> recentActivities;

    @Getter
    @Setter
    public static class TrendPoint {
        private String label;
        private Integer value;
        private String color;
    }
}