package com.skillforge.repository;

import com.skillforge.model.ResumeAchievement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeAchievementRepository extends JpaRepository<ResumeAchievement, Long> {

    List<ResumeAchievement> findByParsedResumeIdOrderByIdAsc(Long parsedResumeId);
}