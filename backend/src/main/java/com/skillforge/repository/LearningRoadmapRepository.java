package com.skillforge.repository;

import com.skillforge.model.LearningRoadmap;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningRoadmapRepository extends JpaRepository<LearningRoadmap, Long> {

    void deleteByStudentIdAndRecommendationId(Long studentId, Long recommendationId);

    @EntityGraph(attributePaths = {"job", "recommendation", "job.company", "job.category", "job.location"})
    List<LearningRoadmap> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    @EntityGraph(attributePaths = {"job", "recommendation", "job.company", "job.category", "job.location"})
    Optional<LearningRoadmap> findByStudentIdAndJobId(Long studentId, Long jobId);
}