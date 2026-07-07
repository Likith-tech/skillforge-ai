package com.skillforge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String collegeName;
    private String degree;
    private String branch;
    private Integer graduationYear;
    private String skills;
    private String experience;
    private String projects;
    private String certifications;
    private String githubUrl;
    private String linkedinUrl;
}
