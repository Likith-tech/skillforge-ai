package com.skillforge.repository;

import com.skillforge.model.ResumeHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeHistoryRepository extends JpaRepository<ResumeHistory, Long> {

    List<ResumeHistory> findByStudentIdOrderByVersionNumberDesc(Long studentId);

    Optional<ResumeHistory> findByStudentIdAndResumeVersionId(Long studentId, Long resumeVersionId);
}