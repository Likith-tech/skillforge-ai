package com.skillforge.service;

import com.skillforge.dto.CompanyResponse;
import com.skillforge.dto.JobResponse;
import com.skillforge.dto.SearchResponse;
import com.skillforge.dto.UserResponse;
import com.skillforge.model.Company;
import com.skillforge.model.Job;
import com.skillforge.model.Role;
import com.skillforge.model.ResumeSkill;
import com.skillforge.model.User;
import com.skillforge.repository.CompanyRepository;
import com.skillforge.repository.JobRepository;
import com.skillforge.repository.ResumeSkillRepository;
import com.skillforge.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final ResumeSkillRepository resumeSkillRepository;

    public SearchService(UserRepository userRepository, JobRepository jobRepository, CompanyRepository companyRepository, ResumeSkillRepository resumeSkillRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.resumeSkillRepository = resumeSkillRepository;
    }

    public SearchResponse search(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        SearchResponse response = new SearchResponse();
        response.setStudents(userRepository.findByRole(Role.STUDENT).stream().filter(user -> contains(user, normalized)).map(UserResponse::from).toList());
        response.setRecruiters(userRepository.findByRole(Role.RECRUITER).stream().filter(user -> contains(user, normalized)).map(UserResponse::from).toList());
        response.setJobs(jobRepository.findAll().stream().filter(job -> contains(job, normalized)).map(JobResponse::from).toList());
        response.setCompanies(companyRepository.findAll().stream().filter(company -> contains(company, normalized)).map(CompanyResponse::from).toList());
        response.setSkills(resumeSkillRepository.findAll().stream().map(ResumeSkill::getSkillName).filter(skill -> skill.toLowerCase(Locale.ROOT).contains(normalized)).distinct().toList());
        return response;
    }

    private boolean contains(User user, String query) {
        return query.isBlank() || matches(user.getName(), query) || matches(user.getEmail(), query) || matches(user.getRole().name(), query);
    }

    private boolean contains(Job job, String query) {
        return query.isBlank() || matches(job.getTitle(), query) || matches(job.getDescription(), query) || matches(job.getCompany().getName(), query) || matches(job.getCategory().getName(), query) || matches(job.getLocation().getDisplayName(), query);
    }

    private boolean contains(Company company, String query) {
        return query.isBlank() || matches(company.getName(), query) || matches(company.getIndustry(), query) || matches(company.getDescription(), query);
    }

    private boolean matches(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }
}