package com.skillforge.controller;

import com.skillforge.dto.AdminAnalyticsSummary;
import com.skillforge.dto.RecruiterDashboardStats;
import com.skillforge.dto.StudentDashboardStats;
import com.skillforge.security.SecurityUser;
import com.skillforge.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentDashboardStats> studentStats(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(dashboardService.getStudentStats(principal.getUser().getId()));
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<RecruiterDashboardStats> recruiterStats(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(dashboardService.getRecruiterStats(principal.getUser().getId()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminAnalyticsSummary> adminSummary() {
        return ResponseEntity.ok(dashboardService.getAdminSummary());
    }
}
