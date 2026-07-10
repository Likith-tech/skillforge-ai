package com.skillforge.repository;

import com.skillforge.model.ResumeSuggestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeSuggestionRepository extends JpaRepository<ResumeSuggestion, Long> {

    List<ResumeSuggestion> findByAnalysisIdOrderBySeverityRankAsc(Long analysisId);
}