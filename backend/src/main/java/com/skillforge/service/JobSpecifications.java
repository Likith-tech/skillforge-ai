package com.skillforge.service;

import com.skillforge.model.ExperienceLevel;
import com.skillforge.model.Job;
import com.skillforge.model.JobStatus;
import com.skillforge.model.JobType;
import com.skillforge.model.Skill;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

/** Composable filter predicates behind GET /jobs's optional search/filter query params. */
final class JobSpecifications {

    private JobSpecifications() {
    }

    static Specification<Job> hasStatus(JobStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    static Specification<Job> keywordMatches(String keyword) {
        String like = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("description")), like)
        );
    }

    static Specification<Job> hasLocation(String location) {
        String like = "%" + location.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("location")), like);
    }

    static Specification<Job> hasType(JobType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    static Specification<Job> hasExperienceLevel(ExperienceLevel level) {
        return (root, query, cb) -> cb.equal(root.get("experienceLevel"), level);
    }

    static Specification<Job> requiresSkill(String skillName) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Job, Skill> skills = root.join("requiredSkills");
            return cb.equal(cb.lower(skills.get("name")), skillName.trim().toLowerCase());
        };
    }

    static Specification<Job> hasCompany(String company) {
        String like = "%" + company.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("company")), like);
    }
}
