package com.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeResponse {
    private Long id;
    private Long userId;
    private String originalFileName;
    private String fileType;
    private long fileSizeBytes;
    private Integer atsScore;
    private Set<String> skills;
    private Set<String> missingCoreSkills;
    private List<String> suggestions;
    private LocalDateTime uploadedAt;
}
