package com.skillforge.dto;

import com.skillforge.model.EmploymentType;
import com.skillforge.model.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String companyName;

    @NotBlank
    private String description;

    @NotBlank
    private String categoryName;

    @NotBlank
    private String locationDisplayName;

    @NotNull
    private EmploymentType employmentType;

    private BigDecimal minimumCgpa;
    private Integer minimumExperienceYears;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency = "INR";
    private LocalDate deadline;
    private JobStatus status = JobStatus.ACTIVE;
    private List<JobSkillRequest> requiredSkills;
    private List<JobSkillRequest> preferredSkills;

    @Getter
    @Setter
    public static class JobSkillRequest {
        @NotBlank
        private String skillName;
        private Integer priorityRank = 1;
    }
}