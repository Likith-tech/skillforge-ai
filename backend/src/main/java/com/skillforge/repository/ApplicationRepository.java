package com.skillforge.repository;

import com.skillforge.model.Application;
import com.skillforge.model.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /** job/student/resume are all single-valued (ManyToOne) - safe to fetch-join together with pagination. */
    @EntityGraph(attributePaths = {"job", "student", "resume"})
    Page<Application> findByStudentId(Long studentId, Pageable pageable);

    @EntityGraph(attributePaths = {"job", "student", "resume"})
    Page<Application> findByJobId(Long jobId, Pageable pageable);

    @EntityGraph(attributePaths = {"job", "student", "resume"})
    Page<Application> findByJobIdAndStatus(Long jobId, ApplicationStatus status, Pageable pageable);

    boolean existsByJobIdAndStudentId(Long jobId, Long studentId);

    boolean existsByResumeId(Long resumeId);

    /** True if the given recruiter has received at least one application built on this resume. */
    boolean existsByResume_IdAndJob_PostedById(Long resumeId, Long recruiterId);

    boolean existsByJobId(Long jobId);

    long countByStudentId(Long studentId);

    long countByJob_PostedById(Long recruiterId);

    @Query("select a.status as status, count(a) as total from Application a where a.job.postedBy.id = :recruiterId group by a.status")
    List<StatusCount> countByStatusForRecruiter(@Param("recruiterId") Long recruiterId);

    interface StatusCount {
        ApplicationStatus getStatus();
        Long getTotal();
    }
}
