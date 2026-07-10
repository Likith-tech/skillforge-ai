package com.skillforge.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeAnalyticsResponse {

    private Integer overallAts;
    private Integer skillsCount;
    private Integer projectsCount;
    private Integer experienceCount;
    private Integer educationCount;
    private Integer certificationsCount;
    private Integer languagesCount;
    private Integer technicalSkillsCount;
    private Integer softSkillsCount;
    private Integer completenessScore;
    private List<TrendPoint> atsTrend;
    private List<DistributionPoint> skillsDistribution;
    private List<DistributionPoint> completenessBreakdown;

    @Getter
    @Setter
    public static class TrendPoint {
        private String label;
        private Integer value;
    }

    @Getter
    @Setter
    public static class DistributionPoint {
        private String label;
        private Integer value;
        private String color;
    }
}