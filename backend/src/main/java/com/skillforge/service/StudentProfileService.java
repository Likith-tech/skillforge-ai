package com.skillforge.service;

import com.skillforge.dto.StudentProfileRequest;
import com.skillforge.dto.ProfileCompletionResponse;
import com.skillforge.dto.StudentProfileResponse;
import com.skillforge.model.StudentProfile;
import com.skillforge.repository.StudentProfileRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;

    public StudentProfileService(StudentProfileRepository studentProfileRepository) {
        this.studentProfileRepository = studentProfileRepository;
    }

    public StudentProfileResponse createProfile(Long userId, StudentProfileRequest request) {
        validateRequest(request);

        if (studentProfileRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student profile already exists");
        }

        StudentProfile profile = new StudentProfile();
        profile.setUserId(userId);
        applyRequest(profile, request);
        return StudentProfileResponse.from(studentProfileRepository.save(profile));
    }

    public StudentProfileResponse getCurrentProfile(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found"));

        return StudentProfileResponse.from(profile);
    }

    public StudentProfileResponse updateProfile(Long userId, StudentProfileRequest request) {
        validateRequest(request);

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found"));

        applyRequest(profile, request);
        return StudentProfileResponse.from(studentProfileRepository.save(profile));
    }

    public ProfileCompletionResponse getProfileCompletion(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found"));

        List<String> missingFields = new ArrayList<>();
        int totalFields = 11;
        int completedFields = 0;

        completedFields += countIfPresent(profile.getFullName(), "Full name", missingFields);
        completedFields += countIfPresent(profile.getCollegeName(), "College name", missingFields);
        completedFields += countIfPresent(profile.getDegree(), "Degree", missingFields);
        completedFields += countIfPresent(profile.getBranch(), "Branch", missingFields);
        completedFields += countIfPresent(profile.getGraduationYear(), "Graduation year", missingFields);
        completedFields += countIfPresent(profile.getSkills(), "Skills", missingFields);
        completedFields += countIfPresent(profile.getExperience(), "Experience", missingFields);
        completedFields += countIfPresent(profile.getProjects(), "Projects", missingFields);
        completedFields += countIfPresent(profile.getCertifications(), "Certifications", missingFields);
        completedFields += countIfPresent(profile.getGithubUrl(), "GitHub URL", missingFields);
        completedFields += countIfPresent(profile.getLinkedinUrl(), "LinkedIn URL", missingFields);

        int percentage = Math.round((completedFields * 100f) / totalFields);
        return new ProfileCompletionResponse(percentage, missingFields);
    }

    private void validateRequest(StudentProfileRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile data is required");
        }

        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Full name is required");
        }
    }

    private void applyRequest(StudentProfile profile, StudentProfileRequest request) {
        profile.setFullName(request.getFullName().trim());
        profile.setCollegeName(request.getCollegeName());
        profile.setDegree(request.getDegree());
        profile.setBranch(request.getBranch());
        profile.setGraduationYear(request.getGraduationYear());
        profile.setSkills(request.getSkills());
        profile.setExperience(request.getExperience());
        profile.setProjects(request.getProjects());
        profile.setCertifications(request.getCertifications());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setLinkedinUrl(request.getLinkedinUrl());
    }

    private int countIfPresent(String value, String label, List<String> missingFields) {
        if (value != null && !value.trim().isEmpty()) {
            return 1;
        }
        missingFields.add(label);
        return 0;
    }

    private int countIfPresent(Integer value, String label, List<String> missingFields) {
        if (value != null) {
            return 1;
        }
        missingFields.add(label);
        return 0;
    }
}
