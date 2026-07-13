package com.skillforge.service;

import com.skillforge.model.AtsAnalysis;
import com.skillforge.model.Resume;
import com.skillforge.model.TargetRole;

public interface AtsIntelligenceService {

    /**
     * Runs the full Module 2 analysis (parsing, categorized skill breakdown,
     * sub-scores, role comparison, suggestions) for a resume. Returns a fully
     * populated but unsaved AtsAnalysis - callers decide if/when to persist it.
     */
    AtsAnalysis analyze(Resume resume, TargetRole targetRole);
}
