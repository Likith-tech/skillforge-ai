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
@Table(name = "resume_achievements")
@Getter
@Setter
@NoArgsConstructor
public class ResumeAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parsed_resume_id", nullable = false)
    private Long parsedResumeId;

    @Column(name = "achievement_title")
    private String achievementTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "achievement_year")
    private Integer achievementYear;
}