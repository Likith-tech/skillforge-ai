package com.skillforge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_job_recommendations", indexes = {
        @Index(name = "idx_student_job_recommendations_student", columnList = "student_id"),
        @Index(name = "idx_student_job_recommendations_job", columnList = "job_id"),
        @Index(name = "idx_student_job_recommendations_analysis", columnList = "resume_analysis_id")
})
@Getter
@Setter
@NoArgsConstructor
public class StudentJobRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "resume_analysis_id", nullable = false)
    private Long resumeAnalysisId;

    @Column(name = "match_percentage", nullable = false)
    private Integer matchPercentage;

    @Column(name = "recommendation_score", nullable = false)
    private Integer recommendationScore;

    @Column(name = "recommendation_confidence", nullable = false)
    private Integer recommendationConfidence;

    @Column(name = "reason_for_recommendation", columnDefinition = "TEXT", nullable = false)
    private String reasonForRecommendation;

    @Column(name = "matching_skills", columnDefinition = "TEXT")
    private String matchingSkills;

    @Column(name = "missing_requirements", columnDefinition = "TEXT")
    private String missingRequirements;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "expected_salary_min")
    private BigDecimal expectedSalaryMin;

    @Column(name = "expected_salary_max")
    private BigDecimal expectedSalaryMax;

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