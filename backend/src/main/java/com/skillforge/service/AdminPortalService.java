package com.skillforge.service;

import com.skillforge.dto.PortalStatsResponse;
import com.skillforge.model.ApplicationStatus;
import com.skillforge.model.JobStatus;
import com.skillforge.model.Role;
import com.skillforge.repository.CompanyRepository;
import com.skillforge.repository.JobApplicationRepository;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.ResumeAnalysisRepository;
import com.skillforge.repository.StudentDashboardMetricRepository;
import com.skillforge.repository.StudentProfileRepository;
import com.skillforge.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdminPortalService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final StudentDashboardMetricRepository studentDashboardMetricRepository;

    public AdminPortalService(UserRepository userRepository, StudentProfileRepository studentProfileRepository, CompanyRepository companyRepository, JobRepository jobRepository, ResumeAnalysisRepository resumeAnalysisRepository, JobApplicationRepository jobApplicationRepository, StudentDashboardMetricRepository studentDashboardMetricRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.studentDashboardMetricRepository = studentDashboardMetricRepository;
    }

    public PortalStatsResponse dashboard() {
        PortalStatsResponse response = new PortalStatsResponse();
        List<PortalStatsResponse.KeyValuePoint> cards = new ArrayList<>();
        cards.add(point("Students", (long) userRepository.findByRole(Role.STUDENT).size()));
        cards.add(point("Recruiters", (long) userRepository.findByRole(Role.RECRUITER).size()));
        cards.add(point("Admins", (long) userRepository.findByRole(Role.ADMIN).size()));
        cards.add(point("Profiles", (long) studentProfileRepository.count()));
        cards.add(point("Companies", companyRepository.count()));
        cards.add(point("Jobs", jobRepository.count()));
        cards.add(point("Active Jobs", jobRepository.findByStatusOrderByCreatedAtDesc(JobStatus.ACTIVE).size()));
        cards.add(point("Resume Analyses", resumeAnalysisRepository.count()));
        cards.add(point("Applications", jobApplicationRepository.count()));
        response.setCards(cards);

        List<PortalStatsResponse.KeyValuePoint> charts = new ArrayList<>();
        charts.add(point("Applied", jobApplicationRepository.countByStudentIdAndStatus(0L, ApplicationStatus.APPLIED)));
        charts.add(point("Shortlisted", jobApplicationRepository.countByStudentIdAndStatus(0L, ApplicationStatus.SHORTLISTED)));
        charts.add(point("Interview", jobApplicationRepository.countByStudentIdAndStatus(0L, ApplicationStatus.INTERVIEW)));
        charts.add(point("Offer", jobApplicationRepository.countByStudentIdAndStatus(0L, ApplicationStatus.OFFER)));
        response.setCharts(charts);
        response.setHighlights(List.of(
                "Student dashboard metrics stored: " + studentDashboardMetricRepository.count(),
                "Resume analysis records: " + resumeAnalysisRepository.count(),
                "All portal data is coming from PostgreSQL"
        ));
        return response;
    }

    private PortalStatsResponse.KeyValuePoint point(String label, long value) {
        PortalStatsResponse.KeyValuePoint point = new PortalStatsResponse.KeyValuePoint();
        point.setLabel(label);
        point.setValue(value);
        return point;
    }
}