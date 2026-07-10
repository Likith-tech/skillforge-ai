package com.skillforge.repository;

import com.skillforge.model.Resume;
import com.skillforge.model.ResumeStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findByStudentIdAndStatus(Long studentId, ResumeStatus status);

    boolean existsByStudentIdAndStatus(Long studentId, ResumeStatus status);
}
