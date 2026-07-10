package com.skillforge.dto;

import com.skillforge.model.StudentJobRecommendation;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRecommendationResponse {

    private Long id;
    private Long jobId;
    private JobResponse job;
    private Integer matchPercentage;
    private Integer recommendationScore;
    private Integer recommendationConfidence;
    private String reasonForRecommendation;
    private List<String> matchingSkills;
    private List<String> missingRequirements;
    private List<String> strengths;
    private List<String> weaknesses;
    private BigDecimal expectedSalaryMin;
    private BigDecimal expectedSalaryMax;

    public static JobRecommendationResponse from(StudentJobRecommendation recommendation) {
        JobRecommendationResponse response = new JobRecommendationResponse();
        response.setId(recommendation.getId());
        response.setJobId(recommendation.getJob().getId());
        response.setJob(JobResponse.from(recommendation.getJob()));
        response.setMatchPercentage(recommendation.getMatchPercentage());
        response.setRecommendationScore(recommendation.getRecommendationScore());
        response.setRecommendationConfidence(recommendation.getRecommendationConfidence());
        response.setReasonForRecommendation(recommendation.getReasonForRecommendation());
        response.setMatchingSkills(splitList(recommendation.getMatchingSkills()));
        response.setMissingRequirements(splitList(recommendation.getMissingRequirements()));
        response.setStrengths(splitList(recommendation.getStrengths()));
        response.setWeaknesses(splitList(recommendation.getWeaknesses()));
        response.setExpectedSalaryMin(recommendation.getExpectedSalaryMin());
        response.setExpectedSalaryMax(recommendation.getExpectedSalaryMax());
        return response;
    }

    private static List<String> splitList(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split("\\s*\\|\\s*"));
    }
}