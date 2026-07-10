package com.skillforge.repository;

import com.skillforge.model.ResumeExperience;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeExperienceRepository extends JpaRepository<ResumeExperience, Long> {

    List<ResumeExperience> findByParsedResumeIdOrderByIdAsc(Long parsedResumeId);
}