package com.skillforge.repository;

import com.skillforge.model.JobSkill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {

    List<JobSkill> findByJobIdOrderByPriorityRankAsc(Long jobId);

    List<JobSkill> findByJobIdIn(List<Long> jobIds);
}