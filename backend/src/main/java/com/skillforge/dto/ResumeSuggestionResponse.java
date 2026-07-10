package com.skillforge.dto;

import com.skillforge.model.ResumeSuggestion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeSuggestionResponse {

    private Long id;
    private String severity;
    private String category;
    private String recommendation;
    private Integer expectedAtsImprovement;
    private Integer severityRank;

    public static ResumeSuggestionResponse from(ResumeSuggestion suggestion) {
        ResumeSuggestionResponse response = new ResumeSuggestionResponse();
        response.setId(suggestion.getId());
        response.setSeverity(suggestion.getSeverity());
        response.setCategory(suggestion.getCategory());
        response.setRecommendation(suggestion.getRecommendation());
        response.setExpectedAtsImprovement(suggestion.getExpectedAtsImprovement());
        response.setSeverityRank(suggestion.getSeverityRank());
        return response;
    }
}