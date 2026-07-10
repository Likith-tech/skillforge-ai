package com.skillforge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resume_suggestions")
@Getter
@Setter
@NoArgsConstructor
public class ResumeSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analysis_id", nullable = false)
    private Long analysisId;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "recommendation", nullable = false, columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "expected_ats_improvement", nullable = false)
    private Integer expectedAtsImprovement;

    @Column(name = "severity_rank", nullable = false)
    private Integer severityRank;
}