package com.skillforge.service;

import com.skillforge.dto.JobMatchResponse;
import com.skillforge.model.Job;
import com.skillforge.model.Skill;

import java.util.List;
import java.util.Set;

public interface JobMatchingService {

    List<JobMatchResponse> getRecommendedJobs(Long studentId);

    /** Skill-overlap percentage (0-100) between a job's required skills and a candidate's skills. */
    int computeMatchScore(Job job, Set<Skill> candidateSkills);

    Set<Skill> computeMissingSkills(Job job, Set<Skill> candidateSkills);
}
