package com.skillforge.controller;

import com.skillforge.dto.StudentProfileRequest;
import com.skillforge.dto.StudentProfileResponse;
import com.skillforge.security.UserPrincipal;
import com.skillforge.service.StudentProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student/profile")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentProfileResponse createProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentProfileRequest request
    ) {
        return studentProfileService.createProfile(principal.getId(), request);
    }

    @GetMapping
    public StudentProfileResponse getCurrentProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return studentProfileService.getCurrentProfile(principal.getId());
    }

    @PutMapping
    public StudentProfileResponse updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentProfileRequest request
    ) {
        return studentProfileService.updateProfile(principal.getId(), request);
    }
}
