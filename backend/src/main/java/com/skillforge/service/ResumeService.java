package com.skillforge.service;

import com.skillforge.dto.PageResponse;
import com.skillforge.dto.ResumeFileDownload;
import com.skillforge.dto.ResumeResponse;
import com.skillforge.dto.ResumeSummaryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

    ResumeResponse upload(Long userId, MultipartFile file);

    ResumeResponse getLatestForUser(Long userId);

    /** Fetches any resume by its own id; callers enforce ownership/role rules. */
    ResumeResponse getById(Long resumeId);

    /** sort accepts: newest (default), oldest, highestScore. */
    PageResponse<ResumeSummaryResponse> listHistory(Long userId, Integer page, Integer size, String sort);

    /** Soft-deletes a resume; scoped to the owning user so it 404s on anyone else's resume. */
    void deleteResume(Long userId, Long resumeId);

    /** Loads any resume's file by its own id; callers enforce ownership/role rules. */
    ResumeFileDownload loadFile(Long resumeId);

    /**
     * Strict ownership gate: throws AccessDeniedException unless userId owns resumeId.
     * Used for actions only the resume's own student may perform (e.g. triggering an ATS analysis).
     */
    void assertOwner(Long resumeId, Long userId);

    /**
     * Read-access gate: throws AccessDeniedException unless the requester is an admin,
     * the resume's own student, or a recruiter who has received an application built on
     * this exact resume. This is the authorization rule for viewing/downloading a resume
     * or its ATS report - enforced here (service layer) rather than only in controllers,
     * so every current and future caller gets it for free.
     */
    void assertViewable(Long resumeId, Long requesterId, boolean isAdmin);
}
