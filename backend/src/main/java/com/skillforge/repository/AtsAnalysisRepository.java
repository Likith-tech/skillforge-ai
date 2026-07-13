package com.skillforge.repository;

import com.skillforge.model.AtsAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtsAnalysisRepository extends JpaRepository<AtsAnalysis, Long> {

    Optional<AtsAnalysis> findFirstByResumeIdOrderByCreatedAtDesc(Long resumeId);

    /** toHistoryItem() reads resume.id/originalFileName per row - fetch it in the same query. */
    @EntityGraph(attributePaths = {"resume"})
    Page<AtsAnalysis> findByUserId(Long userId, Pageable pageable);

    Optional<AtsAnalysis> findByIdAndUserId(Long id, Long userId);
}
