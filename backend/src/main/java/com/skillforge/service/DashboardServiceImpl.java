package com.skillforge.service;

import com.skillforge.dto.AdminAnalyticsSummary;
import com.skillforge.dto.RecruiterDashboardStats;
import com.skillforge.dto.StudentDashboardStats;
import com.skillforge.model.JobStatus;
import com.skillforge.model.RoleName;
import com.skillforge.repository.ApplicationRepository;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.ResumeRepository;
import com.skillforge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ResumeRepository resumeRepository;
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardStats getStudentStats(Long studentId) {
        var resume = resumeRepository.findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(studentId);

        return StudentDashboardStats.builder()
                .resumeUploaded(resume.isPresent())
                .atsScore(resume.map(com.skillforge.model.Resume::getAtsScore).orElse(null))
                .applicationsCount(applicationRepository.countByStudentId(studentId))
                .openJobsCount(jobRepository.countByStatus(JobStatus.OPEN))
                .build();
    }

    @Override
    public RecruiterDashboardStats getRecruiterStats(Long recruiterId) {
        Map<String, Long> byStatus = new HashMap<>();
        applicationRepository.countByStatusForRecruiter(recruiterId)
                .forEach(row -> byStatus.put(row.getStatus().name(), row.getTotal()));

        return RecruiterDashboardStats.builder()
                .jobsPostedCount(jobRepository.countByPostedById(recruiterId))
                .totalApplications(applicationRepository.countByJob_PostedById(recruiterId))
                .applicationsByStatus(byStatus)
                .build();
    }

    @Override
    public AdminAnalyticsSummary getAdminSummary() {
        return AdminAnalyticsSummary.builder()
                .totalUsers(userRepository.count())
                .totalStudents(userRepository.countByRoles_Name(RoleName.STUDENT))
                .totalRecruiters(userRepository.countByRoles_Name(RoleName.RECRUITER))
                .totalAdmins(userRepository.countByRoles_Name(RoleName.ADMIN))
                .totalJobs(jobRepository.count())
                .totalOpenJobs(jobRepository.countByStatus(JobStatus.OPEN))
                .totalApplications(applicationRepository.count())
                .averageAtsScore(resumeRepository.averageAtsScore())
                .build();
    }
}
