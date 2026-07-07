package com.skillforge.service;

import com.skillforge.dto.StudentProfileRequest;
import com.skillforge.dto.StudentProfileResponse;
import com.skillforge.model.StudentProfile;
import com.skillforge.repository.StudentProfileRepository;
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
}
