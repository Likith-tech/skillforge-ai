package com.skillforge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "placement_scores", indexes = {
        @Index(name = "idx_placement_scores_student", columnList = "student_id"),
        @Index(name = "idx_placement_scores_analysis", columnList = "resume_analysis_id")
})
@Getter
@Setter
@NoArgsConstructor
public class PlacementScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "resume_analysis_id", nullable = false)
    private Long resumeAnalysisId;

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(name = "resume_score", nullable = false)
    private Integer resumeScore;

    @Column(name = "projects_score", nullable = false)
    private Integer projectsScore;

    @Column(name = "coding_score", nullable = false)
    private Integer codingScore;

    @Column(name = "communication_score", nullable = false)
    private Integer communicationScore;

    @Column(name = "experience_score", nullable = false)
    private Integer experienceScore;

    @Column(name = "certifications_score", nullable = false)
    private Integer certificationsScore;

    @Column(name = "education_score", nullable = false)
    private Integer educationScore;

    @Column(name = "recommendation_fit_score", nullable = false)
    private Integer recommendationFitScore;

    @Column(name = "improvement_suggestions", columnDefinition = "TEXT")
    private String improvementSuggestions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}