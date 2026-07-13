package com.skillforge.service;

import com.skillforge.dto.JobRequest;
import com.skillforge.dto.JobResponse;
import com.skillforge.dto.PageResponse;
import com.skillforge.dto.SavedJobResponse;
import com.skillforge.model.Job;

import java.util.List;

public interface JobService {

    JobResponse createJob(Long recruiterId, JobRequest request);

    /**
     * Any filter parameter may be null/blank to skip it. Only OPEN jobs are searched.
     * sort accepts: newest (default), oldest, companyName, jobTitle, salary. "salary"
     * sorts lexicographically since salaryRange is free text (e.g. "8-12 LPA"), not a
     * numeric column - there's no schema field to sort on numerically.
     */
    PageResponse<JobResponse> searchJobs(String keyword, String location, String type, String experienceLevel,
                                          String skill, String company, Integer page, Integer size, String sort);

    PageResponse<JobResponse> listJobsForRecruiter(Long recruiterId, Integer page, Integer size, String sort);

    /** Admin oversight: every job regardless of status. */
    PageResponse<JobResponse> listAllJobs(Integer page, Integer size, String sort);

    JobResponse getJob(Long jobId);

    /** Maps an already-loaded entity to its DTO, for callers (JobMatchingService) that already hold the Job. */
    JobResponse toDto(Job job);

    JobResponse updateJob(Long jobId, JobRequest request);

    /** Hard-deletes the job, unless applications already reference it (see BadRequestException message). */
    void deleteJob(Long jobId);

    JobResponse setStatus(Long jobId, String status);

    void saveJob(Long studentId, Long jobId);

    void unsaveJob(Long studentId, Long jobId);

    List<SavedJobResponse> listSavedJobs(Long studentId);

    /** Internal accessor for other services (Application, JobMatching) that need the entity, not the DTO. */
    Job getJobEntityOrThrow(Long jobId);
}
