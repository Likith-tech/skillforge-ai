package com.skillforge.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeAnalysisDashboardResponse {

    private ResumeResponse resume;
    private ResumeVersionResponse resumeVersion;
    private ResumeParsedDataResponse parsedResume;
    private ResumeScoreBreakdownResponse scores;
    private ResumeKeywordAnalysisResponse keywords;
    private List<ResumeSuggestionResponse> suggestions;
    private ResumeAnalyticsResponse analytics;
    private List<ResumeHistoryEntryResponse> history;
    private ResumeComparisonResponse latestComparison;
}