package com.skillforge.service;

import com.skillforge.dto.JobMatchResponse;
import com.skillforge.model.Job;
import com.skillforge.model.JobStatus;
import com.skillforge.model.Resume;
import com.skillforge.model.Skill;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobMatchingServiceImpl implements JobMatchingService {

    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final JobService jobService;

    @Override
    @Transactional(readOnly = true)
    public List<JobMatchResponse> getRecommendedJobs(Long studentId) {
        Set<Skill> candidateSkills = resumeRepository.findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(studentId)
                .map(Resume::getSkills)
                .orElse(Set.of());

        // findByStatusOrderByCreatedAtDesc carries an @EntityGraph fetching postedBy +
        // requiredSkills, so job is already fully hydrated here - toDto does a pure
        // in-memory mapping instead of the extra per-job findById() this used to do.
        List<Job> openJobs = jobRepository.findByStatusOrderByCreatedAtDesc(JobStatus.OPEN);

        return openJobs.stream()
                .map(job -> JobMatchResponse.builder()
                        .job(jobService.toDto(job))
                        .matchScore(computeMatchScore(job, candidateSkills))
                        .missingSkills(computeMissingSkills(job, candidateSkills).stream()
                                .map(Skill::getName)
                                .collect(Collectors.toSet()))
                        .build())
                .sorted(Comparator.comparingInt(JobMatchResponse::getMatchScore).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public int computeMatchScore(Job job, Set<Skill> candidateSkills) {
        Set<Skill> required = job.getRequiredSkills();
        if (required.isEmpty()) {
            return 0;
        }
        long overlap = required.stream().filter(candidateSkills::contains).count();
        return Math.round((overlap / (float) required.size()) * 100);
    }

    @Override
    public Set<Skill> computeMissingSkills(Job job, Set<Skill> candidateSkills) {
        Set<Skill> missing = new HashSet<>(job.getRequiredSkills());
        missing.removeAll(candidateSkills);
        return missing;
    }
}
