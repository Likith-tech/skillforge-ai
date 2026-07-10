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
@Table(name = "resume_certifications")
@Getter
@Setter
@NoArgsConstructor
public class ResumeCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parsed_resume_id", nullable = false)
    private Long parsedResumeId;

    @Column(name = "certification_name")
    private String certificationName;

    @Column(name = "issuer_name")
    private String issuerName;

    @Column(name = "issue_year")
    private Integer issueYear;

    @Column(columnDefinition = "TEXT")
    private String description;
}