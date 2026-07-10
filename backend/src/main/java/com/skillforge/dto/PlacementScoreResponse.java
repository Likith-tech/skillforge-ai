package com.skillforge.dto;

import com.skillforge.model.PlacementScore;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlacementScoreResponse {

    private Long id;
    private Integer overallScore;
    private Integer resumeScore;
    private Integer projectsScore;
    private Integer codingScore;
    private Integer communicationScore;
    private Integer experienceScore;
    private Integer certificationsScore;
    private Integer educationScore;
    private Integer recommendationFitScore;
    private String improvementSuggestions;
    private LocalDateTime createdAt;

    public static PlacementScoreResponse from(PlacementScore score) {
        PlacementScoreResponse response = new PlacementScoreResponse();
        response.setId(score.getId());
        response.setOverallScore(score.getOverallScore());
        response.setResumeScore(score.getResumeScore());
        response.setProjectsScore(score.getProjectsScore());
        response.setCodingScore(score.getCodingScore());
        response.setCommunicationScore(score.getCommunicationScore());
        response.setExperienceScore(score.getExperienceScore());
        response.setCertificationsScore(score.getCertificationsScore());
        response.setEducationScore(score.getEducationScore());
        response.setRecommendationFitScore(score.getRecommendationFitScore());
        response.setImprovementSuggestions(score.getImprovementSuggestions());
        response.setCreatedAt(score.getCreatedAt());
        return response;
    }
}