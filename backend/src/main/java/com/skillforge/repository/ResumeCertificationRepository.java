package com.skillforge.repository;

import com.skillforge.model.ResumeCertification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeCertificationRepository extends JpaRepository<ResumeCertification, Long> {

    List<ResumeCertification> findByParsedResumeIdOrderByIdAsc(Long parsedResumeId);
}