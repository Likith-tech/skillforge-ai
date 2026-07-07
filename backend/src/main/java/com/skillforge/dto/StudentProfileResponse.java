package com.skillforge.dto;

import com.skillforge.model.StudentProfile;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProfileResponse {

    private Long id;
    private Long userId;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StudentProfileResponse from(StudentProfile profile) {
        StudentProfileResponse response = new StudentProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUserId());
        response.setFullName(profile.getFullName());
        response.setCollegeName(profile.getCollegeName());
        response.setDegree(profile.getDegree());
        response.setBranch(profile.getBranch());
        response.setGraduationYear(profile.getGraduationYear());
        response.setSkills(profile.getSkills());
        response.setExperience(profile.getExperience());
        response.setProjects(profile.getProjects());
        response.setCertifications(profile.getCertifications());
        response.setGithubUrl(profile.getGithubUrl());
        response.setLinkedinUrl(profile.getLinkedinUrl());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
}
