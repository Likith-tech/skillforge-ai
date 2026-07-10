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
@Table(name = "resume_keywords")
@Getter
@Setter
@NoArgsConstructor
public class ResumeKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analysis_id", nullable = false)
    private Long analysisId;

    @Column(name = "keyword_text", nullable = false)
    private String keywordText;

    @Column(name = "keyword_type", nullable = false)
    private String keywordType;

    @Column(name = "priority_rank", nullable = false)
    private Integer priorityRank;
}