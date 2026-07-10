package com.skillforge.dto;

import com.skillforge.model.StudentSkillGap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillGapResponse {

    private Long id;
    private Long jobId;
    private Long recommendationId;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> partiallyMatchingSkills;
    private List<String> recommendedSkills;
    private Integer priorityScore;
    private String difficulty;
    private Integer estimatedLearningTimeHours;

    public static SkillGapResponse from(StudentSkillGap gap) {
        SkillGapResponse response = new SkillGapResponse();
        response.setId(gap.getId());
        response.setJobId(gap.getJob().getId());
        response.setRecommendationId(gap.getRecommendation().getId());
        response.setMatchedSkills(split(gap.getMatchedSkills()));
        response.setMissingSkills(split(gap.getMissingSkills()));
        response.setPartiallyMatchingSkills(split(gap.getPartiallyMatchingSkills()));
        response.setRecommendedSkills(split(gap.getRecommendedSkills()));
        response.setPriorityScore(gap.getPriorityScore());
        response.setDifficulty(gap.getDifficulty());
        response.setEstimatedLearningTimeHours(gap.getEstimatedLearningTimeHours());
        return response;
    }

    private static List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split("\\s*\\|\\s*"));
    }
}