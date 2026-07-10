package com.skillforge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_dashboard_metrics", indexes = {
        @Index(name = "idx_student_dashboard_metrics_student", columnList = "student_id")
})
@Getter
@Setter
@NoArgsConstructor
public class StudentDashboardMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private Long studentId;

    @Column(name = "recommended_jobs_count", nullable = false)
    private Integer recommendedJobsCount;

    @Column(name = "placement_score", nullable = false)
    private Integer placementScore;

    @Column(name = "ats_score", nullable = false)
    private Integer atsScore;

    @Column(name = "skill_gap_count", nullable = false)
    private Integer skillGapCount;

    @Column(name = "applied_jobs_count", nullable = false)
    private Integer appliedJobsCount;

    @Column(name = "resume_status", nullable = false)
    private String resumeStatus;

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