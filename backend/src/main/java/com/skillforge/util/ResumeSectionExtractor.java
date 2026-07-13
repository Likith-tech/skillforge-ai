package com.skillforge.util;

import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Best-effort, rule-based structural parser for resume text: pulls out a
 * contact header (name/email/phone) and splits the body into the sections
 * the ATS engine scores individually (education/experience/projects/
 * certifications). There's no ML/NLP model behind this - it's heading-
 * keyword + regex heuristics, consistent with RuleBasedAtsScoringService's
 * "simple, explainable" approach from Module 1. Good enough to drive scoring
 * and suggestions; not a guarantee of perfect field extraction.
 */
@Component
public class ResumeSectionExtractor {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+?\\d[\\d\\s-]{8,}\\d)");
    private static final Pattern GITHUB_PATTERN = Pattern.compile("github\\.com|gitlab\\.com", Pattern.CASE_INSENSITIVE);
    private static final Pattern NUMBER_OR_PERCENT = Pattern.compile("\\d+%|\\b\\d{2,}\\b");

    private static final Map<String, String[]> SECTION_HEADERS = new LinkedHashMap<>();

    static {
        SECTION_HEADERS.put("EDUCATION", new String[]{"education", "academic background", "academics"});
        SECTION_HEADERS.put("EXPERIENCE", new String[]{
                "experience", "work experience", "professional experience", "employment history", "work history"
        });
        SECTION_HEADERS.put("PROJECTS", new String[]{"projects", "personal projects", "academic projects"});
        SECTION_HEADERS.put("CERTIFICATIONS", new String[]{
                "certifications", "certificates", "licenses & certifications", "licenses and certifications"
        });
        SECTION_HEADERS.put("SKILLS", new String[]{"skills", "technical skills", "skill set", "core competencies"});
    }

    public ParsedResume parse(String resumeText) {
        String text = resumeText == null ? "" : resumeText;
        String[] lines = text.split("\\r?\\n");

        Map<String, StringBuilder> sections = new LinkedHashMap<>();
        for (String key : SECTION_HEADERS.keySet()) {
            sections.put(key, new StringBuilder());
        }

        StringBuilder preamble = new StringBuilder();
        String currentSection = null;

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            String matchedHeader = matchHeader(line);
            if (matchedHeader != null) {
                currentSection = matchedHeader;
                continue;
            }

            if (currentSection != null) {
                sections.get(currentSection).append(line).append('\n');
            } else {
                preamble.append(line).append('\n');
            }
        }

        String preambleText = preamble.toString();
        String fullText = text;

        return ParsedResume.builder()
                .name(guessName(preambleText))
                .email(firstMatch(EMAIL_PATTERN, fullText))
                .phone(firstMatch(PHONE_PATTERN, fullText))
                .hasGithubLink(GITHUB_PATTERN.matcher(fullText).find())
                .educationText(sections.get("EDUCATION").toString())
                .experienceText(sections.get("EXPERIENCE").toString())
                .projectsText(sections.get("PROJECTS").toString())
                .certificationsText(sections.get("CERTIFICATIONS").toString())
                .skillsText(sections.get("SKILLS").toString())
                .build();
    }

    /** True if the given section's body contains a quantified achievement (a number or a percentage). */
    public boolean hasQuantifiedContent(String sectionText) {
        return sectionText != null && NUMBER_OR_PERCENT.matcher(sectionText).find();
    }

    /** Rough count of distinct entries in a section, using blank-ish line density as a proxy for bullet count. */
    public int countEntries(String sectionText) {
        if (sectionText == null || sectionText.isBlank()) {
            return 0;
        }
        return (int) sectionText.lines().filter(l -> !l.isBlank()).count();
    }

    private String matchHeader(String line) {
        if (line.length() > 45) {
            return null;
        }
        String normalized = line.toLowerCase(Locale.ROOT).replace(":", "").trim();
        for (Map.Entry<String, String[]> entry : SECTION_HEADERS.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (normalized.equals(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private String guessName(String preambleText) {
        for (String line : preambleText.split("\\r?\\n")) {
            String candidate = line.trim();
            if (candidate.isEmpty() || candidate.length() > 60) {
                continue;
            }
            if (candidate.contains("@") || PHONE_PATTERN.matcher(candidate).find()) {
                continue;
            }
            return candidate;
        }
        return null;
    }

    private String firstMatch(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group().trim() : null;
    }

    @Getter
    @Builder
    public static class ParsedResume {
        private final String name;
        private final String email;
        private final String phone;
        private final boolean hasGithubLink;
        private final String educationText;
        private final String experienceText;
        private final String projectsText;
        private final String certificationsText;
        private final String skillsText;
    }
}
