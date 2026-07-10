package com.skillforge.repository;

import com.skillforge.model.StudentSkillGap;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentSkillGapRepository extends JpaRepository<StudentSkillGap, Long> {

    void deleteByStudentIdAndJobId(Long studentId, Long jobId);

    @EntityGraph(attributePaths = {"job", "recommendation", "job.company", "job.category", "job.location"})
    Optional<StudentSkillGap> findByStudentIdAndJobId(Long studentId, Long jobId);

    @EntityGraph(attributePaths = {"job", "recommendation", "job.company", "job.category", "job.location"})
    List<StudentSkillGap> findByStudentIdOrderByPriorityScoreDesc(Long studentId);
}