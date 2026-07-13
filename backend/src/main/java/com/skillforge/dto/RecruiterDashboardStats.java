package com.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterDashboardStats {
    private long jobsPostedCount;
    private long totalApplications;
    private Map<String, Long> applicationsByStatus;
}
