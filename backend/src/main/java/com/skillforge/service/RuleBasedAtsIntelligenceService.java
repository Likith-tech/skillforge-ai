package com.skillforge.service;

import com.skillforge.model.AtsAnalysis;
import com.skillforge.model.MissingSkillEntry;
import com.skillforge.model.Resume;
import com.skillforge.model.RoleSkillRequirement;
import com.skillforge.model.Skill;
import com.skillforge.model.SkillCategory;
import com.skillforge.model.SkillPriority;
import com.skillforge.model.TargetRole;
import com.skillforge.repository.RoleSkillRequirementRepository;
import com.skillforge.util.ResumeSectionExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Rule-based Module 2 scoring engine: parses structure, breaks skills down by
 * category, scores six weighted sub-dimensions, compares against a target
 * role (or a general well-rounded baseline when none is given), and produces
 * suggestions. Same "simple, explainable, swappable later" philosophy as
 * Module 1's RuleBasedAtsScoringService - no ML/LLM call, just heuristics
 * that are easy to read and adjust.
 */
@Service
@RequiredArgsConstructor
public class RuleBasedAtsIntelligenceService implements AtsIntelligenceService {

    /** Weights must sum to 1.0 - see class-level scoring rationale below each sub-score. */
    private static final double SKILLS_WEIGHT = 0.30;
    private static final double EXPERIENCE_WEIGHT = 0.20;
    private static final double PROJECTS_WEIGHT = 0.15;
    private static final double EDUCATION_WEIGHT = 0.15;
    private static final double CERTIFICATIONS_WEIGHT = 0.10;
    private static final double FORMATTING_WEIGHT = 0.10;

    private static final Set<String> GENERAL_TARGET_SKILLS = Set.of(
            "Java", "Python", "JavaScript", "SQL", "React", "Spring Boot",
            "Git", "REST API", "Docker", "AWS", "HTML", "CSS"
    );

