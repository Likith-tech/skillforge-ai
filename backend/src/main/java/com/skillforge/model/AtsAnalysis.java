package com.skillforge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A single point-in-time ATS report for one resume (Module 2). Append-only:
 * every POST /ats/analyze call - including the one auto-triggered on resume
 * upload - inserts a new row rather than updating a prior one, so this table
 * doubles as the "analysis history" the dashboard/history endpoint reads.
 */
@Entity
@Table(name = "ats_analyses", indexes = {
        @Index(name = "idx_ats_analyses_user_id", columnList = "user_id"),
        @Index(name = "idx_ats_analyses_resume_id", columnList = "resume_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AtsAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    /** Denormalized alongside resume.user so history-by-student doesn't need a join. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Null = generic analysis (not compared against any specific job role). */
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TargetRole targetRole;

    @Column(nullable = false)
    private int overallScore;

    @Column(nullable = false)
    private int formattingScore;

    @Column(nullable = false)
    private int skillsScore;

    @Column(nullable = false)
    private int educationScore;

    @Column(nullable = false)
    private int experienceScore;

    @Column(nullable = false)
    private int projectsScore;

    @Column(nullable = false)
    private int certificationsScore;

    @Column(length = 120)
    private String extractedName;

    @Column(length = 150)
    private String extractedEmail;

    @Column(length = 30)
    private String extractedPhone;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "ats_analysis_suggestions", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "suggestion", length = 300)
    @OrderColumn(name = "position")
    private List<String> suggestions = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "ats_analysis_missing_skills", joinColumns = @JoinColumn(name = "analysis_id"))
    private List<MissingSkillEntry> missingSkills = new ArrayList<>();

    /** Category name -> count of detected skills in that category. */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "ats_analysis_skill_breakdown", joinColumns = @JoinColumn(name = "analysis_id"))
    @MapKeyColumn(name = "category", length = 30)
    @Column(name = "skill_count")
    private Map<String, Integer> skillBreakdown = new HashMap<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
