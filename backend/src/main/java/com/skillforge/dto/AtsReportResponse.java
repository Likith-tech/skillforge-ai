package com.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsReportResponse {
    private Long id;
    private Long resumeId;
    private String targetRole;
    private int overallScore;
    private String strengthLabel;
    private AtsSubScores subScores;
    private String extractedName;
    private String extractedEmail;
    private String extractedPhone;
    private Map<String, Integer> skillBreakdown;
    private List<AtsMissingSkillResponse> missingSkills;
    private List<String> suggestions;
    private LocalDateTime analyzedAt;
}
