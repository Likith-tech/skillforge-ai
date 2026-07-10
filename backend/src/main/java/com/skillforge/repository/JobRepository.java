package com.skillforge.repository;

import com.skillforge.model.Job;
import com.skillforge.model.JobStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    @EntityGraph(attributePaths = {"company", "category", "location", "skills"})
    Optional<Job> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"company", "category", "location"})
    List<Job> findByRecruiterIdOrderByCreatedAtDesc(Long recruiterId);

    @EntityGraph(attributePaths = {"company", "category", "location"})
    List<Job> findByStatusOrderByCreatedAtDesc(JobStatus status);

    @EntityGraph(attributePaths = {"company", "category", "location", "skills"})
    @Query("select j from Job j where j.status = :status")
    List<Job> findAllActiveDetailed(@Param("status") JobStatus status);

    @Query("select j from Job j where j.id in :ids")
    List<Job> findAllByIdIn(@Param("ids") List<Long> ids);

    boolean existsByIdAndRecruiterId(Long id, Long recruiterId);
}