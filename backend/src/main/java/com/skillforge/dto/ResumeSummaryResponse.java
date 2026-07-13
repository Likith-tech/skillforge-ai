package com.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Lightweight row shape for resume history lists - no extracted text/skills/suggestions. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeSummaryResponse {
    private Long id;
    private String originalFileName;
    private String fileType;
    private long fileSizeBytes;
    private Integer atsScore;
    private boolean current;
    private LocalDateTime uploadedAt;
}
