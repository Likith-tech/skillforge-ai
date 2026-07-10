package com.skillforge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resume_analyses")
@Getter
@Setter
@NoArgsConstructor
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Column(name = "resume_version_id", nullable = false)
    private Long resumeVersionId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(name = "structure_score", nullable = false)
    private Integer structureScore;

    @Column(name = "formatting_score", nullable = false)
    private Integer formattingScore;

    @Column(name = "skills_score", nullable = false)
    private Integer skillsScore;

    @Column(name = "education_score", nullable = false)
    private Integer educationScore;

    @Column(name = "projects_score", nullable = false)
    private Integer projectsScore;

    @Column(name = "experience_score", nullable = false)
    private Integer experienceScore;

    @Column(name = "keywords_score", nullable = false)
    private Integer keywordsScore;

    @Column(name = "readability_score", nullable = false)
    private Integer readabilityScore;

    @Column(name = "contact_information_score", nullable = false)
    private Integer contactInformationScore;

    @Column(name = "completeness_score", nullable = false)
    private Integer completenessScore;

    @Column(name = "summary_text", columnDefinition = "TEXT")
    private String summaryText;

    @Column(name = "detected_skill_count", nullable = false)
    private Integer detectedSkillCount;

    @Column(name = "technical_skill_count", nullable = false)
    private Integer technicalSkillCount;

    @Column(name = "soft_skill_count", nullable = false)
    private Integer softSkillCount;

    @Column(name = "project_count", nullable = false)
    private Integer projectCount;

    @Column(name = "experience_count", nullable = false)
    private Integer experienceCount;

    @Column(name = "education_count", nullable = false)
    private Integer educationCount;

    @Column(name = "certification_count", nullable = false)
    private Integer certificationCount;

    @Column(name = "language_count", nullable = false)
    private Integer languageCount;

    @Column(name = "keyword_coverage", nullable = false)
    private Integer keywordCoverage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}