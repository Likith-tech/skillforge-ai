package com.skillforge.dto;

import com.skillforge.model.ResumeAnalysis;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeScoreBreakdownResponse {

    private Integer overallScore;
    private Integer structureScore;
    private Integer formattingScore;
    private Integer skillsScore;
    private Integer educationScore;
    private Integer projectsScore;
    private Integer experienceScore;
    private Integer keywordsScore;
    private Integer readabilityScore;
    private Integer contactInformationScore;
    private Integer completenessScore;

    public static ResumeScoreBreakdownResponse from(ResumeAnalysis analysis) {
        ResumeScoreBreakdownResponse response = new ResumeScoreBreakdownResponse();
        response.setOverallScore(analysis.getOverallScore());
        response.setStructureScore(analysis.getStructureScore());
        response.setFormattingScore(analysis.getFormattingScore());
        response.setSkillsScore(analysis.getSkillsScore());
        response.setEducationScore(analysis.getEducationScore());
        response.setProjectsScore(analysis.getProjectsScore());
        response.setExperienceScore(analysis.getExperienceScore());
        response.setKeywordsScore(analysis.getKeywordsScore());
        response.setReadabilityScore(analysis.getReadabilityScore());
        response.setContactInformationScore(analysis.getContactInformationScore());
        response.setCompletenessScore(analysis.getCompletenessScore());
        return response;
    }
}