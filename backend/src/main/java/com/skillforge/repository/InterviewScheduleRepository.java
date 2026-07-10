package com.skillforge.repository;

import com.skillforge.model.InterviewSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    List<InterviewSchedule> findByStudentIdOrderByInterviewAtDesc(Long studentId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    List<InterviewSchedule> findByStudentIdAndInterviewAtAfterOrderByInterviewAtAsc(Long studentId, java.time.LocalDateTime interviewAt);

    @EntityGraph(attributePaths = {"job", "job.company", "job.category", "job.location"})
    List<InterviewSchedule> findByJobIdOrderByInterviewAtDesc(Long jobId);
}