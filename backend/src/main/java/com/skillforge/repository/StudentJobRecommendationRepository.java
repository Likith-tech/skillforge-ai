package com.skillforge.repository;

import com.skillforge.model.StudentJobRecommendation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentJobRecommendationRepository extends JpaRepository<StudentJobRecommendation, Long> {

    void deleteByStudentIdAndResumeAnalysisId(Long studentId, Long resumeAnalysisId);

    boolean existsByStudentIdAndResumeAnalysisId(Long studentId, Long resumeAnalysisId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location", "job.skills"})
    List<StudentJobRecommendation> findByStudentIdAndResumeAnalysisIdOrderByMatchPercentageDesc(Long studentId, Long resumeAnalysisId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location", "job.skills"})
    List<StudentJobRecommendation> findByStudentIdOrderByMatchPercentageDesc(Long studentId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location", "job.skills"})
    Optional<StudentJobRecommendation> findByStudentIdAndJobId(Long studentId, Long jobId);
}