package com.skillforge.service;

import com.skillforge.model.Skill;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Matches resume text against the known Skill catalog. Word-only skill names
 * (java, react, sql) use word-boundary matching to avoid matching inside
 * unrelated words; names with punctuation (C++, Node.js) fall back to a
 * plain substring check since \b doesn't apply cleanly to them.
 */
@Service
public class SkillExtractionService {

    private static final Pattern WORD_ONLY = Pattern.compile("[a-z0-9 ]+");

    public Set<Skill> extractSkills(String resumeText, List<Skill> catalog) {
        String normalized = resumeText == null ? "" : resumeText.toLowerCase();
        Set<Skill> found = new HashSet<>();

        for (Skill skill : catalog) {
            String name = skill.getName().toLowerCase();
            if (matches(normalized, name)) {
                found.add(skill);
            }
        }
        return found;
    }

    private boolean matches(String text, String skillName) {
        if (WORD_ONLY.matcher(skillName).matches()) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skillName) + "\\b");
            return pattern.matcher(text).find();
        }
        return text.contains(skillName);
    }
}
