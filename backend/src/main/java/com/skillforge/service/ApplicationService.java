package com.skillforge.service;

import com.skillforge.dto.ApplicationRequest;
import com.skillforge.dto.ApplicationResponse;
import com.skillforge.dto.PageResponse;

public interface ApplicationService {

    ApplicationResponse apply(Long studentId, ApplicationRequest request);

    /** sort accepts: newest (default), oldest, highestScore, jobTitle. */
    PageResponse<ApplicationResponse> listForStudent(Long studentId, Integer page, Integer size, String sort);

    /** status may be null to return every status for the job. */
    PageResponse<ApplicationResponse> listForJob(Long jobId, String status, Integer page, Integer size, String sort);

    ApplicationResponse getById(Long applicationId);

    ApplicationResponse updateStatus(Long applicationId, String status);
}
