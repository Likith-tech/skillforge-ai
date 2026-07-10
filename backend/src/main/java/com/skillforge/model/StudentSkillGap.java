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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_skill_gap", indexes = {
        @Index(name = "idx_student_skill_gap_student", columnList = "student_id"),
        @Index(name = "idx_student_skill_gap_job", columnList = "job_id")
})
@Getter
@Setter
@NoArgsConstructor
public class StudentSkillGap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recommendation_id", nullable = false)
    private StudentJobRecommendation recommendation;

    @Column(name = "matched_skills", columnDefinition = "TEXT")
    private String matchedSkills;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(name = "partially_matching_skills", columnDefinition = "TEXT")
    private String partiallyMatchingSkills;

    @Column(name = "recommended_skills", columnDefinition = "TEXT")
    private String recommendedSkills;

    @Column(name = "priority_score", nullable = false)
    private Integer priorityScore;

    @Column(nullable = false)
    private String difficulty;

    @Column(name = "estimated_learning_time_hours", nullable = false)
    private Integer estimatedLearningTimeHours;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}