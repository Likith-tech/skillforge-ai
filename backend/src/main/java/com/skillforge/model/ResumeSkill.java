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
@Table(name = "resume_skills")
@Getter
@Setter
@NoArgsConstructor
public class ResumeSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parsed_resume_id", nullable = false)
    private Long parsedResumeId;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(name = "skill_type", nullable = false)
    private String skillType;

    @Column(name = "source_section")
    private String sourceSection;
}