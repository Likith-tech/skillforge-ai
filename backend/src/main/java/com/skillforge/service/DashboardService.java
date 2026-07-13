package com.skillforge.service;

import com.skillforge.dto.AdminAnalyticsSummary;
import com.skillforge.dto.RecruiterDashboardStats;
import com.skillforge.dto.StudentDashboardStats;

public interface DashboardService {

    StudentDashboardStats getStudentStats(Long studentId);

    RecruiterDashboardStats getRecruiterStats(Long recruiterId);

    AdminAnalyticsSummary getAdminSummary();
}
