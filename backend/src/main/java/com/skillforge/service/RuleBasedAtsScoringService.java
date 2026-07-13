package com.skillforge.service;

import com.skillforge.dto.AtsAnalysisResult;
import com.skillforge.model.Skill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Simple, explainable rule-based scorer: skill coverage against a baseline
 * "expected skills" checklist (60 pts) + resume structure signals (25 pts)
 * + length sanity (15 pts). Good enough for real feedback without needing
 * an ML model; swap in a Python service behind AtsScoringService later if
 * that's ever warranted.
 */
@Service
public class RuleBasedAtsScoringService implements AtsScoringService {

    private static final Set<String> BASELINE_CORE_SKILLS = Set.of(
            "Java", "Python", "JavaScript", "SQL", "React", "Spring Boot",
            "Git", "REST API", "HTML", "CSS", "Communication", "Problem Solving",
            "Teamwork", "AWS", "Docker"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+?\\d[\\d\\s-]{8,}\\d)");

    @Override
    public AtsAnalysisResult analyze(String resumeText, Set<Skill> extractedSkills) {
        String text = resumeText == null ? "" : resumeText;
        String lower = text.toLowerCase();

        Set<String> matchedSkillNames = extractedSkills.stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        Set<String> matchedCore = BASELINE_CORE_SKILLS.stream()
                .filter(core -> matchedSkillNames.stream().anyMatch(core::equalsIgnoreCase))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> missingCore = BASELINE_CORE_SKILLS.stream()
                .filter(core -> !matchedCore.contains(core))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        int skillScore = Math.round((matchedCore.size() / (float) BASELINE_CORE_SKILLS.size()) * 60);

        boolean hasEmail = EMAIL_PATTERN.matcher(text).find();
        boolean hasPhone = PHONE_PATTERN.matcher(text).find();
        boolean hasExperienceSection = lower.contains("experience");
        boolean hasEducationSection = lower.contains("education");
        boolean hasSkillsSection = lower.contains("skills");

        int structureScore = (hasEmail ? 5 : 0) + (hasPhone ? 5 : 0) + (hasExperienceSection ? 5 : 0)
                + (hasEducationSection ? 5 : 0) + (hasSkillsSection ? 5 : 0);

        int wordCount = text.isBlank() ? 0 : text.trim().split("\\s+").length;
        int lengthScore;
        if (wordCount < 100) {
            lengthScore = 3;
        } else if (wordCount <= 1200) {
            lengthScore = 15;
        } else {
            lengthScore = 8;
        }

        int score = skillScore + structureScore + lengthScore;

        List<String> suggestions = new ArrayList<>();
        if (!missingCore.isEmpty()) {
            suggestions.add("Consider highlighting these in-demand skills if you have them: "
                    + String.join(", ", missingCore));
        }
        if (!hasEmail) {
            suggestions.add("Add a professional email address near the top of your resume.");
        }
        if (!hasPhone) {
            suggestions.add("Include a phone number so recruiters can reach you.");
        }
        if (!hasExperienceSection) {
            suggestions.add("Add a dedicated Experience or Work History section.");
        }
        if (!hasEducationSection) {
            suggestions.add("Add an Education section listing your degree(s).");
        }
        if (!hasSkillsSection) {
            suggestions.add("Add a dedicated Skills section to help ATS systems parse your abilities.");
        }
        if (wordCount < 100) {
            suggestions.add("Your resume looks quite short - add more detail about your projects and responsibilities.");
        } else if (wordCount > 1200) {
            suggestions.add("Your resume is quite long - consider trimming it to the most relevant 1-2 pages.");
        }
        if (suggestions.isEmpty()) {
            suggestions.add("Great job! Your resume covers the key ATS basics.");
        }

        return AtsAnalysisResult.builder()
                .score(Math.min(score, 100))
                .matchedCoreSkills(matchedCore)
                .missingCoreSkills(missingCore)
                .suggestions(suggestions)
                .build();
    }
}
