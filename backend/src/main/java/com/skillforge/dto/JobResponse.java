package com.skillforge.dto;

import com.skillforge.model.Job;
import com.skillforge.model.JobSkill;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobResponse {

    private Long id;
    private Long recruiterId;
    private String title;
    private String description;
    private CompanyResponse company;
    private JobCategoryResponse category;
    private JobLocationResponse location;
    private String employmentType;
    private BigDecimal minimumCgpa;
    private Integer minimumExperienceYears;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    private LocalDate deadline;
    private String status;
    private List<JobSkillResponse> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static JobResponse from(Job job) {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setRecruiterId(job.getRecruiterId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setCompany(CompanyResponse.from(job.getCompany()));
        response.setCategory(JobCategoryResponse.from(job.getCategory()));
        response.setLocation(JobLocationResponse.from(job.getLocation()));
        response.setEmploymentType(job.getEmploymentType().name());
        response.setMinimumCgpa(job.getMinimumCgpa());
        response.setMinimumExperienceYears(job.getMinimumExperienceYears());
        response.setSalaryMin(job.getSalaryMin());
        response.setSalaryMax(job.getSalaryMax());
        response.setCurrency(job.getCurrency());
        response.setDeadline(job.getDeadline());
        response.setStatus(job.getStatus().name());
        response.setSkills(job.getSkills().stream().map(JobSkillResponse::from).toList());
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());
        return response;
    }
}