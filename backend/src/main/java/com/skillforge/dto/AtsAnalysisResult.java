package com.skillforge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtsAnalysisResult {
    private int score;
    private Set<String> matchedCoreSkills;
    private Set<String> missingCoreSkills;
    private List<String> suggestions;
}
