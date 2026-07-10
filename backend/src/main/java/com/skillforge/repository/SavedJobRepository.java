package com.skillforge.repository;

import com.skillforge.model.SavedJob;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);

    Optional<SavedJob> findByStudentIdAndJobId(Long studentId, Long jobId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location", "job.skills"})
    List<SavedJob> findByStudentIdOrderBySavedAtDesc(Long studentId);
}