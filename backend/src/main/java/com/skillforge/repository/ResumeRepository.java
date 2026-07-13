package com.skillforge.repository;

import com.skillforge.model.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    /** The student's current resume: most recent non-deleted upload. */
    Optional<Resume> findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    /** Paginated upload history (excluding soft-deleted rows); ordering comes from the caller's Pageable. */
    Page<Resume> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    /** Ownership-scoped lookup for a specific resume, used by get/download/delete. */
    Optional<Resume> findByIdAndUserId(Long id, Long userId);

    @Query("select avg(r.atsScore) from Resume r where r.atsScore is not null")
    Double averageAtsScore();
}
