package com.skillforge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "roadmap_steps", indexes = {
        @Index(name = "idx_roadmap_steps_roadmap", columnList = "roadmap_id"),
        @Index(name = "idx_roadmap_steps_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class RoadmapStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private LearningRoadmap roadmap;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoadmapSkillLevel level;

    @Column(name = "resource_title", nullable = false)
    private String resourceTitle;

    @Column(name = "resource_url", columnDefinition = "TEXT")
    private String resourceUrl;

    @Column(name = "estimated_duration_hours", nullable = false)
    private Integer estimatedDurationHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoadmapStepStatus status = RoadmapStepStatus.PENDING;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}