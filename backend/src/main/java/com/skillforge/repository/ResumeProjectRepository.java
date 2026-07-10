package com.skillforge.repository;

import com.skillforge.model.ResumeProject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeProjectRepository extends JpaRepository<ResumeProject, Long> {

    List<ResumeProject> findByParsedResumeIdOrderByIdAsc(Long parsedResumeId);
}