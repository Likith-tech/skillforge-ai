package com.skillforge.controller;

import com.skillforge.dto.StudentProfileRequest;
import com.skillforge.dto.StudentProfileResponse;
import com.skillforge.service.StudentProfileService;
import java.lang.reflect.Method;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
            Authentication authentication,
            @RequestBody StudentProfileRequest request
    ) {
        return studentProfileService.createProfile(resolveUserId(authentication), request);
    }

    @GetMapping
    public StudentProfileResponse getCurrentProfile(Authentication authentication) {
        return studentProfileService.getCurrentProfile(resolveUserId(authentication));
    }

    @PutMapping
    public StudentProfileResponse updateProfile(
            Authentication authentication,
            @RequestBody StudentProfileRequest request
    ) {
        return studentProfileService.updateProfile(resolveUserId(authentication), request);
    }

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        Object principal = authentication.getPrincipal();
        Long principalId = resolveFromPrincipal(principal);
        if (principalId != null) {
            return principalId;
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException ignored) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User id is missing from authentication");
        }
    }

    private Long resolveFromPrincipal(Object principal) {
        if (principal == null) {
            return null;
        }

        Long claimId = resolveClaim(principal, "userId");
        if (claimId != null) {
            return claimId;
        }

        claimId = resolveClaim(principal, "id");
        if (claimId != null) {
            return claimId;
        }

        try {
            Method getId = principal.getClass().getMethod("getId");
            Object id = getId.invoke(principal);
            return id instanceof Number number ? number.longValue() : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private Long resolveClaim(Object principal, String claimName) {
        try {
            Method getClaim = principal.getClass().getMethod("getClaim", String.class);
            Object claim = getClaim.invoke(principal, claimName);
            if (claim instanceof Number number) {
                return number.longValue();
            }
            if (claim instanceof String value) {
                return Long.valueOf(value);
            }
        } catch (ReflectiveOperationException | NumberFormatException ignored) {
            return null;
        }

        return null;
    }
}
