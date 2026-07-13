package com.skillforge.config;

import com.skillforge.model.Role;
import com.skillforge.model.RoleName;
import com.skillforge.model.RoleSkillRequirement;
import com.skillforge.model.Skill;
import com.skillforge.model.SkillCategory;
import com.skillforge.model.SkillPriority;
import com.skillforge.model.TargetRole;
import com.skillforge.repository.RoleRepository;
import com.skillforge.repository.RoleSkillRequirementRepository;
import com.skillforge.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;
    private final RoleSkillRequirementRepository roleSkillRequirementRepository;

    /** Baseline catalog so ATS scoring and job posting have a usable skill vocabulary out of the box. */
    private static final Map<String, SkillCategory> BASELINE_SKILLS = new LinkedHashMap<>();

    static {
        BASELINE_SKILLS.put("Java", SkillCategory.PROGRAMMING_LANGUAGE);
        BASELINE_SKILLS.put("Python", SkillCategory.PROGRAMMING_LANGUAGE);
        BASELINE_SKILLS.put("JavaScript", SkillCategory.PROGRAMMING_LANGUAGE);
        BASELINE_SKILLS.put("TypeScript", SkillCategory.PROGRAMMING_LANGUAGE);
        BASELINE_SKILLS.put("C++", SkillCategory.PROGRAMMING_LANGUAGE);
        BASELINE_SKILLS.put("C#", SkillCategory.PROGRAMMING_LANGUAGE);
        BASELINE_SKILLS.put("Go", SkillCategory.PROGRAMMING_LANGUAGE);
        BASELINE_SKILLS.put("Kotlin", SkillCategory.PROGRAMMING_LANGUAGE);
        BASELINE_SKILLS.put("Swift", SkillCategory.PROGRAMMING_LANGUAGE);

        BASELINE_SKILLS.put("React", SkillCategory.FRAMEWORK);
        BASELINE_SKILLS.put("Angular", SkillCategory.FRAMEWORK);
        BASELINE_SKILLS.put("Vue", SkillCategory.FRAMEWORK);
        BASELINE_SKILLS.put("Spring Boot", SkillCategory.FRAMEWORK);
        BASELINE_SKILLS.put("Node.js", SkillCategory.FRAMEWORK);
        BASELINE_SKILLS.put("Express", SkillCategory.FRAMEWORK);
        BASELINE_SKILLS.put("REST API", SkillCategory.FRAMEWORK);
        BASELINE_SKILLS.put("GraphQL", SkillCategory.FRAMEWORK);
        BASELINE_SKILLS.put("HTML", SkillCategory.FRAMEWORK);
        BASELINE_SKILLS.put("CSS", SkillCategory.FRAMEWORK);

        BASELINE_SKILLS.put("SQL", SkillCategory.DATABASE);
        BASELINE_SKILLS.put("PostgreSQL", SkillCategory.DATABASE);
        BASELINE_SKILLS.put("MySQL", SkillCategory.DATABASE);
        BASELINE_SKILLS.put("MongoDB", SkillCategory.DATABASE);
        BASELINE_SKILLS.put("Redis", SkillCategory.DATABASE);

        BASELINE_SKILLS.put("AWS", SkillCategory.CLOUD);
        BASELINE_SKILLS.put("Azure", SkillCategory.CLOUD);
        BASELINE_SKILLS.put("GCP", SkillCategory.CLOUD);

        BASELINE_SKILLS.put("Docker", SkillCategory.DEVOPS);
        BASELINE_SKILLS.put("Kubernetes", SkillCategory.DEVOPS);
        BASELINE_SKILLS.put("CI/CD", SkillCategory.DEVOPS);
        BASELINE_SKILLS.put("Linux", SkillCategory.DEVOPS);
        BASELINE_SKILLS.put("Git", SkillCategory.DEVOPS);
        BASELINE_SKILLS.put("Agile", SkillCategory.DEVOPS);
        BASELINE_SKILLS.put("Scrum", SkillCategory.DEVOPS);

        BASELINE_SKILLS.put("Communication", SkillCategory.SOFT_SKILL);
        BASELINE_SKILLS.put("Problem Solving", SkillCategory.SOFT_SKILL);
        BASELINE_SKILLS.put("Teamwork", SkillCategory.SOFT_SKILL);
        BASELINE_SKILLS.put("Leadership", SkillCategory.SOFT_SKILL);
        BASELINE_SKILLS.put("Time Management", SkillCategory.SOFT_SKILL);

        BASELINE_SKILLS.put("Machine Learning", SkillCategory.OTHER);
        BASELINE_SKILLS.put("Data Analysis", SkillCategory.OTHER);
    }

    private record RoleRequirementSeed(String skillName, SkillPriority priority) {
    }

    /** Curated, rule-based role -> required-skill mapping that Module 2's role comparison scores against. */
    private static final Map<TargetRole, List<RoleRequirementSeed>> ROLE_REQUIREMENTS = new LinkedHashMap<>();

    static {
        ROLE_REQUIREMENTS.put(TargetRole.JAVA_DEVELOPER, List.of(
                new RoleRequirementSeed("Java", SkillPriority.HIGH),
                new RoleRequirementSeed("Spring Boot", SkillPriority.HIGH),
                new RoleRequirementSeed("SQL", SkillPriority.HIGH),
                new RoleRequirementSeed("REST API", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Git", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Docker", SkillPriority.MEDIUM),
                new RoleRequirementSeed("PostgreSQL", SkillPriority.LOW),
                new RoleRequirementSeed("AWS", SkillPriority.LOW)
        ));
        ROLE_REQUIREMENTS.put(TargetRole.PYTHON_DEVELOPER, List.of(
                new RoleRequirementSeed("Python", SkillPriority.HIGH),
                new RoleRequirementSeed("SQL", SkillPriority.HIGH),
                new RoleRequirementSeed("REST API", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Git", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Data Analysis", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Docker", SkillPriority.LOW),
                new RoleRequirementSeed("AWS", SkillPriority.LOW),
                new RoleRequirementSeed("PostgreSQL", SkillPriority.LOW)
        ));
        ROLE_REQUIREMENTS.put(TargetRole.FRONTEND_DEVELOPER, List.of(
                new RoleRequirementSeed("JavaScript", SkillPriority.HIGH),
                new RoleRequirementSeed("React", SkillPriority.HIGH),
                new RoleRequirementSeed("HTML", SkillPriority.HIGH),
                new RoleRequirementSeed("CSS", SkillPriority.HIGH),
                new RoleRequirementSeed("TypeScript", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Git", SkillPriority.MEDIUM),
                new RoleRequirementSeed("REST API", SkillPriority.LOW),
                new RoleRequirementSeed("GraphQL", SkillPriority.LOW)
        ));
        ROLE_REQUIREMENTS.put(TargetRole.BACKEND_DEVELOPER, List.of(
                new RoleRequirementSeed("SQL", SkillPriority.HIGH),
                new RoleRequirementSeed("REST API", SkillPriority.HIGH),
                new RoleRequirementSeed("Node.js", SkillPriority.HIGH),
                new RoleRequirementSeed("Docker", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Git", SkillPriority.MEDIUM),
                new RoleRequirementSeed("PostgreSQL", SkillPriority.MEDIUM),
                new RoleRequirementSeed("AWS", SkillPriority.LOW),
                new RoleRequirementSeed("Redis", SkillPriority.LOW)
        ));
        ROLE_REQUIREMENTS.put(TargetRole.FULL_STACK_DEVELOPER, List.of(
                new RoleRequirementSeed("JavaScript", SkillPriority.HIGH),
                new RoleRequirementSeed("React", SkillPriority.HIGH),
                new RoleRequirementSeed("Node.js", SkillPriority.HIGH),
                new RoleRequirementSeed("SQL", SkillPriority.MEDIUM),
                new RoleRequirementSeed("REST API", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Git", SkillPriority.MEDIUM),
                new RoleRequirementSeed("HTML", SkillPriority.LOW),
                new RoleRequirementSeed("CSS", SkillPriority.LOW),
                new RoleRequirementSeed("Docker", SkillPriority.LOW)
        ));
        ROLE_REQUIREMENTS.put(TargetRole.AI_ENGINEER, List.of(
                new RoleRequirementSeed("Python", SkillPriority.HIGH),
                new RoleRequirementSeed("Machine Learning", SkillPriority.HIGH),
                new RoleRequirementSeed("Data Analysis", SkillPriority.HIGH),
                new RoleRequirementSeed("SQL", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Git", SkillPriority.MEDIUM),
                new RoleRequirementSeed("AWS", SkillPriority.LOW),
                new RoleRequirementSeed("Docker", SkillPriority.LOW)
        ));
        ROLE_REQUIREMENTS.put(TargetRole.DATA_ANALYST, List.of(
                new RoleRequirementSeed("SQL", SkillPriority.HIGH),
                new RoleRequirementSeed("Data Analysis", SkillPriority.HIGH),
                new RoleRequirementSeed("Python", SkillPriority.MEDIUM),
                new RoleRequirementSeed("Communication", SkillPriority.MEDIUM),
                new RoleRequirementSeed("PostgreSQL", SkillPriority.LOW),
                new RoleRequirementSeed("MySQL", SkillPriority.LOW)
        ));
    }

    @Override
    public void run(String... args) {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(roleName)));
        }

        BASELINE_SKILLS.forEach((name, category) -> {
            Skill skill = skillRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> new Skill(name, category));
            if (skill.getCategory() == null) {
                skill.setCategory(category);
            }
            skillRepository.save(skill);
        });

        ROLE_REQUIREMENTS.forEach((role, requirements) -> {
            if (roleSkillRequirementRepository.existsByRole(role)) {
                return;
            }
            for (RoleRequirementSeed seed : requirements) {
                Skill skill = skillRepository.findByNameIgnoreCase(seed.skillName())
                        .orElseThrow(() -> new IllegalStateException(
                                "Seed skill missing from catalog: " + seed.skillName()));
                roleSkillRequirementRepository.save(RoleSkillRequirement.builder()
                        .role(role)
                        .skill(skill)
                        .priority(seed.priority())
                        .build());
            }
        });
    }
}
