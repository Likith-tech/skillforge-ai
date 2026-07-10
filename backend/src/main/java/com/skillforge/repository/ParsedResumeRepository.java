package com.skillforge.repository;

import com.skillforge.model.ParsedResume;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParsedResumeRepository extends JpaRepository<ParsedResume, Long> {

    Optional<ParsedResume> findByResumeVersionId(Long resumeVersionId);

    Optional<ParsedResume> findTopByResumeIdOrderByIdDesc(Long resumeId);
}