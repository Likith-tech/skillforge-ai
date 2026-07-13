package com.skillforge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AtsAnalyzeRequest {

    @NotNull(message = "resumeId is required")
    private Long resumeId;

    /**
     * Optional job role to compare the resume against, e.g. "JAVA_DEVELOPER".
     * Omit (or blank) for a generic analysis against a well-rounded baseline.
     */
    private String targetRole;
}
