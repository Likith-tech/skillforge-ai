package com.skillforge.service;

import com.skillforge.dto.AtsHistoryItemResponse;
import com.skillforge.dto.AtsReportResponse;
import com.skillforge.dto.PageResponse;

public interface AtsReportService {

    /** Runs a fresh analysis for the resume and persists it as a new history entry. */
    AtsReportResponse analyze(Long resumeId, String targetRoleRaw);

    /** Returns the most recent report for a resume, generating a generic one first if none exists yet. */
    AtsReportResponse getLatestReport(Long resumeId);

    /** sort accepts: newest (default), oldest, highestScore. */
    PageResponse<AtsHistoryItemResponse> listHistory(Long userId, Integer page, Integer size, String sort);

    void deleteHistoryEntry(Long userId, Long analysisId);
}
