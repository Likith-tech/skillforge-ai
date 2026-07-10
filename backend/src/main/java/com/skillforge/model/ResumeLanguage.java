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
@Table(name = "resume_languages")
@Getter
@Setter
@NoArgsConstructor
public class ResumeLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parsed_resume_id", nullable = false)
    private Long parsedResumeId;

    @Column(name = "language_name")
    private String languageName;

    @Column(name = "proficiency_level")
    private String proficiencyLevel;
}