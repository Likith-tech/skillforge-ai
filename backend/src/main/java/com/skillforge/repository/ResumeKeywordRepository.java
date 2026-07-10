package com.skillforge.repository;

import com.skillforge.model.ResumeKeyword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeKeywordRepository extends JpaRepository<ResumeKeyword, Long> {

    List<ResumeKeyword> findByAnalysisIdOrderByPriorityRankAsc(Long analysisId);
}