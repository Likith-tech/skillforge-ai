package com.skillforge.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeComparisonResponse {

    private Long leftVersionId;
    private Long rightVersionId;
    private Integer leftScore;
    private Integer rightScore;
    private Integer scoreDelta;
    private List<String> improvements;
    private List<String> regressions;
    private List<String> sharedHighlights;
    private List<String> changedFields;
}