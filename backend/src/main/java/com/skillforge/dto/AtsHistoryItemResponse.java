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
public class AtsHistoryItemResponse {
    private Long id;
    private Long resumeId;
    private String resumeFileName;
    private String targetRole;
    private int overallScore;
    private String strengthLabel;
    private LocalDateTime analyzedAt;
}
