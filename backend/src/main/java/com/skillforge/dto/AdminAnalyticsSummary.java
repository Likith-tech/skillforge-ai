package com.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAnalyticsSummary {
    private long totalUsers;
    private long totalStudents;
    private long totalRecruiters;
    private long totalAdmins;
    private long totalJobs;
    private long totalOpenJobs;
    private long totalApplications;
    private Double averageAtsScore;
}