    private static final Pattern DEGREE_PATTERN = Pattern.compile(
            "bachelor|master|b\\.?tech|m\\.?tech|b\\.?sc|m\\.?sc|phd|diploma|associate degree",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern CERTIFICATION_KEYWORD_PATTERN = Pattern.compile(
            "aws certified|azure certified|oracle certified|microsoft certified|google (cloud )?certified|"
                    + "comptia|scrum master|pmp|cisco certified",
            Pattern.CASE_INSENSITIVE);

    private final ResumeSectionExtractor sectionExtractor;
    private final RoleSkillRequirementRepository roleSkillRequirementRepository;

    @Override
    public AtsAnalysis analyze(Resume resume, TargetRole targetRole) {
        ResumeSectionExtractor.ParsedResume parsed = sectionExtractor.parse(resume.getContent());

        int formattingScore = scoreFormatting(resume.getContent(), parsed);
        int educationScore = scoreEducation(parsed.getEducationText());
        int experienceScore = scoreExperience(parsed.getExperienceText());
        int projectsScore = scoreProjects(parsed);

        int certificationsScore = scoreCertifications(parsed.getCertificationsText());

        SkillComparison skillComparison = compareSkills(resume.getSkills(), targetRole);

        int overallScore = (int) Math.round(
                skillComparison.score * SKILLS_WEIGHT
                        + experienceScore * EXPERIENCE_WEIGHT
                        + projectsScore * PROJECTS_WEIGHT
                        + educationScore * EDUCATION_WEIGHT
                        + certificationsScore * CERTIFICATIONS_WEIGHT
                        + formattingScore * FORMATTING_WEIGHT
        );

        List<String> suggestions = buildSuggestions(
                resume.getSkills(), parsed, projectsScore, skillComparison, targetRole);

        return AtsAnalysis.builder()
                .resume(resume)
                .user(resume.getUser())
                .targetRole(targetRole)
                .overallScore(clamp(overallScore))
                .formattingScore(formattingScore)
                .skillsScore(skillComparison.score)
                .educationScore(educationScore)
                .experienceScore(experienceScore)
                .projectsScore(projectsScore)
                .certificationsScore(certificationsScore)
                .extractedName(parsed.getName())
                .extractedEmail(parsed.getEmail())
                .extractedPhone(parsed.getPhone())
                .suggestions(suggestions)
                .missingSkills(skillComparison.missingSkills)
                .skillBreakdown(skillBreakdown(resume.getSkills()))
                .build();
    }

    private int scoreFormatting(String content, ResumeSectionExtractor.ParsedResume parsed) {
        String text = content == null ? "" : content;
        int wordCount = text.isBlank() ? 0 : text.trim().split("\\s+").length;

        int score = 0;
        score += parsed.getEmail() != null ? 20 : 0;
        score += parsed.getPhone() != null ? 20 : 0;
        score += isBlank(parsed.getExperienceText()) ? 0 : 15;
        score += isBlank(parsed.getEducationText()) ? 0 : 15;
        score += isBlank(parsed.getSkillsText()) ? 0 : 15;
        score += wordCount < 100 ? 3 : (wordCount <= 1200 ? 15 : 8);
        return clamp(score);
    }

    private int scoreEducation(String educationText) {
        if (isBlank(educationText)) {
            return 0;
        }
        int score = 40;
        score += DEGREE_PATTERN.matcher(educationText).find() ? 30 : 0;
        int entries = sectionExtractor.countEntries(educationText);
        score += entries >= 2 ? 30 : (entries == 1 ? 15 : 0);
        return clamp(score);
    }

    private int scoreExperience(String experienceText) {
        if (isBlank(experienceText)) {
            return 0;
        }
        int score = 30;
        score += sectionExtractor.hasQuantifiedContent(experienceText) ? 30 : 0;
        int entries = sectionExtractor.countEntries(experienceText);
        score += entries >= 3 ? 40 : (entries >= 1 ? 20 : 0);
        return clamp(score);
    }

    private int scoreProjects(ResumeSectionExtractor.ParsedResume parsed) {
        if (isBlank(parsed.getProjectsText())) {
            return parsed.isHasGithubLink() ? 30 : 0;
        }
        int score = 30;
        score += parsed.isHasGithubLink() ? 30 : 0;
        int entries = sectionExtractor.countEntries(parsed.getProjectsText());
        score += entries >= 2 ? 40 : (entries >= 1 ? 20 : 0);
        return clamp(score);
    }

    private int scoreCertifications(String certificationsText) {
        if (isBlank(certificationsText)) {
            return 0;
        }
        int score = 60;
        score += CERTIFICATION_KEYWORD_PATTERN.matcher(certificationsText).find() ? 40 : 0;
        return clamp(score);
    }

    private SkillComparison compareSkills(Set<Skill> resumeSkills, TargetRole targetRole) {
        if (targetRole != null) {
            List<RoleSkillRequirement> requirements = roleSkillRequirementRepository.findByRole(targetRole);
            if (!requirements.isEmpty()) {
                return compareAgainstRole(resumeSkills, requirements);
            }
        }
        return compareAgainstGeneralBaseline(resumeSkills);
    }

    private SkillComparison compareAgainstRole(Set<Skill> resumeSkills, List<RoleSkillRequirement> requirements) {
        int totalWeight = 0;
        int matchedWeight = 0;
        List<MissingSkillEntry> missing = new ArrayList<>();

        for (RoleSkillRequirement req : requirements) {
            int weight = priorityWeight(req.getPriority());
            totalWeight += weight;
            if (resumeSkills.contains(req.getSkill())) {
                matchedWeight += weight;
            } else {
                MissingSkillEntry entry = new MissingSkillEntry();
                entry.setSkillName(req.getSkill().getName());
                entry.setPriority(req.getPriority());
                missing.add(entry);
            }
        }

        missing.sort(Comparator.comparingInt(e -> priorityRank(e.getPriority())));
        int score = totalWeight == 0 ? 0 : clamp((int) Math.round(matchedWeight / (double) totalWeight * 100));
        return new SkillComparison(score, missing);
    }

    private SkillComparison compareAgainstGeneralBaseline(Set<Skill> resumeSkills) {
        Set<String> resumeSkillNames = resumeSkills.stream()
                .map(s -> s.getName().toLowerCase())
                .collect(Collectors.toSet());

        long matched = GENERAL_TARGET_SKILLS.stream()
                .filter(target -> resumeSkillNames.contains(target.toLowerCase()))
                .count();

        List<MissingSkillEntry> missing = GENERAL_TARGET_SKILLS.stream()
                .filter(target -> !resumeSkillNames.contains(target.toLowerCase()))
                .map(target -> {
                    MissingSkillEntry entry = new MissingSkillEntry();
                    entry.setSkillName(target);
                    entry.setPriority(SkillPriority.MEDIUM);
                    return entry;
                })
                .collect(Collectors.toList());

        int score = clamp((int) Math.round(matched / (double) GENERAL_TARGET_SKILLS.size() * 100));
        return new SkillComparison(score, missing);
    }

    private Map<String, Integer> skillBreakdown(Set<Skill> skills) {
        Map<String, Integer> breakdown = new HashMap<>();
        for (Skill skill : skills) {
            String category = skill.getCategory() != null ? skill.getCategory().name() : SkillCategory.OTHER.name();
            breakdown.merge(category, 1, Integer::sum);
        }
        return breakdown;
    }

    private List<String> buildSuggestions(
            Set<Skill> resumeSkills,
            ResumeSectionExtractor.ParsedResume parsed,
            int projectsScore,
            SkillComparison skillComparison,
            TargetRole targetRole
    ) {
        List<String> suggestions = new ArrayList<>();

        if (!sectionExtractor.hasQuantifiedContent(parsed.getExperienceText())) {
            suggestions.add("Add quantified achievements to your experience section (e.g., "
                    + "\"Improved API latency by 30%\" instead of \"Improved API latency\").");
        }
        if (!parsed.isHasGithubLink()) {
            suggestions.add("Include a GitHub (or GitLab) link so recruiters can see your code.");
        }
        if (projectsScore < 60) {
            suggestions.add("Improve your project descriptions - list the tech stack and impact for each one.");
        }
        if (resumeSkills.stream().noneMatch(s -> s.getCategory() == SkillCategory.CLOUD)) {
            suggestions.add("Add cloud skills (e.g., AWS, Azure, GCP) to your skill set.");
        }
        if (resumeSkills.stream().noneMatch(s -> s.getName().equalsIgnoreCase("SQL"))) {
            suggestions.add("Add SQL to your skill set - most ATS systems screen for database query experience.");
        }
        if (resumeSkills.stream().noneMatch(s -> s.getName().equalsIgnoreCase("Docker"))) {
            suggestions.add("Add Docker to your skill set to show containerization experience.");
        }
        if (targetRole != null) {
            List<String> highPriorityMissing = skillComparison.missingSkills.stream()
                    .filter(m -> m.getPriority() == SkillPriority.HIGH)
                    .map(MissingSkillEntry::getSkillName)
                    .collect(Collectors.toList());
            if (!highPriorityMissing.isEmpty()) {
                suggestions.add("For " + formatRoleName(targetRole) + " roles, prioritize learning: "
                        + String.join(", ", highPriorityMissing) + ".");
            }
        }
        if (suggestions.isEmpty()) {
            suggestions.add("Strong resume! Keep it updated as you gain new skills and experience.");
        }
        return suggestions;
    }

    private String formatRoleName(TargetRole role) {
        String[] parts = role.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part.charAt(0)).append(part.substring(1).toLowerCase()).append(' ');
        }
        return sb.toString().trim();
    }

    private int priorityWeight(SkillPriority priority) {
        return switch (priority) {
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    private int priorityRank(SkillPriority priority) {
        return switch (priority) {
            case HIGH -> 0;
            case MEDIUM -> 1;
            case LOW -> 2;
        };
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    private int clamp(int score) {
        return Math.max(0, Math.min(100, score));
    }

    private record SkillComparison(int score, List<MissingSkillEntry> missingSkills) {
    }
}
