package com.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String company;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long resumeId;
    private String resumeFileName;
    private String resumeFileType;
    private Integer atsScore;
    private int matchScore;
    private String status;
    private LocalDateTime appliedAt;
}
