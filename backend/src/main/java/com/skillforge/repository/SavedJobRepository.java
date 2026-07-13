package com.skillforge.repository;

import com.skillforge.model.SavedJob;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    @EntityGraph(attributePaths = {"job", "job.postedBy", "job.requiredSkills"})
    List<SavedJob> findByStudentIdOrderBySavedAtDesc(Long studentId);

    Optional<SavedJob> findByStudentIdAndJobId(Long studentId, Long jobId);

    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);
}
