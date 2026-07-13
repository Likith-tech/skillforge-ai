package com.skillforge.service;

import com.skillforge.dto.AtsAnalysisResult;
import com.skillforge.model.Skill;

import java.util.Set;

public interface AtsScoringService {

    /**
     * Scores a resume 0-100. Implementations may be swapped for a
     * Python/ML-backed service later - callers only depend on this interface.
     */
    AtsAnalysisResult analyze(String resumeText, Set<Skill> extractedSkills);
}
