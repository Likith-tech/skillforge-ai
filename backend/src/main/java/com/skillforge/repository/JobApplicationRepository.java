package com.skillforge.repository;

import com.skillforge.model.ApplicationStatus;
import com.skillforge.model.JobApplication;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);

    Optional<JobApplication> findByStudentIdAndJobId(Long studentId, Long jobId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    List<JobApplication> findByStudentIdOrderByAppliedAtDesc(Long studentId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    List<JobApplication> findByJobIdOrderByAppliedAtDesc(Long jobId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    List<JobApplication> findByJobIdIn(List<Long> jobIds);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, ApplicationStatus status);

    long countByJobIdAndStatus(Long jobId, ApplicationStatus status);
}