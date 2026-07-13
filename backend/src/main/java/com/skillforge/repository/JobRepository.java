package com.skillforge.repository;

import com.skillforge.model.Job;
import com.skillforge.model.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    /**
     * postedBy is a single ManyToOne and requiredSkills is the only collection being
     * fetched, so this entity graph is safe to combine with pagination elsewhere -
     * Hibernate only risks "cannot simultaneously fetch multiple bags" when two or
     * more *collection* attributes are eagerly joined in the same query.
     */
    @EntityGraph(attributePaths = {"postedBy", "requiredSkills"})
    List<Job> findByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"postedBy", "requiredSkills"})
    List<Job> findByStatusOrderByCreatedAtDesc(JobStatus status);

    Page<Job> findByPostedById(Long recruiterId, Pageable pageable);

    long countByPostedById(Long recruiterId);

    long countByStatus(JobStatus status);
}
