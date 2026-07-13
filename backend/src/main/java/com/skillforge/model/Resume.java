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
@Table(name = "resumes", indexes = {
        // Every resume lookup filters by user_id + deleted together (history, "current
        // resume"), so a composite index serves both instead of two single-column ones.
        @Index(name = "idx_resumes_user_id_deleted", columnList = "user_id, deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    /** Raw text extracted from the uploaded PDF/DOCX, used for ATS scoring and skill extraction. */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 255)
    private String originalFileName;

    /** UUID-based name the file is stored under on disk, to avoid collisions/path traversal. */
    @Column(nullable = false, length = 255)
    private String storedFileName;

    @Column(nullable = false, length = 10)
    private String fileType;

    @Column(nullable = false)
    private long fileSizeBytes;

    private Integer atsScore;

    /** Soft-delete flag. Kept (rather than a hard delete) so past applications can still
     *  reference the resume they were submitted with via the resumes.id foreign key. */
    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "resume_skills",
            joinColumns = @JoinColumn(name = "resume_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
