package com.skillforge.repository;

import com.skillforge.model.OfferLetter;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferLetterRepository extends JpaRepository<OfferLetter, Long> {

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    List<OfferLetter> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    List<OfferLetter> findByStudentIdAndJobIdIn(Long studentId, List<Long> jobIds);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    List<OfferLetter> findByJobIdIn(List<Long> jobIds);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    Optional<OfferLetter> findByStudentIdAndJobId(Long studentId, Long jobId);
}