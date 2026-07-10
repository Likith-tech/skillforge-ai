package com.skillforge.dto;

import com.skillforge.model.ResumeHistory;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeHistoryEntryResponse {

    private Long id;
    private Long resumeId;
    private Long resumeVersionId;
    private Long studentId;
    private Integer versionNumber;
    private LocalDateTime uploadedAt;
    private Integer atsScore;
    private Long previousVersionId;
    private Integer previousAtsScore;
    private String changesSummary;
    private LocalDateTime analyzedAt;

    public static ResumeHistoryEntryResponse from(ResumeHistory history) {
        ResumeHistoryEntryResponse response = new ResumeHistoryEntryResponse();
        response.setId(history.getId());
        response.setResumeId(history.getResumeId());
        response.setResumeVersionId(history.getResumeVersionId());
        response.setStudentId(history.getStudentId());
        response.setVersionNumber(history.getVersionNumber());
        response.setUploadedAt(history.getUploadedAt());
        response.setAtsScore(history.getAtsScore());
        response.setPreviousVersionId(history.getPreviousVersionId());
        response.setPreviousAtsScore(history.getPreviousAtsScore());
        response.setChangesSummary(history.getChangesSummary());
        response.setAnalyzedAt(history.getAnalyzedAt());
        return response;
    }
}