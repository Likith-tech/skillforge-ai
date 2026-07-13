package com.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String company;
    private Long companyId;
    private String companyWebsite;
    private String companyLogo;
    private String location;
    private String type;
    private String experienceLevel;
    private String salaryRange;
    private String status;
    private Long postedById;
    private String postedByName;
    private Set<String> requiredSkills;
    private LocalDateTime createdAt;
}
