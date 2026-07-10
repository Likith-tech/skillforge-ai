package com.skillforge.repository;

import com.skillforge.model.ResumeEducation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeEducationRepository extends JpaRepository<ResumeEducation, Long> {

    List<ResumeEducation> findByParsedResumeIdOrderByIdAsc(Long parsedResumeId);
}