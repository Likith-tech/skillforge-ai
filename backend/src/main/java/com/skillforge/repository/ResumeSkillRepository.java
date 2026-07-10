package com.skillforge.repository;

import com.skillforge.model.ResumeSkill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeSkillRepository extends JpaRepository<ResumeSkill, Long> {

    List<ResumeSkill> findByParsedResumeIdOrderByIdAsc(Long parsedResumeId);
}