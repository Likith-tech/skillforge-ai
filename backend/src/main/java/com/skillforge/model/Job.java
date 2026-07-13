package com.skillforge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs", indexes = {
        // Every GET /jobs search filters on status=OPEN first; posted_by backs
        // /jobs/mine and the recruiter dashboard; created_at is the default sort.
        @Index(name = "idx_jobs_status", columnList = "status"),
        @Index(name = "idx_jobs_posted_by", columnList = "posted_by"),
        @Index(name = "idx_jobs_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    // Not @Lob: columnDefinition="TEXT" already gives Postgres an unbounded text column,
    // and @Lob (which maps to JDBC CLOB) breaks Hibernate's lower()/like() criteria
    // functions used by Module 3's keyword search with "argument is of type ... CLOB".
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** Free-text company name, kept for backward compatibility with jobs posted before Module 3.
     *  When the posting recruiter has a Company profile, companyProfile is also linked below. */
    @Column(nullable = false, length = 150)
    private String company;

    /** Nullable: only set once the posting recruiter has created a Company profile (Module 3). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company companyProfile;

    @Column(length = 150)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobType type;

    /** Nullable so pre-Module-3 jobs (created before this column existed) don't break. */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ExperienceLevel experienceLevel;

    @Column(length = 100)
    private String salaryRange;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private JobStatus status = JobStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"),
            // Backs JobSpecifications.requiresSkill's join on skill_id.
            indexes = @Index(name = "idx_job_skills_skill_id", columnList = "skill_id")
    )
    private Set<Skill> requiredSkills = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
