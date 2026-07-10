package com.skillforge.repository;

import com.skillforge.model.ResumeLanguage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeLanguageRepository extends JpaRepository<ResumeLanguage, Long> {

    List<ResumeLanguage> findByParsedResumeIdOrderByIdAsc(Long parsedResumeId);
}