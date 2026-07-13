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
public class StudentDashboardStats {
    private boolean resumeUploaded;
    private Integer atsScore;
    private long applicationsCount;
    private long openJobsCount;
}
